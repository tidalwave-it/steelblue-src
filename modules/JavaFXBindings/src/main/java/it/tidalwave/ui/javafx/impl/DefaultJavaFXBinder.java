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
package it.tidalwave.ui.javafx.impl;

import jakarta.annotation.Nonnull;
import java.util.concurrent.Executor;
import java.util.function.Function;
import javafx.beans.property.Property;
import javafx.scene.control.TextField;
import javafx.stage.Window;
import javafx.application.Platform;
import it.tidalwave.ui.core.BoundProperty;
import it.tidalwave.ui.javafx.JavaFXBinder;
import it.tidalwave.ui.javafx.impl.button.ButtonBindings;
import it.tidalwave.ui.javafx.impl.combobox.ComboBoxBindings;
import it.tidalwave.ui.javafx.impl.common.CellBinder;
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
import static java.util.Objects.requireNonNull;

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

    @Delegate
    private final ButtonBindings buttonBindings;

    @Delegate
    private final TreeViewBindings treeItemBindings;

    @Delegate
    private final TableViewBindings tableViewBindings;

    @Delegate
    private final TreeTableViewBindings treeTableViewBindings;

    @Delegate
    private final ListViewBindings listViewBindings;

    @Delegate
    private final ComboBoxBindings comboBoxBindings;

    @Delegate
    private final DialogBindings dialogBindings;

    @Delegate
    private final FileChooserBindings fileChooserBindings;

    private final CellBinder cellBinder;

    /***********************************************************************************************************************************************************
     *
     **********************************************************************************************************************************************************/
    public DefaultJavaFXBinder (@Nonnull final Executor executor)
      {
        this.executor = executor;
        cellBinder = new DefaultCellBinder(executor);
        buttonBindings = new ButtonBindings(executor);
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
    public <T, S> void bind (@Nonnull final BoundProperty<? super T> target, @Nonnull final Property<? extends S> source, @Nonnull final Function<S, T> adapter)
      {
        enforceFxApplicationThread();
        source.addListener((_1, _2, newValue) -> executor.execute(() -> target.set(adapter.apply(newValue))));
      }

    /***********************************************************************************************************************************************************
     * {@inheritDoc}
     **********************************************************************************************************************************************************/
    @Override @SuppressWarnings("unchecked")
    public <T, S> void bindBidirectionally (@Nonnull final BoundProperty<? super T> property1,
                                            @Nonnull final Property<S> property2,
                                            @Nonnull final Function<? super S, T> adapter,
                                            @Nonnull final Function<? super T, ? extends S> reverseAdapter)
      {
        enforceFxApplicationThread();
        property2.addListener((_1, _2, newValue) -> executor.execute(() -> property1.set(adapter.apply(newValue))));
        property1.addPropertyChangeListener(evt -> Platform.runLater(() -> property2.setValue(reverseAdapter.apply((T)evt.getNewValue()))));
      }

    /***********************************************************************************************************************************************************
     * {@inheritDoc}
     **********************************************************************************************************************************************************/
    @Override
    public void bindBidirectionally (@Nonnull final TextField textField,
                                     @Nonnull final BoundProperty<String> textProperty,
                                     @Nonnull final BoundProperty<Boolean> validProperty)
      {
        enforceFxApplicationThread();
        requireNonNull(textField, "textField");
        requireNonNull(textProperty, "textProperty");
        requireNonNull(validProperty, "validProperty");

        textField.textProperty().bindBidirectional(new PropertyAdapter<>(executor, textProperty));

        // FIXME: weak listener
        validProperty.addPropertyChangeListener(__ -> textField.setStyle(validProperty.get() ? "" : invalidTextFieldStyle));
      }

    /***********************************************************************************************************************************************************
     *
     **********************************************************************************************************************************************************/
    public static void enforceFxApplicationThread()
      {
        if (!Platform.isFxApplicationThread())
          {
            throw new IllegalStateException("Must run in the JavaFX Application Thread");
          }
      }
  }
