/*
 * *********************************************************************************************************************
 *
 * SteelBlue: DCI User Interfaces
 * http://tidalwave.it/projects/steelblue
 *
 * Copyright (C) 2015 - 2024 by Tidalwave s.a.s. (http://tidalwave.it)
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
package it.tidalwave.role.ui.example.presentation.javafx;

import javax.annotation.Nonnull;
import javafx.application.Platform;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;
import it.tidalwave.ui.javafx.JavaFXSpringAnnotationApplication;
import it.tidalwave.ui.javafx.JavaFXSpringApplication;
import it.tidalwave.util.PreferencesHandler;
import it.tidalwave.role.ui.example.presentation.MainPanelPresentationControl;
import static it.tidalwave.util.PreferencesHandler.KEY_INITIAL_SIZE;

/***********************************************************************************************************************
 *
 * The main class extends {@link JavaFXSpringApplication} and invokes a starting method on a controller that boots
 * the application.
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
// START SNIPPET: annotations
@Configuration
@EnableSpringConfigured
@EnableAspectJAutoProxy
@ComponentScan(basePackages = "it.tidalwave")
public class Main extends JavaFXSpringAnnotationApplication
// END SNIPPET: annotations
  {
    /*******************************************************************************************************************
     *
     * Usually {@code main()} does nothing more than a typical {@code main()} of a JavaFX application.
     * JavaFX and Spring are automatically booted with an implicit configuration:
     *
     * <ul>
     * <li>The FXML resource for the main UI is loaded from the same package as this class, and the name's
     *     {@code Application.fxml}</li>
     * <li>A splash screen is created from a FXML resource in the same package as this class and name
     *     {@code Splash.fxml}, It is rendered on the screen while the system is initialised in background.</li>
     * </ul>
     *
     ******************************************************************************************************************/
    // START SNIPPET: main
    public static void main (@Nonnull final String ... args)
      {
        try
          {
            System.setProperty(PreferencesHandler.PROP_APP_NAME, "SteelBlueExample");
            Platform.setImplicitExit(true);
            final var preferencesHandler = PreferencesHandler.getInstance();
            preferencesHandler.setDefaultProperty(KEY_INITIAL_SIZE, 0.8);
            launch(args);
          }
        catch (Throwable t)
          {
            // Don't use logging facilities here, they could be not initialized
            t.printStackTrace();
            System.exit(-1);
          }
      }
    // END SNIPPET: main

    /*******************************************************************************************************************
     *
     * This method retrieves a reference to the main controller and boots it.
     *
     ******************************************************************************************************************/
    // START SNIPPET: onStageCreated
    @Override
    protected void onStageCreated (@Nonnull final ApplicationContext applicationContext)
      {
        applicationContext.getBean(MainPanelPresentationControl.class).start();
      }
    // END SNIPPET: onStageCreated
  }