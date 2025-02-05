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
package it.tidalwave.ui.javafx.impl.tableview;

import jakarta.annotation.Nonnull;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.function.Supplier;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Window;
import javafx.util.Callback;
import it.tidalwave.ui.core.role.PresentationModel;
import it.tidalwave.ui.javafx.impl.common.CellBinder;
import it.tidalwave.ui.javafx.impl.common.ChangeListenerSelectableAdapter;
import it.tidalwave.ui.javafx.impl.common.DelegateSupport;
import it.tidalwave.ui.javafx.impl.common.JavaFXWorker;
import it.tidalwave.ui.javafx.impl.common.PresentationModelObservable;
import lombok.extern.slf4j.Slf4j;
import static it.tidalwave.ui.javafx.impl.DefaultJavaFXBinder.enforceFxApplicationThread;
import static it.tidalwave.ui.javafx.impl.common.JavaFXWorker.childrenPm;

/***************************************************************************************************************************************************************
 *
 * This class takes care of bindings related to {@link TableView}.
 *
 * @author  Fabrizio Giudici
 *
 **************************************************************************************************************************************************************/
@Slf4j
public class TableViewBindings extends DelegateSupport
  {
    private final Callback<TableColumn<PresentationModel, PresentationModel>, TableCell<PresentationModel, PresentationModel>> cellFactory;

    private final ChangeListener<PresentationModel> changeListener = new ChangeListenerSelectableAdapter(executor);

    /***********************************************************************************************************************************************************
     *
     **********************************************************************************************************************************************************/
    public TableViewBindings (@Nonnull final Executor executor, @Nonnull final CellBinder cellBinder, @Nonnull final Supplier<Window> mainWindow)
      {
        super(executor, mainWindow);
        cellFactory = ignored -> new AsObjectTableCell<>(cellBinder);
      }

    /***********************************************************************************************************************************************************
     * Binds a {@link TableView}.
     * @param   tableView           the {@code TableView}
     * @param   pm                  the {@link PresentationModel}
     * @param   callback            an optional callback to invoke at the end of the binding
     **********************************************************************************************************************************************************/
    public void bind (@Nonnull final TableView<PresentationModel> tableView, @Nonnull final PresentationModel pm, @Nonnull final Optional<Runnable> callback)
      {
        enforceFxApplicationThread();
        log.debug("bind({}, {}, {})", tableView, pm, callback);
        final var selectedProperty = tableView.getSelectionModel().selectedItemProperty();
        selectedProperty.removeListener(changeListener);
        JavaFXWorker.run(executor,
                         () -> childrenPm(pm),
                         items -> finalizeBinding(tableView, items, selectedProperty, callback));
      }

    /***********************************************************************************************************************************************************
     * Finalizes binding in the JavaFX thread and eventually invokes a callback.
     * @param   tableView           the {@link TableView}
     * @param   items               the items of the table
     * @param   selectedProperty    the 'selected' property
     * @param   callback            an optional callback to invoke at the end
     **********************************************************************************************************************************************************/
    @SuppressWarnings("unchecked")
    private void finalizeBinding (@Nonnull final TableView<PresentationModel> tableView,
                                  @Nonnull final ObservableList<PresentationModel> items,
                                  @Nonnull final ReadOnlyObjectProperty<PresentationModel> selectedProperty,
                                  @Nonnull final Optional<Runnable> callback)
      {
        tableView.setItems(items);
        selectedProperty.addListener(changeListener);
        tableView.getColumns()
                 .stream()
                 .map(c -> (TableColumn<PresentationModel, PresentationModel>)c)
                 .forEach(column ->
          {
            column.setCellValueFactory(PresentationModelObservable::of);
            column.setCellFactory(cellFactory);
          });

        callback.ifPresent(Runnable::run);
      }
  }
