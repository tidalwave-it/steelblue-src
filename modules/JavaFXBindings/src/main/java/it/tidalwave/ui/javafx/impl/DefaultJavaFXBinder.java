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
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import javafx.beans.binding.BooleanExpression;
import javafx.beans.property.Property;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.stage.Window;
import javafx.application.Platform;
import it.tidalwave.role.SimpleComposite;
import it.tidalwave.ui.core.BoundProperty;
import it.tidalwave.ui.core.role.Displayable;
import it.tidalwave.ui.core.role.PresentationModel;
import it.tidalwave.ui.core.role.Styleable;
import it.tidalwave.ui.core.role.UserAction;
import it.tidalwave.ui.core.role.UserActionProvider;
import it.tidalwave.ui.javafx.JavaFXBinder;
import it.tidalwave.ui.javafx.impl.combobox.ComboBoxBindings;
import it.tidalwave.ui.javafx.impl.common.CellBinder;
import it.tidalwave.ui.javafx.impl.common.ChangeListenerSelectableAdapter;
import it.tidalwave.ui.javafx.impl.common.DefaultCellBinder;
import it.tidalwave.ui.javafx.impl.common.PropertyAdapter;
import it.tidalwave.ui.javafx.impl.dialog.DialogBindings;
import it.tidalwave.ui.javafx.impl.filechooser.FileChooserBindings;
import it.tidalwave.ui.javafx.impl.list.ListViewBindings;
import it.tidalwave.ui.javafx.impl.tableview.TableViewBindings;
import it.tidalwave.ui.javafx.impl.tree.TreeViewBindings;
import it.tidalwave.ui.javafx.impl.treetable.TreeTableViewBindings;
import lombok.experimental.Delegate;
import lombok.extern.slf4j.Slf4j;
import static java.util.Collections.emptyList;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.*;
import static it.tidalwave.role.SimpleComposite._SimpleComposite_;
import static it.tidalwave.ui.core.role.Displayable._Displayable_;
import static it.tidalwave.ui.core.role.Styleable._Styleable_;
import static it.tidalwave.ui.core.role.UserActionProvider._UserActionProvider_;

