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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.concurrent.Callable;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class LazyProviderTest {

    public static interface Dummy {

    }

    @Mock
    Callable<Dummy> callable;

    @Before
    public void doBefore() throws Exception {
        when(callable.call()).thenReturn(new Dummy() {
        });
    }

    @Test
    public void instance_is_only_created_once() throws Exception {
        // Given a Lazy Provider
        LazyProvider<Dummy> lp = new LazyProvider<>(callable);

        // When I call it twice
        verify(callable, never()).call();
        Dummy dummy1 = lp.get();
        Dummy dummy2 = lp.get();

        // Then the underlying construction happened only once
        verify(callable, times(1)).call();
        assertThat(dummy1).isSameAs(dummy2);

    }

    @Test
    public void instance_can_be_set_externally() throws Exception {
        // Given a Lazy Provider
        LazyProvider<Dummy> lp = new LazyProvider<>(callable);

        // When I set the instance to a new value
        verify(callable, never()).call();
        Dummy dummy1 = lp.get();
        lp.set(new Dummy() {
        });
        Dummy dummy2 = lp.get();

        // Then the underlying construction happened only once
        // but I get a different instance
        verify(callable, times(1)).call();
        assertThat(dummy1).isNotSameAs(dummy2);
    }

}
