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
package it.tidalwave.ui.example.presentation.javafx;

import javax.annotation.Nonnull;
import javafx.util.Duration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;
import it.tidalwave.ui.javafx.JavaFXSpringAnnotationApplication;
import it.tidalwave.ui.javafx.JavaFXSpringApplication;
import it.tidalwave.ui.example.presentation.MainPanelPresentationControl;

/***************************************************************************************************************************************************************
 *
 * The main class extends {@link JavaFXSpringApplication} and invokes a starting method on a controller that boots the application.
 *
 * @author  Fabrizio Giudici
 *
 **************************************************************************************************************************************************************/
// START SNIPPET: annotations
@Configuration
@EnableSpringConfigured
@EnableAspectJAutoProxy
@ComponentScan(basePackages = "it.tidalwave")
// @EnableMessageBus
public class Main extends JavaFXSpringAnnotationApplication
// END SNIPPET: annotations
  {
    /***********************************************************************************************************************************************************
     * JavaFX and Spring are automatically booted with an implicit configuration:
     * <ul>
     * <li>The FXML resource for the main UI is loaded from the same package as this class, and the name's {@code Application.fxml}</li>
     * <li>A splash screen is created from a FXML resource in the same package as this class and name {@code Splash.fxml}, It is rendered on the screen while
     *     the system is initialised in background.</li>
     * </ul>
     **********************************************************************************************************************************************************/
    // START SNIPPET: main
    public static void main (@Nonnull final String [] args)
      {
        launch(Main.class,
               params().withArgs(args)
                       .withApplicationName("SteelBlueExample")
                       .withLogFolderPropertyName("it.tidalwave.ui.example.logFolder") // referenced in the logger configuration
                       // .withProperty(K_FULL_SCREEN, true)
                       // .withProperty(K_FULL_SCREEN_LOCKED, true)
                       // .withProperty(K_MAXIMIZED, true)
                       .withProperty(K_MIN_SPLASH_DURATION, Duration.seconds(3))
                       .withProperty(K_INITIAL_SIZE, 0.8));                                 // initial windows size (in percentage)
      }
    // END SNIPPET: main

    /***********************************************************************************************************************************************************
     * This method retrieves a reference to the main controller and boots it.
     **********************************************************************************************************************************************************/
    // START SNIPPET: onStageCreated
    @Override
    protected void onStageCreated (@Nonnull final ApplicationContext applicationContext)
      {
        // Because of STB-78, it's advisable not to user @PostConstruct to initialise controllers.
        applicationContext.getBean(MainPanelPresentationControl.class).populate();

        // If one likes pubsub, an alternate approach is to fire an event to notify initialization.
        // See also EnableMessageBus to simplify implementation.
        // applicationContext.getBean(MessageBus.class).publish(new PowerOnEvent());
      }
    // END SNIPPET: onStageCreated
  }