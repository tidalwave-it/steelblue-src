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
package it.tidalwave.ui.example.presentation.impl;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;
import it.tidalwave.ui.core.MenuBarControl;
import it.tidalwave.ui.core.ToolBarControl;
import it.tidalwave.role.ui.UserAction;
import it.tidalwave.role.ui.spi.DefaultUserActionProvider;
import it.tidalwave.dci.annotation.DciRole;
import lombok.RequiredArgsConstructor;

/***************************************************************************************************************************************************************
 *
 * This class declares which actions should be bound to toolbar buttons.
 *
 * @author  Fabrizio Giudici
 *
 **************************************************************************************************************************************************************/
// START SNIPPET: all
@RequiredArgsConstructor @DciRole(datumType = {ToolBarControl.class, MenuBarControl.class})
public class MainPanelUserActionProvider extends DefaultUserActionProvider
  {
    @Nonnull
    private final DefaultMainPanelPresentationControl pc;

    @Override @Nonnull
    public Collection<? extends UserAction> getActions()
      {
        return List.of(pc.getActionButton(), pc.getActionDialogOk(), pc.getActionDialogCancelOk(), pc.getActionPickFile(), pc.getActionPickDirectory());
      }
  }
// END SNIPPET: all