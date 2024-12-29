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
package it.tidalwave.ui.core.role.spi;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Collections;
import javax.swing.Action;
import it.tidalwave.util.NotFoundException;
import it.tidalwave.ui.core.role.ActionProvider;

/***************************************************************************************************************************************************************
 *
 * @stereotype Role
 *
 * @since   2.0-ALPHA-1
 * @author  Fabrizio Giudici
 *
 **************************************************************************************************************************************************************/
public class ActionProviderSupport implements ActionProvider
  {
    @Override @Nonnull
    public Collection<? extends Action> getActions()
      {
        return Collections.emptyList();
      }

    @Override @Nonnull
    public Action getDefaultAction()
      throws NotFoundException
      {
        throw new NotFoundException("No default action");
      }
  }
