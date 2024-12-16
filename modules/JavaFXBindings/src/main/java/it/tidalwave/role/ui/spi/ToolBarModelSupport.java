/*
 * *********************************************************************************************************************
 *
 * SteelBlue: DCI User Interfaces
 * http://tidalwave.it/projects/steelblue
 *
 * Copyright (C) 2015 - 2024 by Tidalwave s.a.s. (http://tidalwave.it)
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
package it.tidalwave.role.ui.spi;

import javax.annotation.Nonnull;
import java.util.stream.Stream;
import it.tidalwave.util.As;
import it.tidalwave.role.ui.ToolBarModel;
import it.tidalwave.role.ui.UserAction;
import lombok.experimental.Delegate;
import static it.tidalwave.role.ui.UserActionProvider._UserActionProvider_;

/***********************************************************************************************************************
 *
 * A support implementation for {@link ToolBarModel}.
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
public abstract class ToolBarModelSupport implements ToolBarModel
  {
    @Delegate
    private final As as = As.forObject(this);

    /*******************************************************************************************************************
     *
     * Finds the {@link UserAction} instances to be bound to toolbar buttons.
     *
     * @return  the user actions
     *
     ******************************************************************************************************************/
    @Nonnull
    protected Stream<? extends UserAction> findToolBarUserActions()
      {
        return maybeAs(_UserActionProvider_).map(uap -> uap.getActions().stream()).orElse(Stream.empty());
      }
  }
