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

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import okhttp3.HttpUrl;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

public class Oauth2MockServer implements TestRule {

    private MockWebServer server;

    @Override
    public Statement apply(Statement base, Description description) {
        return new Statement() {

            @Override
            public void evaluate() throws Throwable {
                try {
                    server = new MockWebServer();
                    server.setDispatcher(buildDispatcher());
                    server.start(8888);
                    base.evaluate();
                } finally {
                    server.shutdown();
                    server.close();

                }
            }

        };

    }

    private Dispatcher buildDispatcher() throws IOException {
        String tokenAsString = IOUtils.toString(
                this.getClass().getClassLoader().getResource("oauth-token-response.json").openStream(),
                StandardCharsets.UTF_8.name());

        final Dispatcher dispatcher = new Dispatcher() {

            @Override
            public MockResponse dispatch(RecordedRequest request) throws InterruptedException {

                switch (request.getPath()) {
                case "/oauth2/token":
                    return new MockResponse().setResponseCode(200).setBody(tokenAsString);
                }
                return new MockResponse().setResponseCode(404);
            }
        };
        return dispatcher;
    }

    public HttpUrl url(String path) {
        return server.url(path);
    }

}
