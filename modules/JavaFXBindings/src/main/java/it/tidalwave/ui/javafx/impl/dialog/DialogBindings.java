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
package it.tidalwave.ui.javafx.impl.dialog;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.awt.Desktop;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.web.WebView;
import javafx.application.Platform;
import it.tidalwave.util.BundleUtilities;
import it.tidalwave.util.Callback;
import it.tidalwave.util.ui.UserNotificationWithFeedback;
import it.tidalwave.ui.javafx.impl.common.DelegateSupport;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/***************************************************************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **************************************************************************************************************************************************************/
@Slf4j
public class DialogBindings extends DelegateSupport
  {
    @RequiredArgsConstructor
    static class UrlOpener implements ChangeListener<String>
      {
        @Nonnull
        private final WebView webView;

        @Nonnull
        private final String text;

        @Override
        public void changed (final ObservableValue<? extends String> observableValue, final String oldValue, final String newValue)
          {
            if (newValue != null && !newValue.isEmpty())
              {
                Platform.runLater(() ->
                  {
                    try
                      {
                        log.debug("Opening {} ...", newValue);
                        Desktop.getDesktop().browse(new URI(newValue));
                      }
                    catch (IOException | URISyntaxException e)
                      {
                        log.warn("", e);
                      }
                  });

                webView.getEngine().loadContent(text);
              }
          }
      }

    /***********************************************************************************************************************************************************
     *
     **********************************************************************************************************************************************************/
    public DialogBindings (@Nonnull final Executor executor)
      {
        super(executor);
      }

    /***********************************************************************************************************************************************************
     * {@inheritDoc}
     **********************************************************************************************************************************************************/
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
            final var dialogPane = dialog.getDialogPane();
            final var text = notification.getText();

            if (!text.startsWith("<html>"))
              {
                dialog.setContentText(text);
                node.ifPresent(n -> dialogPane.setContent(n));
              }
            else
              {
                final var webView = new WebView();
                webView.setPrefSize(600, 300); // FIXME: proportional to screen
                webView.setContextMenuEnabled(false);
                final var label = new Label();
                dialogPane.setContent(label);
                final var fontFamily = label.getFont().getFamily();
                // System.err.println("FILL " + label.getTextFill());
                final var textColor = "white"; // FIXME
                var backgroundColor = "#252525"; // FIXME

                try
                  {
                    // Available in JDK 18+
                    final var setPageFill = webView.getClass().getDeclaredMethod("setPageFill", Color.class);
                    setPageFill.invoke(webView, Color.TRANSPARENT);
                    backgroundColor = "transparent";
                  }
                catch (Exception e)
                  {
                    log.trace("WebView.setPageFill() not available", e);
                  }

                // FIXME
                final var text2 = BundleUtilities.getMessage(getClass(),"htmlTemplate", text, textColor, backgroundColor, fontFamily);
                // System.err.println(text2);
                webView.getEngine().loadContent(text2);
                webView.getEngine().locationProperty().addListener(new UrlOpener(webView, text2));
                dialogPane.setContent(webView);
              }

            final var feedback = notification.getFeedback();
            final var hasOnCancel = feedback.canCancel();

            final var buttonTypes = dialogPane.getButtonTypes();
            buttonTypes.clear();
            buttonTypes.add(ButtonType.OK);

            if (hasOnCancel)
              {
                buttonTypes.add(ButtonType.CANCEL);
              }

//                okButton.disableProperty().bind(new PropertyAdapter<>(valid)); // FIXME: doesn't work

            final var result = dialog.showAndWait();

            if (result.isEmpty())
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

    /***********************************************************************************************************************************************************
     *
     **********************************************************************************************************************************************************/
    @SneakyThrows(Throwable.class)
    private static void wrap (@Nonnull final Callback callback)
      {
        callback.call();
      }
  }
