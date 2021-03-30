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
package it.tidalwave.ui.javafx;

import javax.annotation.Nonnull;
import java.util.concurrent.Executors;
import java.util.concurrent.Executor;
import java.io.IOException;
import it.tidalwave.util.PreferencesHandler;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCombination;
import javafx.application.Application;
import javafx.application.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import it.tidalwave.ui.javafx.JavaFXSafeProxyCreator.NodeAndDelegate;
import lombok.Getter;
import lombok.Setter;
import static it.tidalwave.util.PreferencesHandler.*;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
public abstract class JavaFXApplicationWithSplash extends Application
  {
    private static final String DEFAULT_APPLICATION_FXML = "Application.fxml";

    private static final String DEFAULT_SPLASH_FXML = "Splash.fxml";

    // Don't use Slf4j and its static logger - give Main a chance to initialize things
    private final Logger log = LoggerFactory.getLogger(JavaFXApplicationWithSplash.class);

    private Splash splash;

    @Getter @Setter
    private boolean maximized;

    @Getter @Setter
    private boolean fullScreen;

    @Getter @Setter
    private boolean fullScreenLocked;

    @Getter @Setter
    private boolean useAquaFxOnMacOsX;

    @Getter @Setter
    protected String applicationFxml = DEFAULT_APPLICATION_FXML;

    @Getter @Setter
    protected String splashFxml = DEFAULT_SPLASH_FXML;

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override
    public void init()
      {
        log.info("init()");
        splash = new Splash(this, splashFxml);
        splash.init();
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override
    public void start (@Nonnull final Stage stage)
      {
        log.info("start({})", stage);
        final Stage splashStage = new Stage(StageStyle.TRANSPARENT);
        stage.setMaximized(maximized);
//        splashStage.setMaximized(maximized); FIXME: doesn't work
        configureFullScreen(stage);
//        configureFullScreen(splashStage); FIXME: deadlocks JDK 1.8.0_40

        if (!maximized && !fullScreen)
          {
            splashStage.centerOnScreen();
          }

        splash.show(splashStage);

        getExecutor().execute(() -> // FIXME: use JavaFX Worker?
          {
            initializeInBackground();
            Platform.runLater(() ->
              {
                try
                  {
                    final NodeAndDelegate applicationNad = createParent();
                    final Scene scene = new Scene((Parent)applicationNad.getNode());

                    if (useAquaFxOnMacOsX && isOSX())
                      {
                        setMacOSXLookAndFeel(scene);
                      }

                    stage.setOnCloseRequest(event -> onClosing());
                    stage.setScene(scene);
                    onStageCreated(stage, applicationNad);
                    final PreferencesHandler preferencesHandler = PreferencesHandler.getInstance();

                    stage.setFullScreen(preferencesHandler.getProperty(KEY_FULL_SCREEN).orElse(false));
                    final double scale = preferencesHandler.getProperty(KEY_INITIAL_SIZE).orElse(0.65);
                    final Rectangle2D screenSize = Screen.getPrimary().getBounds();
                    stage.setWidth(scale * screenSize.getWidth());
                    stage.setHeight(scale * screenSize.getHeight());
                    stage.show();
                    splashStage.toFront();
                    splash.dismiss();
                  }
                catch (IOException e)
                  {
                    log.error("", e);
                  }
              });
          });
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    protected void onStageCreated (@Nonnull final Stage stage, @Nonnull final NodeAndDelegate applicationNad)
      {
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    protected abstract NodeAndDelegate createParent()
      throws IOException;

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    protected abstract void initializeInBackground();

    /*******************************************************************************************************************
     *
     * Invoked when the main {@link Stage} is being closed.
     *
     ******************************************************************************************************************/
    protected void onClosing()
      {
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    protected Executor getExecutor()
      {
        return Executors.newSingleThreadExecutor();
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    private void setMacOSXLookAndFeel (@Nonnull final Scene scene)
      {
      }

    /*******************************************************************************************************************
     *
     * TODO: delegate to a provider
     *
     ******************************************************************************************************************/
    public static boolean isOSX()
      {
        return System.getProperty("os.name").contains("OS X");
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    private void configureFullScreen (@Nonnull final Stage stage)
      {
        stage.setFullScreen(fullScreen);

        if (fullScreen && fullScreenLocked)
          {
            stage.setFullScreenExitHint("");
            stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
          }
      }
  }
