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
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import it.tidalwave.util.As;
import it.tidalwave.util.Pair;
import it.tidalwave.ui.core.MenuBarControl;
import it.tidalwave.role.ui.UserAction;
import it.tidalwave.role.ui.UserActionProvider;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import lombok.extern.slf4j.Slf4j;
import static java.util.Collections.emptyList;
import static it.tidalwave.ui.core.MenuBarControl.MenuPlacement._MenuItemPlacement_;
import static it.tidalwave.role.ui.UserActionProvider._UserActionProvider_;

/***************************************************************************************************************************************************************
 *
 * A support implementation for {@link MenuBarControl}.
 *
 * @param   <B>               the type of the binder
 * @param   <MB>              the type of the menubar
 * @param   <M>               the type of the menu
 * @since   1.1-ALPHA-6
 * @author  Fabrizio Giudici
 *
 **************************************************************************************************************************************************************/
@RequiredArgsConstructor(access = AccessLevel.PROTECTED) @Slf4j
public abstract class MenuBarControlSupport<B, MB, M> implements MenuBarControl<B, MB>
  {
    @Delegate
    private final As as = As.forObject(this);

    /** The default supplier of {@link UserAction}s, can be injected for testing. */
    @Nonnull
    protected final Supplier<Collection<? extends UserAction>> userActionsSupplier;

    /***********************************************************************************************************************************************************
     * Default constructor.
     **********************************************************************************************************************************************************/
    protected MenuBarControlSupport ()
      {
        userActionsSupplier = () -> maybeAs(_UserActionProvider_).map(UserActionProvider::getActions).orElse(emptyList());
      }

    /***********************************************************************************************************************************************************
     * {@inheritDoc}
     **********************************************************************************************************************************************************/
    @Override
    public void populate (@Nonnull final B binder, @Nonnull final MB menuBar)
      {
        final var menuMapByLabel = new HashMap<String, M>();
        final var actions = userActionsSupplier.get();
        log.info("Menu bar user actions: {}", actions);
        actions.stream().map(a -> Pair.of(a, a.maybeAs(_MenuItemPlacement_)))
                        .filter(p -> p.b.isPresent())
                        .forEach(p ->
          {
            final var menuPath = p.b.map(MenuPlacement::getPath).orElseThrow();
            final var menu = menuMapByLabel.computeIfAbsent(menuPath, this::createMenu);
            log.debug("Binding {} to menu item {}", p.a, menuPath);
            addMenuItemToMenu(menu, binder, p.a);
          });

        menuMapByLabel.entrySet().stream().sorted(menuComparator()).forEach(e -> addMenuToMenuBar(menuBar, e.getValue()));
      }

    /***********************************************************************************************************************************************************
     * {@return a new menu with the given label}.
     * @param   label     the label
     **********************************************************************************************************************************************************/
    @Nonnull
    protected abstract M createMenu (@Nonnull final String label);

    /***********************************************************************************************************************************************************
     * Adds a menu to the menu bar.
     * @param   menuBar   the menu bar
     * @param   menu      the menu
     **********************************************************************************************************************************************************/
    protected abstract void addMenuToMenuBar (@Nonnull final MB menuBar, @Nonnull final M menu);

    /***********************************************************************************************************************************************************
     * Adds to the given menu a new item bound to the given {@link UserAction}.
     * @param   menu      the menu
     * @param   binder    the binder
     * @param   action    the user action
     **********************************************************************************************************************************************************/
    protected abstract void addMenuItemToMenu (@Nonnull final M menu, @Nonnull final B binder, @Nonnull final UserAction action);

    /***********************************************************************************************************************************************************
     * {@return a {@link Comparator } for menus}
     **********************************************************************************************************************************************************/
    @Nonnull
    private final Comparator<Map.Entry<String, ?>> menuComparator()
      {
        return (e1, e2) ->
          {
            final var i1 = MenuIndex.findPosition(e1.getKey());
            final var i2 = MenuIndex.findPosition(e2.getKey());
            return (i1 >= 0 && i2 >= 0) ? i1 - i2 : e1.getKey().compareTo(e2.getKey());
          };
      }
  }
