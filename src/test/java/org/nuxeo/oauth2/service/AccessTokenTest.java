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

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

public class AccessTokenTest {

    private String tokenAsString;

    @Before
    public void doBefore() throws Exception {
        tokenAsString = IOUtils.toString(
                this.getClass().getClassLoader().getResource("oauth-token-response.json").openStream(),
                StandardCharsets.UTF_8.name());
    }

    @Test
    public void can_parse_a_token() throws Exception {
        AccessToken token = AccessToken.createFrom(tokenAsString);

        assertThat(token).isNotNull();
        assertThat(new String(token.getTokenValue())).isEqualTo("atoken");
        assertThat(token.getType()).isEqualTo("Bearer");
        assertThat(token.getExpiresAt()).isAfter(LocalDateTime.now().plusMinutes(50));
        assertThat(token.isExpired()).isFalse();
    }

    @Test
    public void a_token_can_expire() throws Exception {

        // Set expires in 0s
        AccessToken token = AccessToken.createFrom(tokenAsString.replace("3600", "0"));

        await().atMost(200, MILLISECONDS).until(() -> token.isExpired());

        assertThat(token.isExpired()).isTrue();

    }
}
