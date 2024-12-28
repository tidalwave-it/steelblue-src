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
package it.tidalwave.ui.javafx.impl;

import javax.annotation.Nonnull;
import java.util.List;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.stage.Stage;
import org.testng.annotations.Test;
import static org.mockito.Mockito.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/***************************************************************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **************************************************************************************************************************************************************/
public class JavaFXToolBarModelTest extends UserActionsTestSupport<JavaFXToolBarModel, ToolBar>
  {
    @Override
    public void start (@Nonnull final Stage stage)
      {
        createBinder();
        underTest = new JavaFXToolBarModel(() -> List.of(a.actionFileOpen, a.actionFileClose, a.actionFileCloseAll));
        control = new ToolBar();
      }

    @Test
    public void test_populate()
      {
        // when
        underTest.populateImpl(binder, control);
        // then
        final var buttons = control.getItems();
        assertThat(buttons.size(), is(3));
        final var button1 = (Button)buttons.get(0);
        final var button2 = (Button)buttons.get(1);
        final var button3 = (Button)buttons.get(2);

        verify(binder).bind(button1, a.actionFileOpen);
        verify(binder).bind(button2, a.actionFileClose);
        verify(binder).bind(button3, a.actionFileCloseAll);
        verifyNoMoreInteractions(binder);
      }
  }
