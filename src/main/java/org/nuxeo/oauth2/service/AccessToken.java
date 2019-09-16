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
import java.time.LocalDateTime;

import org.apache.commons.io.IOUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Helper object that allows to parse the result of an authorize request. The result expected is like that:
 *
 * <pre>
 * {
 *   "access_token":"eap.......",
 *   "expires_in": 3600,
 *   "token_type": "Bearer"
 * }
 * </pre>
 */
public class AccessToken {

    protected byte[] tokenValue;

    protected LocalDateTime expiresAt;

    protected String type;

    protected AccessToken() {

    }

    public static AccessToken createFrom(String tokenAsString) throws IOException {
        return createFrom(IOUtils.toInputStream(tokenAsString, Charset.defaultCharset()));
    }

    public static AccessToken createFrom(InputStream is) throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        JsonNode json = mapper.readTree(is);
        AccessToken token = new AccessToken();

        token.tokenValue = json.get("access_token").asText().getBytes();
        int expiresIn = json.get("expires_in").asInt();
        token.expiresAt = LocalDateTime.now().plusSeconds(expiresIn);
        token.type = json.get("token_type").asText();
        return token;
    }

    public byte[] getTokenValue() {
        return tokenValue;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public String getType() {
        return type;
    }

    public Boolean isExpired() {
        return expiresAt.isBefore(LocalDateTime.now());
    }

}
