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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;
import it.tidalwave.role.ui.UserAction;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.testng.annotations.Test;
import static it.tidalwave.role.ui.Displayable._Displayable_;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class BinderMock
  {
  }

@Getter @RequiredArgsConstructor
class MenuBarMock
  {
    @Nonnull
    private final List<MenuMock> menus = new ArrayList<>();
  }

@Getter @RequiredArgsConstructor
class MenuMock
  {
    @Nonnull
    private final String text;

    @Nonnull
    private final List<MenuItemMock> items = new ArrayList<>();
  }

@Getter @RequiredArgsConstructor
class MenuItemMock
  {
    @Nonnull
    private final String text;
  }

class UnderTest2 extends MenuBarControlSupport<BinderMock, MenuBarMock, MenuMock>
  {
    public UnderTest2 (@Nonnull final Supplier<Collection<? extends UserAction>> userActionsSupplier)
      {
        super(userActionsSupplier);
      }

    @Override @Nonnull
    protected MenuMock createMenu (@Nonnull final String label)
      {
        return new MenuMock(label);
      }

    @Override
    protected void addMenuToMenuBar (@Nonnull final MenuBarMock menuBar, @Nonnull final MenuMock menu)
      {
        menuBar.getMenus().add(menu);
      }

    @Override
    protected void addMenuItemToMenu (@Nonnull final MenuMock menu, @Nonnull final BinderMock binder, @Nonnull final UserAction action)
      {
        final var menuItem = new MenuItemMock(action.as(_Displayable_).getDisplayName());
        menu.getItems().add(menuItem);
      }
  }

/***************************************************************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **************************************************************************************************************************************************************/
public class MenuBarControlSupportTest
  {
    private final TestUserActions a = new TestUserActions();

    @Test
    public void test_populate()
      {
        // given
        final var underTest = new UnderTest2(() -> List.of(a.actionFileOpen, a.actionFileClose, a.actionFileCloseAll, a.actionEditUndo,
                                                 a.actionEditRedo, a.actionSelectSelectAll, a.actionSelectDeselect, a.actionNoMenuBar));
        final var control = new MenuBarMock();
        final var binder = new BinderMock();
        // when
        underTest.populate(binder, control);
        // then
        final var menus = control.getMenus();
        assertThat(menus.size(), is(3));
        final var menuFile = menus.get(0);
        final var menuEdit = menus.get(1);
        final var menuSelect = menus.get(2);

        final var menuFileItems = menuFile.getItems();
        assertThat(menuFileItems.size(), is(3));
        final var menuFileOpen = menuFileItems.get(0);
        final var menuFileClose = menuFileItems.get(1);
        final var menuFileCloseAll = menuFileItems.get(2);

        final var menuEditItems = menuEdit.getItems();
        assertThat(menuEditItems.size(), is(2));
        final var menuEditUndo = menuEditItems.get(0);
        final var menuEditRedo = menuEditItems.get(1);

        final var menuSelectItems = menuSelect.getItems();
        assertThat(menuSelectItems.size(), is(2));
        final var menuSelectSelectAll = menuSelectItems.get(0);
        final var menuSelectDeselect = menuSelectItems.get(1);

        assertThat(menuFile.getText(), is("File"));
        assertThat(menuFileOpen.getText(), is("Open"));
        assertThat(menuFileClose.getText(), is("Close"));
        assertThat(menuFileCloseAll.getText(), is("Close all"));

        assertThat(menuEdit.getText(), is("Edit"));
        assertThat(menuEditUndo.getText(), is("Undo"));
        assertThat(menuEditRedo.getText(), is("Redo"));

        assertThat(menuSelect.getText(), is("Select"));
        assertThat(menuSelectSelectAll.getText(), is("Select all"));
        assertThat(menuSelectDeselect.getText(), is("Deselect"));
        // actionNoMenuBar correctly did not appear anywhere
      }
  }
