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
import java.util.List;
import java.util.function.Supplier;
import it.tidalwave.util.As;
import it.tidalwave.ui.core.ToolBarControl;
import it.tidalwave.ui.core.role.UserAction;
import it.tidalwave.ui.core.role.UserActionProvider;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import lombok.extern.slf4j.Slf4j;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.*;
import static it.tidalwave.ui.core.role.UserActionProvider._UserActionProvider_;

/***************************************************************************************************************************************************************
 *
 * A support implementation for {@link ToolBarControl}.
 *
 * @param   <B>               the concrete type of the binder
 * @param   <T>               the concrete type of the toolbar
 * @param   <BT>              the concrete type of the button
 * @since   1.1-ALPHA-6
 * @author  Fabrizio Giudici
 *
 **************************************************************************************************************************************************************/
@RequiredArgsConstructor(access = AccessLevel.PROTECTED) @Slf4j
public abstract class ToolBarControlSupport<B, T, BT> implements ToolBarControl<B, T>
  {
    @Delegate
    private final As as = As.forObject(this);

    /** The default supplier of {@link UserAction}s, can be injected for testing. */
    @Nonnull
    protected final Supplier<Collection<? extends UserAction>> userActionsSupplier;

    /***********************************************************************************************************************************************************
     * Default constructor.
     **********************************************************************************************************************************************************/
    protected ToolBarControlSupport ()
      {
        userActionsSupplier = () -> maybeAs(_UserActionProvider_).map(UserActionProvider::getActions).orElse(emptyList());
      }

    /***********************************************************************************************************************************************************
     * Populates the menu bar with menus.
     * @param   binder    the binder
     * @param   toolBar   the toolbar
     **********************************************************************************************************************************************************/
    public void populate (@Nonnull final B binder, @Nonnull final T toolBar)
      {
        final var actions = userActionsSupplier.get();
        log.info("Toolbar user actions: {}", actions);
        final var buttons = actions.stream()
                                   .map((action) -> createButton(binder, action))
                                   .collect(toList());
        addButtonsToToolBar(toolBar, buttons);
      }

    /***********************************************************************************************************************************************************
     * Creates a button bound to the given {@link UserAction}.
     *
     * @param   binder    the binder
     * @param   action    the user action
     * @return            the button
     **********************************************************************************************************************************************************/
    @Nonnull
    protected abstract BT createButton (@Nonnull B binder, @Nonnull UserAction action);

    /***********************************************************************************************************************************************************
     * Adds buttons to the toolbar.
     * @param   toolBar   the toolbar
     * @param   buttons   the button
     **********************************************************************************************************************************************************/
    protected abstract void addButtonsToToolBar (@Nonnull T toolBar, @Nonnull List<BT> buttons);
  }