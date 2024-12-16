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
package it.tidalwave.ui.javafx;

import javax.annotation.Nonnull;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import it.tidalwave.role.ui.spi.ToolBarModelSupport;
import it.tidalwave.role.ui.Displayable;
import it.tidalwave.role.ui.UserAction;
import it.tidalwave.role.ui.javafx.JavaFXBinder;
import static it.tidalwave.role.ui.Displayable._Displayable_;

/***********************************************************************************************************************
 *
 * The JavaFX implementation for {@link it.tidalwave.ui.ToolBarModel}.
 *
 * @author  Fabrizio Giudici
 * @since   1.1-ALPHA-4
 *
 **********************************************************************************************************************/
public class JavaFXToolBarModel extends ToolBarModelSupport
  {
    /*******************************************************************************************************************
     * {@inheritDoc}
     ******************************************************************************************************************/
    @Override
    public void populate (@Nonnull final Object binder, @Nonnull final Object toolBar)
      {
        final var buttons = findToolBarUserActions()
              .map((action) -> createButton((JavaFXBinder)binder, action))
              .toArray(Button[]::new);
        ((ToolBar)toolBar).getItems().addAll(buttons);
      }

    /*******************************************************************************************************************
     *
     * Creates a {@link Button} bound to the given {@link UserAction}.
     *
     * @param   binder    the binder
     * @param   action    the user action
     * @return            the button
     *
     ******************************************************************************************************************/
    @Nonnull
    private static Button createButton (@Nonnull final JavaFXBinder binder, @Nonnull final UserAction action)
      {
        final var button = new Button();
        // FIXME: move to JavaFXBinder
        button.setText(action.maybeAs(_Displayable_).map(Displayable::getDisplayName).orElse("???"));
        binder.bind(button, action);
        return button;
      }
  }
