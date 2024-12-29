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
package it.tidalwave.ui.core;

import javax.annotation.Nonnull;

/***************************************************************************************************************************************************************
 *
 * A model for the application toolbar.
 *
 * @param   <B>               the concrete type of the binder
 * @param   <T>               the concrete type of the toolbar
 * @author  Fabrizio Giudici
 *
 **************************************************************************************************************************************************************/
public interface ToolBarControl<B, T>
  {
    /***********************************************************************************************************************************************************
     * Populates the toolbar with buttons.
     *
     * @param   binder    the binder
     * @param   toolBar   the toolbar
     **********************************************************************************************************************************************************/
    public void populate (@Nonnull B binder, @Nonnull T toolBar);
  }