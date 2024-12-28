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
package it.tidalwave.ui.javafx.impl.filechooser;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.Executor;
import java.io.File;
import java.nio.file.Path;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.application.Platform;
import it.tidalwave.util.ui.UserNotificationWithFeedback;
import it.tidalwave.role.ui.BoundProperty;
import it.tidalwave.ui.javafx.impl.common.DelegateSupport;
import lombok.extern.slf4j.Slf4j;

/***************************************************************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **************************************************************************************************************************************************************/
@Slf4j
public class FileChooserBindings extends DelegateSupport
  {
    /***********************************************************************************************************************************************************
     *
     **********************************************************************************************************************************************************/
    public FileChooserBindings (@Nonnull final Executor executor)
      {
        super(executor);
      }

    /***********************************************************************************************************************************************************
     * {@inheritDoc}
     **********************************************************************************************************************************************************/
    public void openFileChooserFor (@Nonnull final UserNotificationWithFeedback notification,
                                    @Nonnull final BoundProperty<Path> selectedFile)
      {
        log.debug("openFileChooserFor({}, {})", notification, selectedFile);
        assertIsFxApplicationThread();

        final var fileChooser = new FileChooser();
        fileChooser.setTitle(notification.getCaption());
        fileChooser.setInitialDirectory(selectedFile.get().toFile());

        // It seems we need to take care of modality: https://javafx-jira.kenai.com/browse/RT-13949
        final var file = runWhileDisabling(mainWindow, () -> fileChooser.showOpenDialog(mainWindow));

        notifyFile(file, notification, selectedFile);
      }

    /***********************************************************************************************************************************************************
     * {@inheritDoc}
     **********************************************************************************************************************************************************/
    public void openDirectoryChooserFor (@Nonnull final UserNotificationWithFeedback notification,
                                         @Nonnull final BoundProperty<Path> selectedFolder)
      {
        log.debug("openDirectoryChooserFor({}, {})", notification, selectedFolder);
        assertIsFxApplicationThread();

        final var directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle(notification.getCaption());
        directoryChooser.setInitialDirectory(selectedFolder.get().toFile());

        // It seems we need to take care of modality: https://javafx-jira.kenai.com/browse/RT-13949
        final var file = runWhileDisabling(mainWindow, () -> directoryChooser.showDialog(mainWindow));

        notifyFile(file, notification, selectedFolder);
      }

    /***********************************************************************************************************************************************************
     *
     **********************************************************************************************************************************************************/
    private void notifyFile (@Nullable final File file,
                             @Nonnull final UserNotificationWithFeedback notification,
                             @Nonnull final BoundProperty<Path> selectedFile)
      {
        Platform.runLater(() ->
          {
            try
              {
                if (file == null)
                  {
                    notification.cancel();
                  }
                else
                  {
                    selectedFile.set(file.toPath());
                    notification.confirm();
                  }
              }
            catch (Exception e)
              {
                log.warn("", e);
              }
          });
      }
  }
