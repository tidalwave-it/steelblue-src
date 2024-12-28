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
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import it.tidalwave.role.ui.UserAction;
import it.tidalwave.ui.javafx.JavaFXBinder;
import it.tidalwave.ui.core.spi.MenuBarModelSupport;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/***************************************************************************************************************************************************************
 *
 * The JavaFX implementation for {@link it.tidalwave.ui.core.MenuBarModel}.
 *
 * @since   1.1-ALPHA-6
 * @author  Fabrizio Giudici
 *
 **************************************************************************************************************************************************************/
@NoArgsConstructor @Slf4j
public class JavaFXMenuBarModel extends MenuBarModelSupport<JavaFXBinder, MenuBar, Menu, MenuItem>
  {
    /***********************************************************************************************************************************************************
     * {@inheritDoc}
     **********************************************************************************************************************************************************/
    protected void populateImpl (@Nonnull final JavaFXBinder binder, @Nonnull final MenuBar menuBar)
      {
        menuBar.useSystemMenuBarProperty().set(true); // FIXME: only if macOS?
        super.populateImpl(binder, menuBar);
      }

    /***********************************************************************************************************************************************************
     * {@inheritDoc}
     **********************************************************************************************************************************************************/
    @Nonnull
    protected Menu createMenu (@Nonnull final String label)
      {
        return new Menu(label);
      }

    /***********************************************************************************************************************************************************
     * {@inheritDoc}
     **********************************************************************************************************************************************************/
    protected void addMenuToMenuBar (@Nonnull final MenuBar menuBar, @Nonnull final Menu menu)
      {
        menuBar.getMenus().add(menu);
      }

    /***********************************************************************************************************************************************************
     * {@inheritDoc}
     **********************************************************************************************************************************************************/
    @Override
    protected void addMenuItemToMenu (@Nonnull final Menu menu, @Nonnull final JavaFXBinder binder, @Nonnull final UserAction action)
      {
        final var menuItem = new MenuItem();
        binder.bind(menuItem, action);
        menu.getItems().add(menuItem);
      }
  }
