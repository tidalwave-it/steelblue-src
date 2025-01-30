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
import jakarta.annotation.Nullable;
import org.apiguardian.api.API;
import static org.apiguardian.api.API.Status.EXPERIMENTAL;

/***************************************************************************************************************************************************************
 *
 * A listener of a mutable property.
 *
 * @param   <T>   the type of the property
 * @since         2.0-ALPHA-2
 * @see           MutableListener1
 * @see           MutableListener2
 * @author        Fabrizio Giudici
 *
 **************************************************************************************************************************************************************/
@FunctionalInterface @API(status = EXPERIMENTAL)
public interface MutableListener<T>
  {
    /***********************************************************************************************************************************************************
     * Notifies that a status has changed.
     * @param     source        the originator of the change
     * @param     oldValue      the old value of the status
     * @param     newValue      the new value
     **********************************************************************************************************************************************************/
    public void changed (@Nonnull Object source, @Nullable T oldValue, @Nullable T newValue);
  }
