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
import java.util.Optional;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import it.tidalwave.ui.core.role.PresentationModel;
import it.tidalwave.ui.core.role.Selectable;
import it.tidalwave.ui.javafx.impl.BindingTestSupport;
import it.tidalwave.ui.javafx.impl.common.DefaultCellBinder;
import org.testng.annotations.Test;
import static it.tidalwave.ui.core.role.PresentationModel._CompositeOfPresentationModel_;
import static it.tidalwave.ui.core.role.Selectable._Selectable_;
import static org.assertj.core.api.Assertions.assertThat;
import static java.util.stream.Collectors.*;
import static java.util.stream.IntStream.rangeClosed;
import static org.testng.Assert.*;
import static org.mockito.Mockito.*;

/***************************************************************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **************************************************************************************************************************************************************/
public class TreeViewBindingsTest extends BindingTestSupport<TreeView<PresentationModel>>
  {
    private TreeViewBindings underTest;

    /**********************************************************************************************************************************************************/
    public TreeViewBindingsTest()
      {
        super(TreeView::new);
      }

    /**********************************************************************************************************************************************************/
    @Override
    protected void setup (@Nonnull final DefaultCellBinder cellBinder)
      {
        underTest = new TreeViewBindings(executor, cellBinder, windowSupplier);
      }

    /**********************************************************************************************************************************************************/
    @Test(groups = "display")
    public void must_have_the_proper_presentation_models_when_not_expanded()
      {
        // when
        runSafelyAndWait("bind", executor, () -> underTest.bind(control, pm, Optional.of(callBack)));
        // then
        final var actual = control.getRoot().getChildren().stream().map(TreeItem::getValue).collect(toList());
        assertEquals(actual, List.of());
      }

    /**********************************************************************************************************************************************************/
    @Test(groups = "display")
    public void must_have_the_proper_presentation_models_when_expanded()
      {
        // when
        runSafelyAndWait("bind", executor, () -> underTest.bind(control, pm, Optional.of(callBack)));
        runSafelyAndWait("expand root", executor, () -> control.getRoot().setExpanded(true));
        // then
        final var actual = control.getRoot().getChildren().stream().map(TreeItem::getValue).collect(toList());
        assertEquals(actual, pm.as(_CompositeOfPresentationModel_).findChildren().results());
      }

    /***********************************************************************************************************************************************************
     *
     **********************************************************************************************************************************************************/
    @Test(groups = "display")
    public void must_have_the_proper_cell_texts_when_not_expanded()
      {
        // given
        runSafelyAndWait("bind", executor, () -> underTest.bind(control, pm, Optional.of(callBack)));
        // then
        // TODO: assert(control.isShowRoot()).isFalse(); // TODO test variant when Visibility.INVISIBLE is passed in the parent PM
        final var texts = findTextsFrom(control.getSkin(), s -> !s.isEmpty());
        assertEquals(texts, List.of());
      }

    /***********************************************************************************************************************************************************
     *
     **********************************************************************************************************************************************************/
    @Test(groups = "display")
    public void must_have_the_proper_cell_texts_when_expanded()
      {
        // given
        runSafelyAndWait("bind", executor, () -> underTest.bind(control, pm, Optional.of(callBack)));
        runSafelyAndWait("expand root", executor, () -> control.getRoot().setExpanded(true));
        // then
        assertThat(control.isShowRoot()).isTrue(); // TODO test variant when Visibility.INVISIBLE is passed in the parent PM
        final var texts = findTextsFrom(control.getSkin(), s -> !s.isEmpty());
        final var expected = rangeClosed(1, 10).mapToObj(i -> "Item #" + i).collect(toList());
        assertEquals(texts, expected);
      }

    /***********************************************************************************************************************************************************
     *
     **********************************************************************************************************************************************************/
    @Test(groups = "display")
    public void must_have_the_proper_selection()
      {
        // given
        runSafelyAndWait("bind", executor, () -> underTest.bind(control, pm, Optional.of(callBack)));
        runSafelyAndWait("expand root", executor, () -> control.getRoot().setExpanded(true));
        // when
        runSafelyAndWait("select", executor, () -> control.getSelectionModel().select(3 + 1)); // plus 1 because #0 is the root
        // then
        final var selectables = control.getRoot()
                                       .getChildren()
                                       .stream()
                                       .map(i -> i.getValue().as(_Selectable_))
                                       .toArray(Selectable[]::new);
        verify(selectables[3]).select();
        rangeClosed(0, 9).filter(i -> i != 3).forEach(i -> verifyNoInteractions(selectables[i]));
      }

    /***********************************************************************************************************************************************************
     *
     **********************************************************************************************************************************************************/
    @Test(groups = "display")
    public void treeItemChangeListener_must_callback_a_Selectable_on_selection_change()
      throws InterruptedException
      {
        // given
        final var selectable = mock(Selectable.class);
        final var datum = new Datum("datum");
        final var oldPm = PresentationModel.of(datum, selectable);
        final var pm = PresentationModel.of(datum, selectable);
        // when
        runSafelyAndWait("changed", executor, () -> underTest.getSelectionListener().asTreeItemChangeListener().changed(null, new TreeItem<>(oldPm), new TreeItem<>(pm)));
        // then
//        executor.shutdown();
//        executor.awaitTermination(5, TimeUnit.SECONDS);
        verify(selectable, times(1)).select();
        verifyNoMoreInteractions(selectable);
      }

    /***********************************************************************************************************************************************************
     *
     **********************************************************************************************************************************************************/
    @Test(groups = "display")
    public void treeItemChangeListener_must_do_nothing_when_there_is_no_Selectable_role()
      {
        // given
        final var datum = new Datum("datum");
        final var oldPm = PresentationModel.of(datum);
        final var pm = PresentationModel.of(datum);
        // when
        runSafelyAndWait("changed", executor, () -> underTest.getSelectionListener().asTreeItemChangeListener().changed(null, new TreeItem<>(oldPm), new TreeItem<>(pm)));
        // then
        // no exceptions are thrown
      }
  }
