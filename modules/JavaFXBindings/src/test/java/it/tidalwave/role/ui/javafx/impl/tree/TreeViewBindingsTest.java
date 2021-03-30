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
package it.tidalwave.role.ui.javafx.impl.tree;

import javafx.scene.control.TreeItem;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import it.tidalwave.role.ContextManager;
import it.tidalwave.util.spi.AsDelegateProvider;
import it.tidalwave.role.spi.DefaultContextManagerProvider;
import it.tidalwave.role.ui.PresentationModel;
import it.tidalwave.role.ui.Selectable;
import it.tidalwave.role.ui.javafx.impl.common.CellBinder;
import it.tidalwave.role.ui.javafx.impl.common.DefaultCellBinder;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
public class TreeViewBindingsTest
  {
    private TreeViewBindings underTest;

    private ExecutorService executor;

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @BeforeMethod
    public void setup()
      {
        AsDelegateProvider.Locator.set(AsDelegateProvider.empty());
        ContextManager.Locator.set(new DefaultContextManagerProvider());
        executor = Executors.newSingleThreadExecutor();
        final CellBinder cellBinder = new DefaultCellBinder(executor);
        underTest = new TreeViewBindings(executor, cellBinder);
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Test
    public void treeItemChangeListener_must_callback_a_Selectable_on_selection_change()
      throws InterruptedException
      {
        // given
        final Selectable selectable = mock(Selectable.class);
        final Object datum = new Object();
        final PresentationModel oldPm = PresentationModel.of(datum, selectable);
        final PresentationModel pm = PresentationModel.of(datum, selectable);
        // when
        underTest.getSelectionListener().asTreeItemChangeListener().changed(null, new TreeItem<>(oldPm), new TreeItem<>(pm));
        // then
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);
        verify(selectable, times(1)).select();
        verifyNoMoreInteractions(selectable);
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Test
    public void treeItemChangeListener_must_do_nothing_when_there_is_no_Selectable_role()
      {
        // given
        final Object datum = new Object();
        final PresentationModel oldPm = PresentationModel.of(datum);
        final PresentationModel pm = PresentationModel.of(datum);
        // when
        underTest.getSelectionListener().asTreeItemChangeListener().changed(null, new TreeItem<>(oldPm), new TreeItem<>(pm));
        // then
        // no exceptions are thrown
      }
  }
