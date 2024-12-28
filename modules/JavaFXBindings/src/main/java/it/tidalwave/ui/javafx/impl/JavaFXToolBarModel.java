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
package it.tidalwave.ui.javafx.impl;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.function.Supplier;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import it.tidalwave.util.annotation.VisibleForTesting;
import it.tidalwave.ui.core.spi.ToolBarModelSupport;
import it.tidalwave.role.ui.UserAction;
import it.tidalwave.ui.javafx.JavaFXBinder;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/***************************************************************************************************************************************************************
 *
 * The JavaFX implementation for {@link it.tidalwave.ui.core.ToolBarModel}.
 *
 * @author  Fabrizio Giudici
 * @since   1.1-ALPHA-4
 *
 **************************************************************************************************************************************************************/
@NoArgsConstructor @Slf4j
public class JavaFXToolBarModel extends ToolBarModelSupport<JavaFXBinder, ToolBar>
  {
    /***********************************************************************************************************************************************************
     * @param   userActionsSupplier   the supplier of actions
     **********************************************************************************************************************************************************/
    @VisibleForTesting JavaFXToolBarModel (@Nonnull final Supplier<Collection<? extends UserAction>> userActionsSupplier)
      {
        super(userActionsSupplier);
      }

    /***********************************************************************************************************************************************************
     * {@inheritDoc}
     **********************************************************************************************************************************************************/
    @Override
    public void populateImpl (@Nonnull final JavaFXBinder binder, @Nonnull final ToolBar toolBar)
      {
        final var actions = userActionsSupplier.get();
        log.info("Toolbar user actions: {}", actions);
        final var buttons = actions.stream()
                                   .map((action) -> createButton(binder, action))
                                   .toArray(Button[]::new);
        toolBar.getItems().addAll(buttons);
      }

    /***********************************************************************************************************************************************************
     * Creates a {@link Button} bound to the given {@link UserAction}.
     *
     * @param   binder    the binder
     * @param   action    the user action
     * @return            the button
     **********************************************************************************************************************************************************/
    @Nonnull
    private static Button createButton (@Nonnull final JavaFXBinder binder, @Nonnull final UserAction action)
      {
        final var button = new Button();
        binder.bind(button, action);
        return button;
      }
  }
