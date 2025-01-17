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
package it.tidalwave.ui.core.role;

import jakarta.annotation.Nonnull;
import java.beans.PropertyChangeListener;
import java.util.Locale;
import java.util.Map;
import it.tidalwave.ui.core.role.impl.DefaultMutableDisplayable;

/***************************************************************************************************************************************************************
 *
 * A specialized {@link it.tidalwave.ui.core.role.Displayable} which is mutable (that is, its display name can be changed)
 * and fires {@code PropertyChangeEvent}s.
 *
 * @stereotype Role
 *
 * @since   2.0-ALPHA-1
 * @author  Fabrizio Giudici
 * @it.tidalwave.javadoc.stable
 *
 **************************************************************************************************************************************************************/
public interface MutableDisplayable extends Displayable
  {
    public static final Class<MutableDisplayable> _MutableDisplayable_ = MutableDisplayable.class;

    /** The property name for displayName */
    public static final String PROP_DISPLAY_NAME = "displayName";

    /** The property name for displayNames */
    public static final String PROP_DISPLAY_NAMES = "displayNames";

    /***********************************************************************************************************************************************************
     * Sets the display name in {@code Locale.ENGLISH}.
     *
     * @param  displayName  the display name
     **********************************************************************************************************************************************************/
    public void setDisplayName (@Nonnull String displayName);

    /***********************************************************************************************************************************************************
     * Sets the display name in the given {@code Locale}.
     *
     * @param  displayName  the display name
     * @param  locale       the locale
     **********************************************************************************************************************************************************/
    public void setDisplayName (@Nonnull String displayName, @Nonnull Locale locale);

    /***********************************************************************************************************************************************************
     * Sets a bag of display names for a number of {@code Locale}s.
     *
     * @param  displayNames  the display names
     **********************************************************************************************************************************************************/
    public void setDisplayNames (@Nonnull Map<Locale, String> displayNames);

    /***********************************************************************************************************************************************************
     * Registers a {@link PropertyChangeListener}.
     *
     * @param  listener   the listener
     **********************************************************************************************************************************************************/
    public void addPropertyChangeListener (@Nonnull PropertyChangeListener listener);

    /***********************************************************************************************************************************************************
     * Unregisters a {@link PropertyChangeListener}.
     *
     * @param  listener   the listener
     **********************************************************************************************************************************************************/
    public void removePropertyChangeListener (@Nonnull PropertyChangeListener listener);

    /***********************************************************************************************************************************************************
     * Creates an instance with an initial given display name in {@code Locale.ENGLISH}.
     *
     * @param  displayName    the display name
     * @return                the new instance
     * @since                 3.2-ALPHA-1
     **********************************************************************************************************************************************************/
    @Nonnull
    public static MutableDisplayable of (@Nonnull final String displayName)
      {
        return of(displayName, "???");
      }

    /***********************************************************************************************************************************************************
     * Creates an instance with an initial given display name in {@code Locale.ENGLISH} and an explicit identifier for
     * {@code toString()}.
     *
     * @param  displayName    the display name
     * @param  toStringName   the name to be rendered when {@code toString()} is called
     * @return                the new instance
     * @since                 3.2-ALPHA-1
     **********************************************************************************************************************************************************/
    @Nonnull
    public static MutableDisplayable of (@Nonnull final String displayName, @Nonnull final String toStringName)
      {
        return new DefaultMutableDisplayable(displayName, toStringName);
      }
  }
