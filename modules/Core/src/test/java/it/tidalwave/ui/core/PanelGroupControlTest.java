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
package it.tidalwave.ui.core;

import jakarta.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import org.testng.annotations.Test;
import static it.tidalwave.ui.core.PanelGroupControl.Options.*;
import static it.tidalwave.ui.core.PanelGroupControl.DefaultGroups.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/***************************************************************************************************************************************************************
 *
 * @author      Fabrizio Giudici
 *
 **************************************************************************************************************************************************************/
public class PanelGroupControlTest
  {
    static class Fixture implements PanelGroupControl<MockPanel>
      {
        @Override
        public void setup (@Nonnull final Configuration<MockPanel> configuration)
          {
            // not needed in tests
          }

        @Override
        public void show (@Nonnull final Object object)
          {
            // not needed in tests
          }
      }

    static class MockPanel {}

    @Test
    public void test_Configuration()
      {
        // given
        final var panelLeft = mock(MockPanel.class);
        final var panelCenter = mock(MockPanel.class);
        final var panelBottom = mock(MockPanel.class);
        final var panelRight = mock(MockPanel.class);
        final var f = new Fixture(); // needed only for calling config()
        // when
        final var underTest = f.config()
                               .withGroup(LEFT, panelLeft, ALWAYS_WRAP)
                               .withGroup(CENTER, panelCenter, ALWAYS_WRAP)
                               .withGroup(BOTTOM, panelBottom, ALWAYS_WRAP)
                               .withGroup(RIGHT, panelRight, ALWAYS_WRAP)
                               .withOptions(DISABLE_ACCORDION_ANIMATION);
        // then
        assertThat(underTest.getTopContainersByGroup()).isEqualTo(Map.of(LEFT, panelLeft,
                                                                  CENTER, panelCenter,
                                                                  BOTTOM, panelBottom,
                                                                  RIGHT, panelRight));
        final var optionsMap = underTest.getGroupOptions();
        assertThat(optionsMap).hasSize(4)
                              .containsEntry(LEFT,   List.of(ALWAYS_WRAP))
                              .containsEntry(CENTER, List.of(ALWAYS_WRAP))
                              .containsEntry(BOTTOM, List.of(ALWAYS_WRAP))
                              .containsEntry(RIGHT,  List.of(ALWAYS_WRAP));
        assertThat(underTest.getOptions()).isEqualTo(List.of(DISABLE_ACCORDION_ANIMATION));
      }
  }