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
package it.tidalwave.ui.javafx.impl.tableview;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.concurrent.Executor;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;
import it.tidalwave.ui.core.role.PresentationModel;
import it.tidalwave.ui.javafx.impl.common.CellBinder;
import it.tidalwave.ui.javafx.impl.common.ChangeListenerSelectableAdapter;
import it.tidalwave.ui.javafx.impl.common.DelegateSupport;
import it.tidalwave.ui.javafx.impl.common.JavaFXWorker;
import it.tidalwave.ui.javafx.impl.common.PresentationModelObservable;
import lombok.extern.slf4j.Slf4j;
import static it.tidalwave.ui.javafx.impl.common.JavaFXWorker.childrenPm;

/***************************************************************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **************************************************************************************************************************************************************/
@Slf4j
public class TableViewBindings extends DelegateSupport
  {
    private final Callback<TableColumn<PresentationModel, PresentationModel>,
                     TableCell<PresentationModel, PresentationModel>> cellFactory;

    private final ChangeListener<PresentationModel> changeListener = new ChangeListenerSelectableAdapter(executor);

    /***********************************************************************************************************************************************************
     *
     **********************************************************************************************************************************************************/
    public TableViewBindings (@Nonnull final Executor executor, @Nonnull final CellBinder cellBinder)
      {
        super(executor);
        cellFactory = __ -> AsObjectTableCell.of(cellBinder);
      }

    /***********************************************************************************************************************************************************
     * {@inheritDoc}
     **********************************************************************************************************************************************************/
    public void bind (@Nonnull final TableView<PresentationModel> tableView,
                      @Nonnull final PresentationModel pm,
                      @Nonnull final Optional<Runnable> callback)
      {
        assertIsFxApplicationThread();
        log.debug("bind({}, {}, {})", tableView, pm, callback);

        final var selectedProperty = tableView.getSelectionModel().selectedItemProperty();
        selectedProperty.removeListener(changeListener);
        JavaFXWorker.run(executor,
                         () -> childrenPm(pm),
                         items -> finalize(tableView, items, selectedProperty, callback));
      }

    /***********************************************************************************************************************************************************
     *
     **********************************************************************************************************************************************************/
    private void finalize (@Nonnull final TableView<PresentationModel> tableView,
                           @Nonnull final ObservableList<PresentationModel> items,
                           @Nonnull final ReadOnlyObjectProperty<PresentationModel> selectedProperty,
                           @Nonnull final Optional<Runnable> callback)
      {
        tableView.setItems(items);
        selectedProperty.addListener(changeListener);

        final ObservableList rawColumns = tableView.getColumns(); // FIXME cast

        ((ObservableList<TableColumn<PresentationModel, PresentationModel>>)rawColumns).forEach(column ->
          {
            column.setCellValueFactory(PresentationModelObservable::of);
            column.setCellFactory(cellFactory);
          });

        callback.ifPresent(Runnable::run);
      }
  }
