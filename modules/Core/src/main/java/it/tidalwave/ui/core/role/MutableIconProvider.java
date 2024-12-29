/*
 * *************************************************************************************************************************************************************
 *
 * SteelBlue: DCI User Interfaces
 * http://tidalwave.it/projects/steelblue
 *
 * Copyright (C) 2015 - 2024 by Tidalwave s.a.s. (http://tidalwave.it)
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

import javax.annotation.Nonnull;
import java.beans.PropertyChangeListener;
import javax.swing.Icon;

/***************************************************************************************************************************************************************
 *
 * A specialized {@link it.tidalwave.ui.core.role.IconProvider} which is mutable and fires {@code PropertyChangeEvent}s.
 *
 * @stereotype Role
 *
 * @since   2.0-ALPHA-1
 * @author  Fabrizio Giudici
 * @it.tidalwave.javadoc.draft
 *
 **************************************************************************************************************************************************************/
public interface MutableIconProvider extends IconProvider
  {
    public static final Class<MutableIconProvider> _MutableIconProvider_ = MutableIconProvider.class;

    /** The property name for icon. */
    public static final String PROP_ICON = "icon";

    /***********************************************************************************************************************************************************
     * Sets the icon. Note that implementations don't actually need to do something in this method: for instance,
     * a valid {@code MutableIconProvider} can autonomously change icon in function of time (e.g. a blinking icon) or
     * reacting to a change in the context.
     *
     * @param  icon  the icon
     **********************************************************************************************************************************************************/
    @SuppressWarnings("EmptyMethod")
    public void setIcon (@Nonnull Icon icon);

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
  }
