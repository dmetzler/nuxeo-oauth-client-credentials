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

import java.util.concurrent.Callable;

/**
 * Small object that allows to lazy load an instance of T.
 */
public class LazyProvider<T> {

    private T instance;

    private Callable<T> callable;

    public LazyProvider(Callable<T> callable) {
        this.callable = callable;
    }

    public T get() {
        if (instance == null) {
            try {
                instance = callable.call();
            } catch (Exception e) {
                return null;
            }
        }
        return instance;
    }

    public void set(T instance) {
        this.instance = instance;

    }

}
