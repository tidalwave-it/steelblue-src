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

import jakarta.annotation.Nonnull;
import java.beans.PropertyChangeListener;
import org.apiguardian.api.API;
import static org.apiguardian.api.API.Status.EXPERIMENTAL;

/***************************************************************************************************************************************************************
 *
 * An interface that describes the capability of mutate status and of supporting listeners. Both old-style {@link PropertyChangeListener} and a new
 * experimental {@link MutableListener}s are supported. The three different methods {@link #addListener1(MutableListener1)},
 * {@link #addListener2(MutableListener2)} and {@link #addListener(MutableListener)} allow shorter syntaxes when only one or two parameters
 * of the callback are needed, that is:
 *
 * <pre>
 * {@code
 * mutable.addMutableListener1(newValue -> consumer1.accept(newValue));
 * mutable.addMutableListener2((oldValue, newValue) -> consumer2.accept(oldValue, newValue));
 * mutable.addMutableListener((source, oldValue, newValue) -> consumer3.accept(source, oldValue, newValue));
 * }
 * </pre>
 *
 * @see           MutableListener
 * @see           MutableListener1
 * @see           MutableListener2
 * @since         2.0-ALPHA-2
 * @author        Fabrizio Giudici
 *
 **************************************************************************************************************************************************************/
@API(status = EXPERIMENTAL)
public interface Mutable
  {
    /***********************************************************************************************************************************************************
     * Registers a {@link PropertyChangeListener}.
     * @param  listener   the listener
     **********************************************************************************************************************************************************/
    public void addPropertyChangeListener (@Nonnull PropertyChangeListener listener);

    /***********************************************************************************************************************************************************
     * Registers a {@link PropertyChangeListener} for the given property.
     * @param propertyName  the name of the property
     * @param listener      the listener
     **********************************************************************************************************************************************************/
    public void addPropertyChangeListener (@Nonnull String propertyName, @Nonnull PropertyChangeListener listener);

    /***********************************************************************************************************************************************************
     * Unregisters a {@link PropertyChangeListener}.
     * @param  listener   the listener
     **********************************************************************************************************************************************************/
    public void removePropertyChangeListener (@Nonnull PropertyChangeListener listener);

    /***********************************************************************************************************************************************************
     * Removes a {@link PropertyChangeListener} for the given property.
     * @param propertyName  the name of the property
     * @param listener      the listener
     **********************************************************************************************************************************************************/
    public void removePropertyChangeListener (@Nonnull String propertyName, @Nonnull PropertyChangeListener listener);

    /***********************************************************************************************************************************************************
     * Returns all the bound {@link PropertyChangeListener}s.
     * @return              the listeners
     **********************************************************************************************************************************************************/
    @Nonnull
    public PropertyChangeListener[] getPropertyChangeListeners();

    /***********************************************************************************************************************************************************
     * Returns the bound {@link PropertyChangeListener}s for the given property.
     * @param propertyName  the name of the property
     * @return              the listeners
     **********************************************************************************************************************************************************/
    @Nonnull
    public PropertyChangeListener[] getPropertyChangeListeners (@Nonnull String propertyName);

    /***********************************************************************************************************************************************************
     * Checks whether the given property has been bound to listeners.
     * @param propertyName  the name of the property
     * @return              {@code true} if the property is bound
     **********************************************************************************************************************************************************/
    public boolean hasListeners (@Nonnull String propertyName);

    /***********************************************************************************************************************************************************
     * Registers a {@link MutableListener}.
     * @param  <T>        the type of the listener
     * @param  listener   the listener
     **********************************************************************************************************************************************************/
    public <T> void addListener (@Nonnull final MutableListener<T> listener);

    /***********************************************************************************************************************************************************
     * Registers a {@link MutableListener}. This method is needed to allow the compiler to infer the correct type of lambdas.
     * @param  <T>        the type of the listener
     * @param  listener   the listener
     **********************************************************************************************************************************************************/
    public default <T> void addListener1 (@Nonnull final MutableListener1<T> listener)
      {
        addListener(listener);
      }

    /***********************************************************************************************************************************************************
     * Registers a {@link MutableListener}. This method is needed to allow the compiler to infer the correct type of lambdas.
     * @param  <T>        the type of the listener
     * @param  listener   the listener
     **********************************************************************************************************************************************************/
    public default <T> void addListener2 (@Nonnull final MutableListener2<T> listener)
      {
        addListener(listener);
      }

    /***********************************************************************************************************************************************************
     * Unregisters a {@link MutableListener}.
     * @param  <T>        the type of the listener
     * @param  listener   the listener
     **********************************************************************************************************************************************************/
    public <T> void removeListener (@Nonnull final MutableListener<T> listener);
  }
