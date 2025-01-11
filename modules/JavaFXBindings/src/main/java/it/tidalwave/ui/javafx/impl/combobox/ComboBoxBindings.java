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
package it.tidalwave.ui.javafx.impl.combobox;

import jakarta.annotation.Nonnull;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executor;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import it.tidalwave.ui.core.role.PresentationModel;
import it.tidalwave.ui.core.role.UserAction;
import it.tidalwave.ui.core.role.UserActionProvider;
import it.tidalwave.ui.javafx.impl.common.CellBinder;
import it.tidalwave.ui.javafx.impl.common.ChangeListenerSelectableAdapter;
import it.tidalwave.ui.javafx.impl.common.DelegateSupport;
import it.tidalwave.ui.javafx.impl.common.JavaFXWorker;
import it.tidalwave.ui.javafx.impl.common.AsObjectListCell;
import lombok.extern.slf4j.Slf4j;
import static javafx.scene.input.KeyCode.*;
import static it.tidalwave.ui.core.role.UserActionProvider._UserActionProvider_;
import static it.tidalwave.ui.javafx.impl.common.JavaFXWorker.childrenPm;

/***************************************************************************************************************************************************************
 *
 * This class takes care of bindings related to {@link ComboBox}.
 *
 * @author  Fabrizio Giudici
 *
 **************************************************************************************************************************************************************/
@Slf4j
public class ComboBoxBindings extends DelegateSupport
  {
    @Nonnull
    private final CellBinder cellBinder;

    private final Callback<ListView<PresentationModel>, ListCell<PresentationModel>> cellFactory;

    private final ChangeListener<PresentationModel> changeListener = new ChangeListenerSelectableAdapter(executor);

    /***********************************************************************************************************************************************************
     *
     **********************************************************************************************************************************************************/
    public ComboBoxBindings (@Nonnull final Executor executor, @Nonnull final CellBinder cellBinder)
      {
        super(executor);
        this.cellBinder = cellBinder;
        cellFactory = comboBox -> new AsObjectListCell<>(cellBinder);
      }

    /***********************************************************************************************************************************************************
     * Binds a combobox.
     * @param   comboBox            the {@link ComboBox}
     * @param   pm                  the {@link PresentationModel}
     * @param   callback            an optional callback to invoke at the end of the binding
     **********************************************************************************************************************************************************/
    public void bind (@Nonnull final ComboBox<PresentationModel> comboBox, @Nonnull final PresentationModel pm, @Nonnull final Optional<Runnable> callback)
      {
        log.debug("bind({}, {}, {})", comboBox, pm, callback);
        comboBox.setCellFactory(cellFactory);
        comboBox.setButtonCell(new AsObjectListCell<>(cellBinder));
        comboBox.setOnAction(this::onActionEvent);
        comboBox.setOnKeyPressed(event ->
          {
            if (List.of(SPACE, ENTER).contains(event.getCode()))
              {
                comboBox.show();
              }
          });

        final var selectedProperty = comboBox.getSelectionModel().selectedItemProperty();
        selectedProperty.removeListener(changeListener);
        JavaFXWorker.run(executor,
                         () -> childrenPm(pm),
                         items -> finalizeBinding(comboBox, items, selectedProperty, callback));
      }

    /***********************************************************************************************************************************************************
     * Event handler that calls back the default action bound to the given combobox item.
     **********************************************************************************************************************************************************/
    @SuppressWarnings("unchecked")
    private void onActionEvent (@Nonnull final ActionEvent event)
      {
        final var comboBox = (ComboBox<PresentationModel>)event.getSource();
        final var selectedPm = comboBox.getSelectionModel().getSelectedItem();
        selectedPm.maybeAs(_UserActionProvider_)
                  .flatMap(UserActionProvider::getOptionalDefaultAction)
                  .ifPresent(UserAction::actionPerformed);
      }

    /***********************************************************************************************************************************************************
     * Finalizes binding in the JavaFX thread and eventually invokes a callback.
     * @param   comboBox            the {@link ComboBox}
     * @param   items               the items of the combo box
     * @param   selectedProperty    the 'selected' property
     * @param   callback            an optional callback to invoke at the end
     **********************************************************************************************************************************************************/
    private void finalizeBinding (@Nonnull final ComboBox<PresentationModel> comboBox,
                                  @Nonnull final ObservableList<PresentationModel> items,
                                  @Nonnull final ReadOnlyObjectProperty<PresentationModel> selectedProperty,
                                  @Nonnull final Optional<Runnable> callback)
      {
        comboBox.setItems(items);

        if (!items.isEmpty())
          {
            comboBox.getSelectionModel().select(items.get(0));
          }

        selectedProperty.addListener(changeListener);
        callback.ifPresent(Runnable::run);
      }
  }
