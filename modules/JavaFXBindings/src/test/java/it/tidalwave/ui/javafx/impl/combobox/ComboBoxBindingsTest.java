/*
 * *************************************************************************************************************************************************************
 *
 * SteelBlue: DCI User Interfaces
 * http://tidalwave.it/projects/steelblue
 *
 * Copyright (C) 2015 - 2026 by Tidalwave s.a.s. (http://tidalwave.it)
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
package it.tidalwave.ui.javafx.impl.combobox;
import jakarta.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javafx.scene.control.ComboBox;
import javafx.scene.control.skin.ComboBoxListViewSkin;
import it.tidalwave.ui.core.role.PresentationModel;
import it.tidalwave.ui.core.role.Selectable;
import it.tidalwave.ui.javafx.impl.BindingTestSupport;
import it.tidalwave.ui.javafx.impl.common.DefaultCellBinder;
import org.testng.annotations.Test;
import static it.tidalwave.ui.core.role.PresentationModel._CompositeOfPresentationModel_;
import static it.tidalwave.ui.core.role.Selectable._Selectable_;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.rangeClosed;
import static org.testng.Assert.assertEquals;
import static org.mockito.Mockito.*;
/***************************************************************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **************************************************************************************************************************************************************/
public class ComboBoxBindingsTest extends BindingTestSupport<ComboBox<PresentationModel>>
  {
    private ComboBoxBindings underTest;
    /**********************************************************************************************************************************************************/
    public ComboBoxBindingsTest()
      {
        super(ComboBox::new);
      }
    /**********************************************************************************************************************************************************/
    @Override
    protected void setup (@Nonnull final DefaultCellBinder cellBinder)
      {
        underTest = new ComboBoxBindings(executor, cellBinder, windowSupplier);
      }
    /**********************************************************************************************************************************************************/
    @Test(groups = "display")
    public void must_have_the_proper_presentation_models()
      {
        // when
        runSafelyAndWait("bind", executor, () -> underTest.bind(control, pm, Optional.of(callBack)));
        // then
        assertEquals(new ArrayList<>(control.getItems()), pm.as(_CompositeOfPresentationModel_).findChildren().results());
      }
    /**********************************************************************************************************************************************************/
    @Test(groups = "display") @SuppressWarnings("unchecked")
    public void must_have_the_proper_cell_texts_when_not_hown()
      {
        // when
        runSafelyAndWait("bind", executor, () -> underTest.bind(control, pm, Optional.of(callBack)));
        // then
        final var skin = (ComboBoxListViewSkin<PresentationModel>)control.getSkin();
        final var texts = findTextsFrom(skin.getPopupContent(), s -> !s.isEmpty());
        assertEquals(texts, List.of(), texts.toString());
      }
    /**********************************************************************************************************************************************************/
    @Test(groups = "display") @SuppressWarnings("unchecked")
    public void must_have_the_proper_cell_texts_when_shown()
      {
        // when
        runSafelyAndWait("bind", executor, () -> underTest.bind(control, pm, Optional.of(callBack)));
        runSafelyAndWait("show", executor, () -> control.show());
        // then
        final var skin = (ComboBoxListViewSkin<PresentationModel>)control.getSkin();
        final var texts = findTextsFrom(skin.getPopupContent(), s -> !s.isEmpty());
        final var expected = rangeClosed(1, 10).mapToObj(i -> "Item #" + i).collect(toList());
        assertEquals(texts, expected, texts.toString());
      }
    /**********************************************************************************************************************************************************/
    @Test(groups = "display")
    public void must_have_the_proper_selection()
      {
        // given
        runSafelyAndWait("bind", executor, () -> underTest.bind(control, pm, Optional.of(callBack)));
        // when
        runSafelyAndWait("select", executor, () -> control.getSelectionModel().select(7));
        // then
        final var selectables = control.getItems().stream().map(pm -> pm.as(_Selectable_)).toArray(Selectable[]::new);
        verify(selectables[7]).select();
        rangeClosed(0, 9).filter(i -> i != 7).forEach(i -> verifyNoInteractions(selectables[i]));
      }
    // TODO: assert callbacks are not in the JavaFX thread
    // TODO: test context menus
  }
