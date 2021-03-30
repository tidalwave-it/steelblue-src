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
import java.io.IOException;
import javafx.util.Duration;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
@RequiredArgsConstructor @Slf4j
public class Splash
  {
    @Nonnull
    private final Object application;

    @Nonnull
    private final String fxml;

    private Pane splashPane;

    private Stage splashStage;

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    public void init()
      {
        try
          {
            log.info("Loading Splash.fxml for application {}", application);
            splashPane = FXMLLoader.load(application.getClass().getResource(fxml));
          }
        catch (IOException e)
          {
            log.warn("No Splash.fxml", e);
          }
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    public void show (@Nonnull final Stage splashStage)
      {
        this.splashStage = splashStage;
        final Scene splashScene = new Scene(splashPane);
        splashStage.setScene(splashScene);
        splashStage.show();
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    public void dismiss()
      {
//        loadProgress.progressProperty().unbind();
//        loadProgress.setProgress(1);
//        progressText.setText("Done.");

        final KeyFrame start = new KeyFrame(Duration.ZERO, new KeyValue(splashStage.opacityProperty(), 1.0));
        final KeyFrame end = new KeyFrame(Duration.millis(500), new KeyValue(splashStage.opacityProperty(), 0.0));
        final Timeline slideAnimation = new Timeline(start, end);
        slideAnimation.setOnFinished(event -> splashStage.close());
        slideAnimation.play();

//         FIXME: fade transition really doesn't work: it fades out the pane, but the stage is opaque.
//        final FadeTransition fadeSplash = new FadeTransition(Duration.seconds(0.5), splashPane);
//        fadeSplash.setFromValue(1.0);
//        fadeSplash.setToValue(0.0);
//        fadeSplash.setOnFinished(event -> splashStage.close());
//        fadeSplash.play();
      }
  }
