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
package it.tidalwave.ui.javafx.impl.tree;

import jakarta.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.control.TreeItem;
import it.tidalwave.role.spi.SystemRoleFactory;
import it.tidalwave.ui.core.role.PresentationModel;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.mockito.Mockito.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/***************************************************************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **************************************************************************************************************************************************************/
public class ObsoletePresentationModelDisposerTest
  {
    private ObsoletePresentationModelDisposer underTest;

    private List<PresentationModel> allPMs;

    @BeforeMethod
    public void setup()
      {
        SystemRoleFactory.reset();
        underTest = new ObsoletePresentationModelDisposer();
        allPMs = new ArrayList<>();
      }

    @Test
    public void changed_must_dispose_all_PresentationModels()
      {
        // given
        final var treeItem = createTreeItemWithChildren(0);
        assertThat(allPMs.size(), is(85));
        // when
        underTest.changed(null, treeItem, null);
        // then
        for (final var pm : allPMs)
          {
            verify(pm).dispose();
            verifyNoMoreInteractions(pm);
          }
      }

    @Nonnull
    private TreeItem<PresentationModel> createTreeItemWithChildren (final int level)
      {
        final var pm = mock(PresentationModel.class);
        allPMs.add(pm);
        final var treeItem = new TreeItem<>(pm);

        if (level < 3)
          {
            for (var n = 0; n < 4; n++)
              {
                treeItem.getChildren().add(createTreeItemWithChildren(level + 1));
              }
          }

        return treeItem;
      }
  }