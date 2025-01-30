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
package it.tidalwave.ui.core.role.impl;

import java.beans.PropertyChangeSupport;
import it.tidalwave.ui.core.MutableListener;
import org.testng.annotations.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/***************************************************************************************************************************************************************
 *
 * @author      Fabrizio Giudici
 *
 **************************************************************************************************************************************************************/
public class MutableListenerSupportTest
  {
    @Test @SuppressWarnings("unchecked")
    public void test_named_property_listener()
      {
        // given
        final var source = new Object();
        final var pcs = new PropertyChangeSupport(source);
        final var underTest = new MutableListenerSupport(pcs);
        final var propertyName = "theProperty";
        final var listener = mock(MutableListener.class);
        final var anotherListener = mock(MutableListener.class);
        underTest.addListener(propertyName, listener);
        underTest.addListener("anotherProperty", anotherListener);
        // when
        underTest.fireChange(propertyName, "old", "new");
        // then
        verify(listener).changed(same(source), eq("old"),  eq("new"));
        verifyNoInteractions(anotherListener);
      }

    /**********************************************************************************************************************************************************/
    @Test @SuppressWarnings("unchecked")
    public void test_generic_listener()
      {
        // given
        final var source = new Object();
        final var pcs = new PropertyChangeSupport(source);
        final var underTest = new MutableListenerSupport(pcs);
        final var listener = mock(MutableListener.class);
        underTest.addListener(listener);
        // when
        underTest.fireChange("old", "new");
        // then
        verify(listener).changed(same(source), eq("old"),  eq("new"));
        assertThat(underTest.adapterMap).containsKey(listener);
      }

    /**********************************************************************************************************************************************************/
    @Test @SuppressWarnings("unchecked")
    public void test_remove_listener()
      {
        // given
        final var source = new Object();
        final var pcs = new PropertyChangeSupport(source);
        final var underTest = new MutableListenerSupport(pcs);
        final var mutableListener = mock(MutableListener.class);
        underTest.addListener(mutableListener);
        // when
        underTest.removeListener(mutableListener);
        underTest.fireChange("old", "new");
        // then
        verifyNoInteractions(mutableListener);
        assertThat(underTest.adapterMap).doesNotContainKey(mutableListener);
      }
  }