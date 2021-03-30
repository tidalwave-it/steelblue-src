/*
 * *********************************************************************************************************************
 *
 * SteelBlue: DCI User Interfaces
 * http://tidalwave.it/projects/steelblue
 *
 * Copyright (C) 2015 - 2021 by Tidalwave s.a.s. (http://tidalwave.it)
 *
 * *********************************************************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * *********************************************************************************************************************
 *
 * git clone https://bitbucket.org/tidalwave/steelblue-src
 * git clone https://github.com/tidalwave-it/steelblue-src
 *
 * *********************************************************************************************************************
 */
package it.tidalwave.role.ui.javafx.impl.dialog;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.concurrent.Executor;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.application.Platform;
import it.tidalwave.util.Callback;
import it.tidalwave.util.ui.UserNotificationWithFeedback;
import it.tidalwave.util.ui.UserNotificationWithFeedback.Feedback;
import it.tidalwave.role.ui.javafx.impl.common.DelegateSupport;
import javafx.scene.control.Alert;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
@Slf4j
public class DialogBindings extends DelegateSupport
  {
    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    public DialogBindings (@Nonnull final Executor executor)
      {
        super(executor);
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    public void showInModalDialog (@Nonnull final UserNotificationWithFeedback notification,
                                   @Nonnull final Optional<Node> node)
      {
        // FIXME: should not be needed
        Platform.runLater(() ->
          {
            log.debug("modalDialog({}, {})", node, notification);

//                final Dialog<ButtonType> dialog = new Dialog<>();
            final Dialog<ButtonType> dialog = new Alert(Alert.AlertType.NONE);
            dialog.initOwner(mainWindow);
            dialog.setTitle(notification.getCaption());
            dialog.setResizable(false);
            dialog.setContentText(notification.getText());
            node.ifPresent(n -> dialog.getDialogPane().setContent(n));

            final Feedback feedback = notification.getFeedback();
            final boolean hasOnCancel = feedback.canCancel();

            final ObservableList<ButtonType> buttonTypes = dialog.getDialogPane().getButtonTypes();
            buttonTypes.clear();
            buttonTypes.add(ButtonType.OK);

            if (hasOnCancel)
              {
                buttonTypes.add(ButtonType.CANCEL);
              }

//                okButton.disableProperty().bind(new PropertyAdapter<>(valid)); // FIXME: doesn't work

            final Optional<ButtonType> result = dialog.showAndWait();

            if (!result.isPresent())
              {
                if (hasOnCancel)
                  {
                    wrap(notification::cancel);
                  }
                else
                  {
                    wrap(notification::confirm);
                  }
              }
            else
              {
                if (result.get() == ButtonType.OK)
                  {
                    wrap(notification::confirm);
                  }
                else if (result.get() == ButtonType.CANCEL)
                  {
                    wrap(notification::cancel);
                  }
                else
                  {
                    throw new IllegalStateException("Unexpected button pressed: " + result.get());
                  }
              }
          });
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @SneakyThrows(Throwable.class)
    private static void wrap (@Nonnull final Callback callback)
      {
        callback.call();
      }
  }
