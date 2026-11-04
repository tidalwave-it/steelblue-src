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
package it.tidalwave.ui.core.role.spi;
import jakarta.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import it.tidalwave.ui.core.role.UserAction;
import it.tidalwave.ui.core.role.UserActionProvider;
import it.tidalwave.util.NotFoundException;
/***************************************************************************************************************************************************************
 *
 * A default implementation of {@link UserActionProvider} which returns no actions.
 *
 * @since   2.0-ALPHA-1
 * @stereotype role
 *
 * @author  Fabrizio Giudici
 *
 **************************************************************************************************************************************************************/
public class DefaultUserActionProvider implements UserActionProvider
  {
    /***********************************************************************************************************************************************************
     * {@inheritDoc}
     **********************************************************************************************************************************************************/
    @Override @Nonnull
    public List<UserAction> getActions()
      {
        return Collections.emptyList();
      }
    /***********************************************************************************************************************************************************
     * {@inheritDoc}
     **********************************************************************************************************************************************************/
    @Override @Nonnull
    public UserAction getDefaultAction()
      throws NotFoundException
      {
        throw new NotFoundException("No default UserAction");
      }
  }
