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

import org.nuxeo.common.xmap.annotation.XNode;
import org.nuxeo.common.xmap.annotation.XObject;
import org.nuxeo.runtime.model.Descriptor;

@XObject("oauth2")
public class IdpDescriptor implements Descriptor {

    @XNode("@id")
    public String id;

    @XNode("tokenUrl")
    public String tokenUrl;

    @XNode("clientId")
    public String clientId;

    @XNode("clientSecret")
    public String clientSecret;

    @XNode("scope")
    public String scope;

    @Override
    public String getId() {
        return id;
    }

    public IdpDescriptor() {

    }

    public IdpDescriptor(String id, String tokenUrl, String clientId, String clientSecret, String scope) {
        this.id = id;
        this.tokenUrl = tokenUrl;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.scope = scope;

    }
}