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
package it.tidalwave.ui.javafx.impl.list;
import jakarta.annotation.Nonnull;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.function.Supplier;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.stage.Window;
import javafx.util.Callback;
import it.tidalwave.ui.core.role.PresentationModel;
import it.tidalwave.ui.core.role.UserAction;
import it.tidalwave.ui.javafx.impl.common.AsObjectListCell;
import it.tidalwave.ui.javafx.impl.common.CellBinder;
import it.tidalwave.ui.javafx.impl.common.ChangeListenerSelectableAdapter;
import it.tidalwave.ui.javafx.impl.common.DelegateSupport;
import it.tidalwave.ui.javafx.impl.common.JavaFXWorker;
import it.tidalwave.ui.javafx.impl.common.RoleCollector;
import lombok.extern.slf4j.Slf4j;
import static it.tidalwave.ui.core.role.UserActionProvider._UserActionProvider_;
import static it.tidalwave.ui.javafx.impl.DefaultJavaFXBinder.enforceFxApplicationThread;
import static it.tidalwave.ui.javafx.impl.common.JavaFXWorker.childrenPm;
import static javafx.collections.FXCollections.observableArrayList;
import static javafx.scene.input.KeyCode.*;
/***************************************************************************************************************************************************************
 *
 * This class takes care of bindings related to {@link ListView}.
 *
 * @author  Fabrizio Giudici
 *
 **************************************************************************************************************************************************************/
@Slf4j
public class ListViewBindings extends DelegateSupport
  {
    @Nonnull
    private final Callback<ListView<PresentationModel>, ListCell<PresentationModel>> cellFactory;
    private final ChangeListener<PresentationModel> changeListener = new ChangeListenerSelectableAdapter(executor);
    /***********************************************************************************************************************************************************
     *
     **********************************************************************************************************************************************************/
    public ListViewBindings (@Nonnull final Executor executor, @Nonnull final CellBinder cellBinder, @Nonnull final Supplier<Window> mainWindow)
      {
        super(executor, mainWindow);
        cellFactory = listView -> new AsObjectListCell<>(cellBinder);
      }
    /***********************************************************************************************************************************************************
     * Binds a {@link ListView}.
     * @param   listView            the {@code ListView}
     * @param   pm                  the {@link PresentationModel}
     * @param   callback            an optional callback to invoke at the end of the binding
     **********************************************************************************************************************************************************/
    public void bind (@Nonnull final ListView<PresentationModel> listView, @Nonnull final PresentationModel pm, @Nonnull final Optional<Runnable> callback)
      {
        enforceFxApplicationThread();
        listView.setCellFactory(cellFactory);
        listView.setOnKeyPressed(event ->
          {
            if (List.of(SPACE, ENTER).contains(event.getCode()))
              {
                final var selectedPm = listView.getSelectionModel().getSelectedItem();
                // TODO: must call the default action - but should we look up it again?
                // Otherwise emulate mouse double click on the cell
                log.debug("ListView onKeyPressed: {}", selectedPm);
                executor.execute(() ->
                  {
                    // TODO: it would be nicer to retrieve the cell and its associated RoleCollector?
                    final var roles = new RoleCollector(selectedPm, List.of(_UserActionProvider_));
                    roles.getDefaultUserAction().ifPresent(UserAction::actionPerformed);
                  });
              }
          });
        final var selectedProperty = listView.getSelectionModel().selectedItemProperty();
        selectedProperty.removeListener(changeListener);
        listView.setItems(observableArrayList()); // quick clear in case of long operations FIXME doesn't work
        JavaFXWorker.run(executor,
                         () -> childrenPm(pm),
                         items -> finalizeBinding(listView, items, selectedProperty, callback));
      }
    /***********************************************************************************************************************************************************
     * Finalizes binding in the JavaFX thread and eventually invokes a callback.
     * @param   listView            the {@link ListView}
     * @param   items               the items of the table
     * @param   selectedProperty    the 'selected' property
     * @param   callback            an optional callback to invoke at the end
     **********************************************************************************************************************************************************/
    private void finalizeBinding (@Nonnull final ListView<PresentationModel> listView,
                                  @Nonnull final ObservableList<PresentationModel> items,
                                  @Nonnull final ReadOnlyObjectProperty<PresentationModel> selectedProperty,
                                  @Nonnull final Optional<Runnable> callback)
      {
        listView.setItems(items);
        selectedProperty.addListener(changeListener);
        callback.ifPresent(Runnable::run);
      }
  }
