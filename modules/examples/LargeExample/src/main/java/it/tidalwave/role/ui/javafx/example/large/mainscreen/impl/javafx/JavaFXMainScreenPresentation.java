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
package it.tidalwave.role.ui.javafx.example.large.mainscreen.impl.javafx;

import it.tidalwave.ui.javafx.JavaFXSafeProxyCreator.NodeAndDelegate;
import it.tidalwave.role.ui.javafx.example.large.mainscreen.MainScreenPresentation;
import lombok.experimental.Delegate;
import lombok.Getter;
import static it.tidalwave.ui.javafx.JavaFXSafeProxyCreator.createNodeAndDelegate;

/***********************************************************************************************************************
 *
 * The default implementation of the presentation, which is instantiated by Spring, is not the JavaFX controller, but
 * a virtual proxy/decorator of it. It instantiates it and then delegate most of the behaviour.
 *
 * This object can be invoked by any thread.
 *
 * @stereotype  Presentation
 *
 * @author  Fabrizio Giudici (Fabrizio.Giudici@tidalwave.it)
 *
 **********************************************************************************************************************/
public class JavaFXMainScreenPresentation implements MainScreenPresentation
  {
    static interface Exclusions
      {
        public void showUp();
      }

    // NoteAndDelegate contains both the JavaFX {@code Node} and its controller (delegate). The delegate is wrapped
    // by a decorator that makes sure that all methods are invoked in the JavaFX thread.
    @Getter
    private final NodeAndDelegate nad = createNodeAndDelegate(getClass());

    // Typically almost all the methods are delegated, with the exception of the one that brings the presentation into
    // view.
    @Delegate(excludes = Exclusions.class)
    private final MainScreenPresentation delegate = nad.getDelegate();

    @Override
    public void showUp()
      {
      }
  }
