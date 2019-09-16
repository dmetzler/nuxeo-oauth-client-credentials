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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.nuxeo.oauth2.service.ClientCredentialsAccessTokenProvider.builder;

import java.util.Collections;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.nuxeo.oauth2.IdpDescriptor;
import org.nuxeo.oauth2.Oauth2MockServer;
import org.nuxeo.runtime.services.event.Event;
import org.nuxeo.runtime.services.event.EventService;

@RunWith(MockitoJUnitRunner.class)
public class ClientCredentialsAccessTokenProviderTest {

    @Mock
    EventService eventService;

    @Rule
    public Oauth2MockServer server = new Oauth2MockServer();

    @Test
    public void event_service_is_called_when_access_token() throws Exception {
        // Given our token provider
        IdpDescriptor descriptor = getDummyIdpDescriptor("myIdp");
        ClientCredentialsAccessTokenProvider ccatp = builder().descriptors(Collections.singletonList(descriptor))
                                                              .eventService(eventService)
                                                              .build();

        // When I call the getToken API
        ccatp.getToken("myIdp");

        // Then it generate an event;
        ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
        verify(eventService).sendEvent(eventCaptor.capture());
        assertThat(eventCaptor.getValue().getSource()).isSameAs(ccatp);
    }

    private IdpDescriptor getDummyIdpDescriptor(String id) {
        IdpDescriptor descriptor = new IdpDescriptor(id, server.url("/oauth2/token").toString(), "clientId",
                "clientSecret", "scope");
        return descriptor;
    }
}
