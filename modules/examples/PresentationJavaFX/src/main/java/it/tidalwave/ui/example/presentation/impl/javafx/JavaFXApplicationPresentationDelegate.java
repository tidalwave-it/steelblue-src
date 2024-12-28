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
import javax.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.MenuBar;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import org.springframework.context.ApplicationContext;
import it.tidalwave.ui.core.MenuBarModel;
import it.tidalwave.ui.core.ToolBarModel;
import it.tidalwave.ui.javafx.JavaFXBinder;
import it.tidalwave.ui.javafx.PresentationAssembler;

/***************************************************************************************************************************************************************
 *
 * This class is referred by Application.fxml and instantiated by the JavaFX runtime.
 *
 * @author  Fabrizio Giudici
 *
 **************************************************************************************************************************************************************/
// START SNIPPET: all
public class JavaFXApplicationPresentationDelegate implements PresentationAssembler
  {
    @Inject
    private JavaFXBinder binder;

    @Inject
    private ToolBarModel toolBarModel;

    @Inject
    private MenuBarModel menuBarModel;

    @FXML
    private BorderPane pnMainPane;

    @FXML
    private ToolBar tbToolBar;

    @FXML
    private MenuBar mbMenuBar;

    @Override
    public void assemble (@Nonnull final ApplicationContext applicationContext)
      {
        toolBarModel.populate(binder, tbToolBar);
        menuBarModel.populate(binder, mbMenuBar);
        final var mainPanelPresentation = applicationContext.getBean(JavaFXMainPanelPresentation.class);
        pnMainPane.setCenter(mainPanelPresentation.getNad().getNode());
      }
  }
// END SNIPPET: all
