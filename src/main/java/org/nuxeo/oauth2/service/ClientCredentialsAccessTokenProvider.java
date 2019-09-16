/*
 * (C) Copyright 2013-2019 Nuxeo (http://nuxeo.com/) and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     dmetzler
 */
package org.nuxeo.oauth2.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.nuxeo.oauth2.IdpDescriptor;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.services.event.Event;
import org.nuxeo.runtime.services.event.EventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.jodah.failsafe.Failsafe;
import net.jodah.failsafe.RetryPolicy;

public class ClientCredentialsAccessTokenProvider implements AccessTokenProvider {

    private static final Logger log = LoggerFactory.getLogger(ClientCredentialsAccessTokenProvider.class);

    // Service dependencies
    private Map<String, IdpDescriptor> descriptors;

    private EventService eventService;

    private RetryPolicy retryPolicy;

    // Internal storage of tokens
    Map<String, AccessToken> tokens = new ConcurrentHashMap<>();

    @Override
    public byte[] getToken(String idpId) throws OAuth2Exception {
        if (descriptors.containsKey(idpId)) {
            eventService.sendEvent(new Event("accessTokenTopic", idpId, this, null));
            return getAccessToken(idpId);
        } else {
            throw new OAuth2Exception("Did not find IDP");
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        ClientCredentialsAccessTokenProvider provider;

        public Builder() {
            provider = new ClientCredentialsAccessTokenProvider();
            if (Framework.isInitialized()) {
                provider.eventService = Framework.getService(EventService.class);
            }
            provider.retryPolicy = new RetryPolicy().withBackoff(5, 60, TimeUnit.SECONDS).withMaxRetries(5);
        }

        public Builder descriptors(List<IdpDescriptor> descriptors) {
            provider.descriptors = descriptors.stream()
                                              .collect(Collectors.toMap(IdpDescriptor::getId, Function.identity()));
            return this;
        }

        public Builder eventService(EventService eventService) {
            provider.eventService = eventService;
            return this;
        }

        public ClientCredentialsAccessTokenProvider build() {
            if (provider.eventService == null) {
                throw new IllegalArgumentException(
                        "EventService can not be null in ClientCredentialsAccessTokenProviderF");
            }
            return provider;
        }
    }

    private ClientCredentialsAccessTokenProvider() {
    }

    public byte[] getAccessToken(String idp) {
        AccessToken currentToken = tokens.get(idp);
        if (currentToken == null || currentToken.isExpired()) {
            String authenticationServerUrl = descriptors.get(idp).tokenUrl;
            AccessToken token = Failsafe.with(retryPolicy)//
                                        .onFailedAttempt(t -> log.warn(
                                                "Failed attempt at getting an access token from {}. Retrying...",
                                                authenticationServerUrl))
                                        .onFailure(t -> {
                                            throw new OAuth2Exception(
                                                    "Failed at getting an access token from " + authenticationServerUrl,
                                                    t);
                                        })
                                        .get(() -> this.fetchNewToken(descriptors.get(idp)));
            setCurrentToken(idp, token);
        }
        return tokens.get(idp).getTokenValue();

    }

    protected void setCurrentToken(String idp, AccessToken token) {
        tokens.put(idp, token);
    }

    private AccessToken fetchNewToken(IdpDescriptor descriptor) throws IOException {
        try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
            HttpPost post = new HttpPost(descriptor.tokenUrl);

            List<NameValuePair> requestBody = new ArrayList<>();
            requestBody.add(new BasicNameValuePair("grant_type", "client_credentials"));
            requestBody.add(new BasicNameValuePair("client_id", descriptor.clientId));
            requestBody.add(new BasicNameValuePair("client_secret", descriptor.clientSecret));
            requestBody.add(new BasicNameValuePair("scope", descriptor.scope));

            post.setEntity(new UrlEncodedFormEntity(requestBody, Charset.defaultCharset().name()));

            HttpResponse response = client.execute(post);

            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode > 299) {
                throw new IOException("IDP returned error status code: " + statusCode);
            }

            InputStream is = response.getEntity().getContent();
            return AccessToken.createFrom(is);

        }
    }

}