/***************************************************************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **************************************************************************************************************************************************************/
@Slf4j
public class DefaultJavaFXBinder implements JavaFXBinder
  {
    private final Executor executor;

    private final String invalidTextFieldStyle = "-fx-background-color: pink";

    interface Exclusions
      {
        public void setMainWindow (Window window);
        // duplicated in TableViewBindings and TreeTableViewBindings due to common super class
        public ChangeListenerSelectableAdapter getSelectionListener();
      }

    @Delegate(excludes = Exclusions.class)
    private final TreeViewBindings treeItemBindings;

    @Delegate(excludes = Exclusions.class)
    private final TableViewBindings tableViewBindings;

    @Delegate(excludes = Exclusions.class)
    private final TreeTableViewBindings treeTableViewBindings;

    @Delegate(excludes = Exclusions.class)
    private final ListViewBindings listViewBindings;

    @Delegate(excludes = Exclusions.class)
    private final ComboBoxBindings comboBoxBindings;

    @Delegate(excludes = Exclusions.class)
    private final DialogBindings dialogBindings;

    @Delegate(excludes = Exclusions.class)
    private final FileChooserBindings fileChooserBindings;

    private final CellBinder cellBinder;

    /***********************************************************************************************************************************************************
     *
     **********************************************************************************************************************************************************/
    public DefaultJavaFXBinder (@Nonnull final Executor executor)
      {
        this.executor = executor;
        cellBinder = new DefaultCellBinder(executor);
        comboBoxBindings = new ComboBoxBindings(executor, cellBinder);
        treeItemBindings = new TreeViewBindings(executor, cellBinder);
        tableViewBindings = new TableViewBindings(executor, cellBinder);
        treeTableViewBindings = new TreeTableViewBindings(executor, cellBinder);
        listViewBindings = new ListViewBindings(executor, cellBinder);
        dialogBindings = new DialogBindings(executor);
        fileChooserBindings = new FileChooserBindings(executor);
      }

    /***********************************************************************************************************************************************************
     * {@inheritDoc}
     **********************************************************************************************************************************************************/
    @Override
    public void setMainWindow (@Nonnull final Window mainWindow)
      {
        treeItemBindings.setMainWindow(mainWindow);
        tableViewBindings.setMainWindow(mainWindow);
        dialogBindings.setMainWindow(mainWindow);
        fileChooserBindings.setMainWindow(mainWindow);
      }

    /***********************************************************************************************************************************************************
     * {@inheritDoc}
     **********************************************************************************************************************************************************/
    @Override
    public void bind (@Nonnull final ButtonBase button, @Nonnull final UserAction action)
      {
        assertIsFxApplicationThread();
        button.setText(action.maybeAs(_Displayable_).map(Displayable::getDisplayName).orElse(""));
        button.disableProperty().bind(adaptBoolean(action.enabled()).not());
        button.setOnAction(__ -> executor.execute(action::actionPerformed));
      }

    /***********************************************************************************************************************************************************
     * {@inheritDoc}
     **********************************************************************************************************************************************************/
    @Override
    public void bind (@Nonnull final MenuItem menuItem, @Nonnull final UserAction action)
      {
        assertIsFxApplicationThread();
        menuItem.setText(action.maybeAs(_Displayable_).map(Displayable::getDisplayName).orElse(""));
        menuItem.disableProperty().bind(adaptBoolean(action.enabled()).not());
        menuItem.setOnAction(__ -> executor.execute(action::actionPerformed));
      }

    /***********************************************************************************************************************************************************
     * {@inheritDoc}
     **********************************************************************************************************************************************************/
    @Override
    public <T> void bindBidirectionally (@Nonnull final Property<T> property1, @Nonnull final BoundProperty<T> property2)
      {
        assertIsFxApplicationThread();
        property1.bindBidirectional(new PropertyAdapter<>(executor, property2));
      }

    /***********************************************************************************************************************************************************
     * {@inheritDoc}
     **********************************************************************************************************************************************************/
    @Override
    public <T> void bindBidirectionally (@Nonnull final TextField textField,
                                         @Nonnull final BoundProperty<String> textProperty,
                                         @Nonnull final BoundProperty<Boolean> validProperty)
      {
        assertIsFxApplicationThread();
        requireNonNull(textField, "textField");
        requireNonNull(textProperty, "textProperty");
        requireNonNull(validProperty, "validProperty");

        textField.textProperty().bindBidirectional(new PropertyAdapter<>(executor, textProperty));

        // FIXME: weak listener
        validProperty.addPropertyChangeListener(__ -> textField.setStyle(validProperty.get() ? "" : invalidTextFieldStyle));
      }

    /***********************************************************************************************************************************************************
     * {@inheritDoc}
     **********************************************************************************************************************************************************/
    @Override
    public void bindToggleButtons (@Nonnull final Pane pane, @Nonnull final PresentationModel pm)
      {
        assertIsFxApplicationThread();
        final var group = new ToggleGroup();
        final var children = pane.getChildren();
        final var prototypeStyleClass = children.get(0).getStyleClass();
        final SimpleComposite<PresentationModel> pmc = pm.as(_SimpleComposite_);
        children.setAll(pmc.findChildren().stream().map(cpm -> createToggleButton(cpm, prototypeStyleClass, group)).collect(toList()));
      }

    /***********************************************************************************************************************************************************
     *
     **********************************************************************************************************************************************************/
    @Override
    public void bindButtonsInPane (@Nonnull final GridPane gridPane, @Nonnull final Collection<UserAction> actions)
      {
        assertIsFxApplicationThread();
        final var columnConstraints = gridPane.getColumnConstraints();
        final var children = gridPane.getChildren();

        columnConstraints.clear();
        children.clear();
        final var columnIndex = new AtomicInteger(0);

        actions.forEach(menuAction ->
          {
            final var column = new ColumnConstraints();
            column.setPercentWidth(100.0 / actions.size());
            columnConstraints.add(column);
            final var button = createButton();
            GridPane.setConstraints(button, columnIndex.getAndIncrement(), 0);
            bind(button, menuAction);
            children.add(button);
          });
      }

    /***********************************************************************************************************************************************************
     * {@return a new {@code Button}} for the menu bar.
     **********************************************************************************************************************************************************/
    @Nonnull
    private Button createButton()
      {
        final var button = new Button();
        GridPane.setHgrow(button, Priority.ALWAYS);
        GridPane.setVgrow(button, Priority.ALWAYS);
        GridPane.setHalignment(button, HPos.CENTER);
        GridPane.setValignment(button, VPos.CENTER);
        button.setPrefSize(999, 999); // fill
        button.getStyleClass().add("mainMenuButton");

        return button;
      }

    /***********************************************************************************************************************************************************
     *
     **********************************************************************************************************************************************************/
    @Nonnull
    private ToggleButton createToggleButton (@Nonnull final PresentationModel pm, @Nonnull final List<String> baseStyleClass, @Nonnull final ToggleGroup group)
      {
        final var button = new ToggleButton();
        button.setToggleGroup(group);
        button.setText(pm.maybeAs(_Displayable_).map(Displayable::getDisplayName).orElse(""));
        button.getStyleClass().addAll(baseStyleClass);
        button.getStyleClass().addAll(pm.maybeAs(_Styleable_).map(Styleable::getStyles).orElse(emptyList()));
        pm.maybeAs(_UserActionProvider_).flatMap(UserActionProvider::getOptionalDefaultAction)
                                        .ifPresent(action -> bind(button, action));

        if (group.getSelectedToggle() == null)
          {
            group.selectToggle(button);
          }

        return button;
      }

    /***********************************************************************************************************************************************************
     *
     **********************************************************************************************************************************************************/
    private void assertIsFxApplicationThread()
      {
        if (!Platform.isFxApplicationThread())
          {
            throw new AssertionError("Must run in the JavaFX Application Thread");
          }
      }

    /***********************************************************************************************************************************************************
     *
     **********************************************************************************************************************************************************/
    @Nonnull
    private BooleanExpression adaptBoolean (@Nonnull final BoundProperty<Boolean> property)
      {
        return BooleanExpression.booleanExpression(new PropertyAdapter<>(executor, property));
      }
  }
