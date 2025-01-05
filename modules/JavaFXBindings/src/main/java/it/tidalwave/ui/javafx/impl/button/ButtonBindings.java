/*
 * *************************************************************************************************************************************************************
 *
 * SteelBlue: DCI User Interfaces
 * http://tidalwave.it/projects/steelblue
 *
 * Copyright (C) 2015 - 2025 by Tidalwave s.a.s. (http://tidalwave.it)
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
package it.tidalwave.ui.javafx.impl.button;

import jakarta.annotation.Nonnull;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import javafx.beans.property.BooleanProperty;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.application.Platform;
import it.tidalwave.ui.core.BoundProperty;
import it.tidalwave.ui.core.role.Displayable;
import it.tidalwave.ui.core.role.PresentationModel;
import it.tidalwave.ui.core.role.Styleable;
import it.tidalwave.ui.core.role.UserAction;
import it.tidalwave.ui.core.role.UserActionProvider;
import it.tidalwave.ui.javafx.impl.common.DelegateSupport;
import it.tidalwave.util.As;
import it.tidalwave.role.SimpleComposite;
import static it.tidalwave.ui.core.role.Displayable._Displayable_;
import static it.tidalwave.ui.core.role.Styleable._Styleable_;
import static it.tidalwave.ui.core.role.UserActionProvider._UserActionProvider_;
import static it.tidalwave.ui.javafx.impl.DefaultJavaFXBinder.enforceFxApplicationThread;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.*;

/***************************************************************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **************************************************************************************************************************************************************/
public class ButtonBindings extends DelegateSupport
  {
    private static final As.Type<SimpleComposite<PresentationModel>>  _SimpleCompositePresentationModel_ = new As.Type<>(SimpleComposite.class);

    /***********************************************************************************************************************************************************
     *
     **********************************************************************************************************************************************************/
    public ButtonBindings (@Nonnull final Executor executor)
      {
        super(executor);
      }

    /***********************************************************************************************************************************************************
     *
     **********************************************************************************************************************************************************/
    public void bind (@Nonnull final ButtonBase button, @Nonnull final UserAction action)
      {
        enforceFxApplicationThread();
        action.maybeAs(_Displayable_).ifPresent(d -> button.setText(d.getDisplayName()));
        button.setOnAction(__ -> executor.execute(action::actionPerformed));
        bindEnableProperty(button.disableProperty(), action.enabled());
      }

    /***********************************************************************************************************************************************************
     *
     **********************************************************************************************************************************************************/
    public void bind (@Nonnull final MenuItem menuItem, @Nonnull final UserAction action)
      {
        enforceFxApplicationThread();
        menuItem.setText(action.maybeAs(_Displayable_).map(Displayable::getDisplayName).orElse(""));
        menuItem.setOnAction(__ -> executor.execute(action::actionPerformed));
        bindEnableProperty(menuItem.disableProperty(), action.enabled());
      }

    /***********************************************************************************************************************************************************
     * {@inheritDoc}
     **********************************************************************************************************************************************************/
    public void bindToggleButtons (@Nonnull final Pane pane, @Nonnull final PresentationModel pm)
      {
        enforceFxApplicationThread();
        final var group = new ToggleGroup();
        final var children = pane.getChildren();
        final var prototypeStyleClass = children.get(0).getStyleClass();
        final var pmc = pm.as(_SimpleCompositePresentationModel_);
        children.setAll(pmc.findChildren().stream().map(cpm -> createToggleButton(cpm, prototypeStyleClass, group)).collect(toList()));
      }

    /***********************************************************************************************************************************************************
     *
     **********************************************************************************************************************************************************/
    public void bindButtonsInPane (@Nonnull final GridPane gridPane, @Nonnull final Collection<UserAction> actions)
      {
        enforceFxApplicationThread();
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
    private void bindEnableProperty (@Nonnull final BooleanProperty property1, @Nonnull final BoundProperty<Boolean> property2)
      {
        property1.setValue(!property2.get());
        property1.addListener((_1, _2, newValue) -> executor.execute(() -> property2.set(!newValue)));
        property2.addPropertyChangeListener(evt -> Platform.runLater(() -> property1.setValue(!(boolean)evt.getNewValue())));
      }
  }
