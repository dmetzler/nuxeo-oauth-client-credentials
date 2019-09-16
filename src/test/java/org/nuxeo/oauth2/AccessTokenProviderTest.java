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
package org.nuxeo.oauth2;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.inject.Inject;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.oauth2.service.AccessTokenProvider;
import org.nuxeo.oauth2.service.OAuth2Exception;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.test.runner.RuntimeFeature;

@RunWith(FeaturesRunner.class)
@Features(RuntimeFeature.class)
@Deploy("org.nuxeo.oauth2.nuxeo-oauth-client-credentials")
@Deploy("org.nuxeo.oauth2.nuxeo-oauth-client-credentials:oauth2-contrib.xml")
public class AccessTokenProviderTest {

    @Inject
    AccessTokenProvider atp;

    @Rule
    public Oauth2MockServer server = new Oauth2MockServer();

    @Test
    public void can_generate_access_token() throws Exception {
        byte[] accessToken = atp.getToken("myIdp");
        assertNotNull(accessToken);
        assertTrue(accessToken.length > 0);
    }

    @Test(expected = OAuth2Exception.class)
    public void can_not_have_token_for_unexisting_idp_configuration() throws Exception {
        atp.getToken("otherIdp");

    }
}
