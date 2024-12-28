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
package it.tidalwave.ui.example.presentation.impl.javafx;

import javax.annotation.Nonnull;
import java.nio.file.Path;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.TreeView;
import it.tidalwave.util.ui.UserNotificationWithFeedback;
import it.tidalwave.role.ui.BoundProperty;
import it.tidalwave.role.ui.PresentationModel;
import it.tidalwave.ui.javafx.JavaFXBinder;
import it.tidalwave.ui.example.presentation.MainPanelPresentation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/***************************************************************************************************************************************************************
 *
 * The JavaFX controller just works as a dumb object with SteelBlue: it must implement in the most simple way each
 * method in the presentation interface, by applying simple manipulation to the UI. No logic here, neither presentaton
 * nor business.
 *
 * @author  Fabrizio Giudici
 *
 **************************************************************************************************************************************************************/
@RequiredArgsConstructor @Slf4j
public class JavaFXMainPanelPresentationDelegate implements MainPanelPresentation
  {
    // START SNIPPET: fields
    // This facility provides all the methods to bind JavaFX controls to DCI roles.
    @Nonnull
    private final JavaFXBinder binder;

    @FXML
    private Button btButton;

    @FXML
    private Button btDialogOk;

    @FXML
    private Button btDialogOkCancel;

    @FXML
    private Button btPickFile;

    @FXML
    private Button btPickDirectory;

    @FXML
    private TextField tfTextField;

    @FXML
    private ListView<PresentationModel> lvListView;

    @FXML
    private ComboBox<PresentationModel> cbComboBox;

    @FXML
    private TableView<PresentationModel> tvTableView;

    @FXML
    private TreeView<PresentationModel> tvTreeView;

    @FXML
    private TreeTableView<PresentationModel> ttvTreeTableView;

    @FXML
    private TextArea taLog;
    // END SNIPPET: fields

    /***********************************************************************************************************************************************************
     * {@inheritDoc}
     **********************************************************************************************************************************************************/
    // START SNIPPET: bind
    @Override
    public void bind (@Nonnull final Bindings bindings)
      {
        binder.bind(btButton, bindings.actionButton);
        binder.bind(btDialogOk, bindings.actionDialogOk);
        binder.bind(btDialogOkCancel, bindings.actionDialogCancelOk);
        binder.bind(btPickFile, bindings.actionPickFile);
        binder.bind(btPickDirectory, bindings.actionPickDirectory);
        binder.bindBidirectionally(tfTextField, bindings.textProperty, bindings.booleanProperty);
      }
    // END SNIPPET: bind

    /***********************************************************************************************************************************************************
     * {@inheritDoc}
     **********************************************************************************************************************************************************/
    // START SNIPPET: populate
    @Override
    public void populate (@Nonnull final PresentationModel pmSimpleEntities,
                          @Nonnull final PresentationModel pmDciEntities,
                          @Nonnull final PresentationModel pmFileEntities)
      {
        binder.bind(lvListView, pmSimpleEntities, () -> log.info("Finished setup of lvListView"));
        binder.bind(cbComboBox, pmSimpleEntities, () -> log.info("Finished setup of cbComboBox"));
        binder.bind(tvTableView, pmDciEntities, () -> log.info("Finished setup of tvTableView"));
        binder.bind(tvTreeView, pmFileEntities, () -> log.info("Finished setup of tvTreeView"));
        binder.bind(ttvTreeTableView, pmFileEntities, () -> log.info("Finished setup of ttvTreeTableView"));
      }
    // END SNIPPET: populate

    /***********************************************************************************************************************************************************
     * {@inheritDoc}
     **********************************************************************************************************************************************************/
    // START SNIPPET: notify1
    @Override
    public void notify (@Nonnull final UserNotificationWithFeedback notification)
      {
        binder.showInModalDialog(notification);
      }
    // END SNIPPET: notify1

    /***********************************************************************************************************************************************************
     * {@inheritDoc}
     **********************************************************************************************************************************************************/
    // START SNIPPET: notify2
    @Override
    public void notify (@Nonnull final String message)
      {
        taLog.appendText(message + "\n");
      }
    // END SNIPPET: notify2

    /***********************************************************************************************************************************************************
     * {@inheritDoc}
     **********************************************************************************************************************************************************/
    // START SNIPPET: pickFile
    @Override
    public void pickFile (@Nonnull final BoundProperty<Path> selectedFile,
                          @Nonnull final UserNotificationWithFeedback notification)
      {
        binder.openFileChooserFor(notification, selectedFile);
      }
    // END SNIPPET: pickFile

    /***********************************************************************************************************************************************************
     * {@inheritDoc}
     **********************************************************************************************************************************************************/
    // START SNIPPET: pickDirectory
    @Override
    public void pickDirectory (@Nonnull final BoundProperty<Path> selectedFolder,
                               @Nonnull final UserNotificationWithFeedback notification)
      {
        binder.openDirectoryChooserFor(notification, selectedFolder);
      }
    // END SNIPPET: pickDirectory
  }
