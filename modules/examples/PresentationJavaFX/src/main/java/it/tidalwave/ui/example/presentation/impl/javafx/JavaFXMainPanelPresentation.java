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

import org.springframework.stereotype.Component;
import it.tidalwave.ui.javafx.NodeAndDelegate;
import it.tidalwave.ui.example.presentation.MainPanelPresentation;
import lombok.Getter;
import lombok.experimental.Delegate;
import static it.tidalwave.ui.javafx.JavaFXSafeProxyCreator.createNodeAndDelegate;

/***************************************************************************************************************************************************************
 *
 * The default implementation of the presentation, which is instantiated by Spring, is not the JavaFX controller, but
 * a virtual proxy/decorator of it. It instantiates it and then delegate most of the behaviour.
 *
 * This object can be invoked by any thread.
 *
 * @stereotype  Presentation
 *
 * @author  Fabrizio Giudici
 *
 **************************************************************************************************************************************************************/
// NoteAndDelegate contains both the JavaFX {@code Node} and its controller (delegate). The delegate is wrapped
// by a decorator that makes sure that all methods are invoked in the JavaFX thread.
@Component
// START SNIPPET: all
public class JavaFXMainPanelPresentation implements MainPanelPresentation
  {
    @Getter
    private final NodeAndDelegate<? extends MainPanelPresentation> nad = createNodeAndDelegate(getClass());

    @Delegate
    private final MainPanelPresentation delegate = nad.getDelegate();
  }
// END SNIPPET: all
