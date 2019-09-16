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

import static org.nuxeo.oauth2.service.ClientCredentialsAccessTokenProvider.builder;

import org.nuxeo.oauth2.service.AccessTokenProvider;
import org.nuxeo.runtime.model.DefaultComponent;

public class OAuth2Component extends DefaultComponent {

    private LazyProvider<AccessTokenProvider> atp = new LazyProvider<>(this::buildAccessTokenProvider);

    @Override
    public <T> T getAdapter(Class<T> adapter) {
        if (AccessTokenProvider.class.equals(adapter)) {
            return (T) atp.get();
        }
        return super.getAdapter(adapter);
    }

    private AccessTokenProvider buildAccessTokenProvider() {
        return builder().descriptors(getDescriptors("idp")).build();
    }

}
