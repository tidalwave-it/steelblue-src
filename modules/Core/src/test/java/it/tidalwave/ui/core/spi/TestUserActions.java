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
package it.tidalwave.ui.core.spi;

import javax.annotation.Nonnull;
import java.util.List;
import it.tidalwave.ui.core.MenuBarControl;
import it.tidalwave.util.Callback;
import it.tidalwave.ui.core.role.Displayable;
import it.tidalwave.ui.core.role.UserAction;
import static org.mockito.Mockito.*;

/***************************************************************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **************************************************************************************************************************************************************/
public class TestUserActions
  {
    @Nonnull
    public final UserAction actionFileOpen;

    @Nonnull
    public final UserAction actionFileClose;

    @Nonnull
    public final UserAction actionFileCloseAll;

    @Nonnull
    public final UserAction actionEditUndo;

    @Nonnull
    public final UserAction actionEditRedo;

    @Nonnull
    public final UserAction actionSelectSelectAll;

    @Nonnull
    public final UserAction actionSelectDeselect;

    @Nonnull
    public final UserAction actionNoMenuBar;

    public TestUserActions()
      {
        actionFileOpen = createAction("Open", "File");
        actionFileClose = createAction("Close", "File");
        actionFileCloseAll = createAction("Close all", "File");
        actionEditUndo = createAction("Undo", "Edit");
        actionEditRedo = createAction("Redo", "Edit");
        actionSelectSelectAll = createAction("Select all", "Select");
        actionSelectDeselect = createAction("Deselect", "Select");
        actionNoMenuBar = UserAction.of(mock(Callback.class), List.of(Displayable.of("foo bar")));
      }

    @Nonnull
    private static UserAction createAction (@Nonnull final String displayName, @Nonnull final String path)
      {
        return UserAction.of(mock(Callback.class), List.of(Displayable.of(displayName), MenuBarControl.MenuPlacement.under(path)));
      }
  }
