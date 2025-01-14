/*
 * *************************************************************************************************************************************************************
 *
 * SteelBlue: DCI User Interfaces
 * http://tidalwave.it/projects/steelblue
 *
 * Copyright (C) 2015 - 2025 by Tidalwave s.a.s. (http://tidalwave.it)
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
package it.tidalwave.ui.javafx.impl.tree;

import jakarta.annotation.Nonnull;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import it.tidalwave.util.As;
import it.tidalwave.ui.core.role.Displayable;
import it.tidalwave.ui.javafx.impl.JavaFXTestSupport;
import it.tidalwave.ui.javafx.impl.common.DefaultCellBinder;
import it.tidalwave.role.spi.SystemRoleFactory;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

/***************************************************************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **************************************************************************************************************************************************************/
public class AsObjectTreeCellTest extends JavaFXTestSupport
  {
    private AsObjectTreeCell<As> underTest;

    private ExecutorService executorService;

    /**********************************************************************************************************************************************************/
    @Override
    public void start (@Nonnull final Stage stage)
      {
        executorService = Executors.newSingleThreadExecutor();
        final var cellBinder = new DefaultCellBinder(executorService);
        underTest = new AsObjectTreeCell<>(cellBinder);
        stage.setScene(new Scene(new StackPane(underTest), 100, 100));
        stage.show();
        stage.setAlwaysOnTop(true);
      }

    /**********************************************************************************************************************************************************/
    @BeforeMethod(groups = "display")
    public void setup()
      {
        SystemRoleFactory.reset();
      }

    /**********************************************************************************************************************************************************/
    @Test(groups = "display")
    public void must_set_text_from_Displayable_when_non_empty()
      {
        // given
        final var asObject = mock(As.class);
        when(asObject.asMany(Displayable.class)).thenReturn(List.of(Displayable.of("foo")));
        // when
        runSafelyAndWait("updateItem", executorService, () -> underTest.updateItem(asObject, false));
        // then
        assertThat(underTest.getText()).isEqualTo("foo");
      }

    /**********************************************************************************************************************************************************/
    @Test(groups = "display")
    public void must_set_empty_text_when_empty()
      {
        // given
        final var asObject = mock(As.class);
        when(asObject.asMany(Displayable.class)).thenReturn(List.of(Displayable.of("foo")));
         // when
        runSafelyAndWait("updateItem", executorService, () -> underTest.updateItem(asObject, true));
        // then
        assertThat(underTest.getText()).isEmpty();
      }

    // TODO: test setting of ContextMenu
  }