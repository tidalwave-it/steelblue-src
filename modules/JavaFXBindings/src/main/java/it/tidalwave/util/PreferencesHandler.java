/*
 * *********************************************************************************************************************
 *
 * SteelBlue: DCI User Interfaces
 * http://tidalwave.it/projects/steelblue
 *
 * Copyright (C) 2015 - 2021 by Tidalwave s.a.s. (http://tidalwave.it)
 *
 * *********************************************************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * *********************************************************************************************************************
 *
 * git clone https://bitbucket.org/tidalwave/steelblue-src
 * git clone https://github.com/tidalwave-it/steelblue-src
 *
 * *********************************************************************************************************************
 */
package it.tidalwave.util;

import javax.annotation.Nonnull;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import it.tidalwave.util.impl.DefaultPreferencesHandler;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
public interface PreferencesHandler
  {
    public static final String PROP_APP_NAME = PreferencesHandler.class.getPackage().getName() + ".appName";

    // FIXME: make private as soon as the right Java version is required
    public static final String __BASE_NAME = "it.tidalwave.javafx";
    public static AtomicReference<PreferencesHandler> __INSTANCE = new AtomicReference<>();

    /** A property representing the initial main window size as a percentual of the screen size. */
    public static final Key<Double> KEY_INITIAL_SIZE = Key.of(__BASE_NAME + ".initialSize", Double.class);

    /** Whether the application should start at full screen. */
    public static final Key<Boolean> KEY_FULL_SCREEN = Key.of(__BASE_NAME + ".fullScreen", Boolean.class);

    @Nonnull
    public Path getAppFolder();

    @Nonnull
    public Path getLogFolder();

    /*******************************************************************************************************************
     *
     * Sets the application name. This method must be called at boot from the {@code main} method before doing
     * anything else.
     *
     * @param name    the property name
     *
     ******************************************************************************************************************/
    public static void setAppName (@Nonnull final String name)
      {
        System.setProperty(PROP_APP_NAME, name);
      }

    /*******************************************************************************************************************
     *
     * Gets a property.
     *
     * @param <T>     the property type
     * @param name    the property name
     * @return        the property value
     *
     ******************************************************************************************************************/
    @Nonnull
    public <T> Optional<T> getProperty (@Nonnull Key<T> name);

    /*******************************************************************************************************************
     *
     * Sets a property, overriding the current value.
     *
     * @param <T>     the property type
     * @param name    the property name
     * @param value   the property value
     * @return        the property value
     *
     ******************************************************************************************************************/
    public <T> void setProperty (@Nonnull final Key<T> name, @Nonnull final T value);

    /**
     *
     * Sets a property, unless it has been already set.
     *
     * @param <T>     the property type
     * @param name    the property name
     * @param value   the property value
     * @return        the property value
     *
     ******************************************************************************************************************/
    public <T> void setDefaultProperty (@Nonnull final Key<T> name, @Nonnull final T value);

    /*******************************************************************************************************************
     *
     * main() probably needs it and dI has not booted yet, so this class can be accessed also by this factory
     * method. Note that Spring instantiates the bean by calling this method, so we really have a singleton.
     *
     ******************************************************************************************************************/
    @Nonnull
    public static PreferencesHandler getInstance()
      {
        synchronized (PreferencesHandler.class)
          {
            if (__INSTANCE.get() == null)
              {
                __INSTANCE.set(new DefaultPreferencesHandler());
              }

            return __INSTANCE.get();
          }
      }
  }
