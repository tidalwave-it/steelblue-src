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

import jakarta.annotation.Nonnull;
import java.beans.PropertyChangeSupport;
import java.util.IdentityHashMap;
import java.util.Map;
import it.tidalwave.ui.core.MutableListener;
import it.tidalwave.util.annotation.VisibleForTesting;
import lombok.RequiredArgsConstructor;

/***************************************************************************************************************************************************************
 *
 * @author      Fabrizio Giudici
 *
 **************************************************************************************************************************************************************/
@RequiredArgsConstructor
public class MutableListenerSupport
  {
    @Nonnull
    private final PropertyChangeSupport pcs;

    @VisibleForTesting final Map<MutableListener<?>, MutableListenerAdapter<?>> adapterMap = new IdentityHashMap<>();

    /***********************************************************************************************************************************************************
     * Registers a {@link MutableListener}.
     * @param   listener        the listener
     **********************************************************************************************************************************************************/
    public <T> void addListener (@Nonnull final MutableListener<T> listener)
      {
        final var adapter = new MutableListenerAdapter<>(listener);
        adapterMap.put(listener, adapter);
        pcs.addPropertyChangeListener(adapter);
      }

    /***********************************************************************************************************************************************************
     * Registers a {@link MutableListener}.
     * @param   listener        the listener
     * @param   propertyName    the property name to which the listener will be associated
     **********************************************************************************************************************************************************/
    public <T> void addListener (@Nonnull final String propertyName, @Nonnull final MutableListener<T> listener)
      {
        final var adapter = new MutableListenerAdapter<>(listener);
        adapterMap.put(listener, adapter);
        pcs.addPropertyChangeListener(propertyName, adapter);
      }

    /***********************************************************************************************************************************************************
     * Unregisters a {@link MutableListener}.
     * @param   listener        the listener
     **********************************************************************************************************************************************************/
    public <T> void removeListener (@Nonnull final MutableListener<T> listener)
      {
        final var adapter = adapterMap.remove(listener);

        if (adapter != null)
          {
            pcs.removePropertyChangeListener(adapter);
          }
      }

    /***********************************************************************************************************************************************************
     * Unregisters a {@link MutableListener}.
     * @param   listener        the listener
     * @param   propertyName    the property name to which the listener will be associated
     **********************************************************************************************************************************************************/
    public <T> void removeListener (@Nonnull final String propertyName, @Nonnull final MutableListener<T> listener)
      {
        final var adapter = adapterMap.remove(listener);

        if (adapter != null)
          {
            pcs.removePropertyChangeListener(propertyName, adapter);
          }
      }

    /***********************************************************************************************************************************************************
     * Unregisters all the {@link MutableListener}s.
     **********************************************************************************************************************************************************/
    public void removeAllListeners()
      {
        adapterMap.clear();
      }

    public <T> void fireChange (final T oldValue, final T newValue)
      {
        pcs.firePropertyChange("", oldValue, newValue);
      }

    public <T> void fireChange (@Nonnull final String propertyName, final T oldValue, final T newValue)
      {
        pcs.firePropertyChange(propertyName, oldValue, newValue);
      }
  }
