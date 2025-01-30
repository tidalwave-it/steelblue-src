/*
 * *************************************************************************************************************************************************************
 *
 * SteelBlue: DCI User Interfaces
 * http://tidalwave.it/projects/steelblue
 *
 * Copyright (C) 2015 - 2025 by Tidalwave s.a.s. (http://tidalwave.it)
 *
 * *************************************************************************************************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 *
 * *************************************************************************************************************************************************************
 *
 * git clone https://bitbucket.org/tidalwave/steelblue-src
 * git clone https://github.com/tidalwave-it/steelblue-src
 *
 * *************************************************************************************************************************************************************
 */
package it.tidalwave.ui.core;

import java.beans.PropertyChangeSupport;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import it.tidalwave.ui.core.role.impl.MutableListenerSupport;
import lombok.experimental.Delegate;
import org.testng.annotations.Test;
import static org.mockito.Mockito.*;

/***************************************************************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **************************************************************************************************************************************************************/
public class MutableTest
  {
    private static class MutableHelper implements Mutable
      {
        @Delegate
        private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

        @Delegate
        private final MutableListenerSupport mls = new MutableListenerSupport(pcs);
      }

    @FunctionalInterface
    static interface TriConsumer<T, S, U>
      {
        public void accept (T t, S s, U u);
      }

    @Test @SuppressWarnings("unchecked")
    public void test_listeners()
      {
        // given
        final var mutable = new MutableHelper();
        final var consumer1 = mock(Consumer.class);
        final var consumer2 = mock(BiConsumer.class);
        final var consumer3 = mock(TriConsumer.class);
        mutable.addListener((source, oldValue, newValue) -> consumer3.accept(source, oldValue, newValue));
        mutable.addListener2((oldValue, newValue) -> consumer2.accept(oldValue, newValue));
        mutable.addListener1(newValue -> consumer1.accept(newValue));
        // when
        mutable.firePropertyChange("property name", "old value", "new value");
        // then
        verify(consumer1).accept("new value");
        verify(consumer2).accept("old value", "new value");
        verify(consumer3).accept(same(mutable), eq("old value"), eq("new value"));
      }
  }