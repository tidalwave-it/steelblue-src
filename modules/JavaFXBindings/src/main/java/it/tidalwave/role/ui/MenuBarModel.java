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
package it.tidalwave.role.ui;

import javax.annotation.Nonnull;
import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/***************************************************************************************************************************************************************
 *
 * A model for the application menubar.
 *
 * @since   1.1-ALPHA-6
 * @author  Fabrizio Giudici
 *
 **************************************************************************************************************************************************************/
public interface MenuBarModel
  {
    /** A class describing the standard sequence of typical main menu bar elements. */
    @RequiredArgsConstructor @Getter
    public enum MenuIndex
      {
        FILE("File", 0),
        EDIT("Edit", 2),
        SELECT("Select", 3),
        HELP("Help", 999);

        /** {@return the position of the menu with the given label}.
         *  @param label  the label */
        public static int findPosition (@Nonnull final String label)
          {
            return Arrays.stream(values()).filter(i -> i.getLabel().equals(label)).findFirst().map(MenuIndex::getIndex).orElse(-1);
          }

        @Nonnull
        private final String label;
        private final int index;
      }

    /***********************************************************************************************************************************************************
     *
     * A role that describes the placement of a menu item.
     *
     * @stereotype  Role
     * @since       1.1-ALPHA-6
     * @author      Fabrizio Giudici
     *
     **********************************************************************************************************************************************************/
    @RequiredArgsConstructor(staticName = "under") @Getter
    public static class MenuPlacement
      {
        public static final Class<MenuPlacement> _MenuItemPlacement_ = MenuPlacement.class;

        @Nonnull
        private String path;
      }

    /***********************************************************************************************************************************************************
     * Populates the menu bar with menus.
     * @param   binder    the binder
     * @param   menuBar   the menu bar
     **********************************************************************************************************************************************************/
    public void populate (@Nonnull Object binder, @Nonnull Object menuBar);
  }
