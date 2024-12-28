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
package it.tidalwave.ui.javafx.impl.common;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.Executor;
import javafx.collections.ObservableList;
import javafx.scene.control.Cell;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import it.tidalwave.ui.javafx.role.CustomGraphicProvider;
import it.tidalwave.util.As;
import it.tidalwave.util.annotation.VisibleForTesting;
import it.tidalwave.role.ui.Displayable;
import it.tidalwave.role.ui.UserAction;
import it.tidalwave.role.ui.UserActionProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import static it.tidalwave.ui.javafx.role.CustomGraphicProvider._CustomGraphicProvider_;
import static java.util.stream.Collectors.*;
import static it.tidalwave.role.ui.Displayable._Displayable_;
import static it.tidalwave.role.ui.Styleable._Styleable_;
import static it.tidalwave.role.ui.UserActionProvider._UserActionProvider_;

/***************************************************************************************************************************************************************
 *
 * An implementation of {@link CellBinder} that extracts information from a {@link UserActionProvider}.
 *
 * @author  Fabrizio Giudici
 *
 **************************************************************************************************************************************************************/
@RequiredArgsConstructor @Slf4j
public class DefaultCellBinder implements CellBinder
  {
    /** Roles to preload, so they are computed in the background thread. */
    private static final List<Class<?>> PRELOADING_ROLE_TYPES = List.of(
            _Displayable_, _UserActionProvider_, _Styleable_, _CustomGraphicProvider_);

    private static final String ROLE_STYLE_PREFIX = "-rs-";

    @Nonnull
    private final Executor executor;

    /***********************************************************************************************************************************************************
     * {@inheritDoc}
     **********************************************************************************************************************************************************/
    @Override
    public void bind (@Nonnull final Cell<?> cell, @Nullable final As item, final boolean empty)
      {
        log.trace("bind({}, {}, {})", cell, item, empty);
        clearBindings(cell);

        if (!empty && (item != null))
          {
            JavaFXWorker.run(executor,
                             () -> new RoleBag(item, PRELOADING_ROLE_TYPES),
                             roles -> bindAll(cell, roles));
          }
      }

    /***********************************************************************************************************************************************************
     * Binds everything provided by the given {@link RoleBag} to the given {@link Cell}.
     *
     * @param     cell            the {@code Cell}
     * @param     roles           the role bag
     **********************************************************************************************************************************************************/
    private void bindAll (@Nonnull final Cell<?> cell, @Nonnull final RoleBag roles)
      {
        bindTextAndGraphic(cell, roles);
        bindDefaultAction(cell, roles);
        bindContextMenu(cell, roles);
        bindStyles(cell.getStyleClass(), roles);
      }

    /***********************************************************************************************************************************************************
     * Binds the text and eventual custom {@link javafx.scene.Node} provided by the given {@link RoleBag} to the given
     * {@link Cell}.
     *
     * @param     cell            the {@code Cell}
     * @param     roles           the role bag
     **********************************************************************************************************************************************************/
    private void bindTextAndGraphic (@Nonnull final Cell<?> cell, @Nonnull final RoleBag roles)
      {
        final var cgp = roles.get(_CustomGraphicProvider_);
        cell.setGraphic(cgp.map(CustomGraphicProvider::getGraphic).orElse(null));
        cell.setText(cgp.map(c -> "").orElse(roles.get(_Displayable_).map(Displayable::getDisplayName).orElse("")));
      }

    /***********************************************************************************************************************************************************
     * Binds the default {@link UserAction}s provided by the given {@link RoleBag} as the default action of the given
     * {@link Cell} (activated by double click or key pressure).
     *
     * @param     cell            the {@code Cell}
     * @param     roles           the role bag
     **********************************************************************************************************************************************************/
    private void bindDefaultAction (@Nonnull final Cell<?> cell, @Nonnull final RoleBag roles)
      {
        roles.getDefaultUserAction().ifPresent(defaultAction ->
          {
            // FIXME: doesn't work - keyevents are probably handled by ListView
            cell.setOnKeyPressed(event ->
              {
                log.debug("onKeyPressed: {}", event);

                if (event.getCode().equals(KeyCode.SPACE))
                  {
                    executor.execute(defaultAction::actionPerformed);
                  }
              });

            // FIXME: depends on mouse click, won't handle keyboard
            cell.setOnMouseClicked(event ->
              {
                if (event.getClickCount() == 2)
                  {
                    executor.execute(defaultAction::actionPerformed);
                  }
              });
          });
      }

    /***********************************************************************************************************************************************************
     * Binds the {@link UserAction}s provided by the given {@link RoleBag} as items of the contextual menu of a
     * {@link Cell}.
     *
     * @param     cell            the {@code Cell}
     * @param     roles           the role bag
     **********************************************************************************************************************************************************/
    private void bindContextMenu (@Nonnull final Cell<?> cell, @Nonnull final RoleBag roles)
      {
        final var menuItems = createMenuItems(roles);
        cell.setContextMenu(menuItems.isEmpty() ? null : new ContextMenu(menuItems.toArray(new MenuItem[0])));
      }

    /***********************************************************************************************************************************************************
     * Adds all the styles provided by the given {@link RoleBag} to a {@link ObservableList} of styles.
     *
     * @param     styleClasses    the destination where to add styles
     * @param     roles           the role bag
     **********************************************************************************************************************************************************/
    @Nonnull
    private void bindStyles (@Nonnull final ObservableList<String> styleClasses, @Nonnull final RoleBag roles)
      {
        final var styles = styleClasses.stream()
                                       .filter(s -> !s.startsWith(ROLE_STYLE_PREFIX))
                                       .collect(toList());
        // FIXME: shouldn't reset them? In case of cell reuse, they get accumulated
        styles.addAll(roles.getMany(_Styleable_)
                           .stream()
                           .flatMap(styleable -> styleable.getStyles().stream())
                           .map(s -> ROLE_STYLE_PREFIX + s)
                           .collect(toList()));
        styleClasses.setAll(styles);
      }

    /***********************************************************************************************************************************************************
     * Create a list of {@link MenuItem}s for each action provided by the given {@link RoleBag}.
     * Don't directly return a ContextMenu otherwise it will be untestable.
     *
     * @param     roles           the role bag
     * @return                    the list of {@MenuItem}s
     **********************************************************************************************************************************************************/
    @Nonnull
    @VisibleForTesting public List<MenuItem> createMenuItems (@Nonnull final RoleBag roles)
      {
        return roles.getMany(_UserActionProvider_).stream()
                    .flatMap(uap -> uap.getActions().stream())
                    .map(this::createMenuItem)
                    .collect(toList());
      }

    /***********************************************************************************************************************************************************
     *
     **********************************************************************************************************************************************************/
    private void clearBindings (@Nonnull final Cell<?> cell)
      {
        cell.setText("");
        cell.setGraphic(null);
        cell.setContextMenu(null);
        cell.setOnKeyPressed(null);
        cell.setOnMouseClicked(null);
      }

    /***********************************************************************************************************************************************************
     * Creates a {@link MenuItem} bound to the given action.
     *
     * @param     action          the action
     * @return                    the bound {@code MenuItem}
     **********************************************************************************************************************************************************/
    @Nonnull
    private MenuItem createMenuItem (@Nonnull final UserAction action)
      {
        final var menuItem = new MenuItem(action.as(_Displayable_).getDisplayName());
        menuItem.setOnAction(new EventHandlerUserActionAdapter(executor, action));
        return menuItem;
      }
  }
