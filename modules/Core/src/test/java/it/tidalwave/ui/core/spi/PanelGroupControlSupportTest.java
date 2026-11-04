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
package it.tidalwave.ui.core.spi;
import jakarta.annotation.Nonnull;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import org.springframework.beans.factory.BeanFactory;
import it.tidalwave.ui.core.PanelGroupControl.Group;
import it.tidalwave.ui.core.PanelGroupProvider;
import it.tidalwave.ui.core.message.PanelShowRequest;
import it.tidalwave.util.As;
import org.testng.annotations.Test;
import static it.tidalwave.ui.core.PanelGroupControl.DefaultGroups.*;
import static it.tidalwave.ui.core.PanelGroupControl.Options.*;
import static org.mockito.Mockito.*;
/***************************************************************************************************************************************************************
 *
 * @author      Fabrizio Giudici
 *
 **************************************************************************************************************************************************************/
public class PanelGroupControlSupportTest
  {
    private static class MockPanel {}
    private static class MockNode {}
    static class Fixture extends PanelGroupControlSupport<MockPanel, MockNode>
      {
        private Fixture (@Nonnull final Function<As, Collection<PanelGroupProvider<MockNode>>> pgProvider)
          {
            super(pgProvider, mock(BeanFactory.class), "");
          }
        @Override
        protected void assemble (@Nonnull final Group group,
                                 @Nonnull final List<? extends PanelGroupProvider<MockNode>> panelProviders,
                                 @Nonnull final MockPanel topContainer,
                                 @Nonnull final Map<Group, List<Options>> groupOptions,
                                 @Nonnull final List<Options> options)
          {
            // not needed in tests
          }
        @Override
        protected void onShowRequest (@Nonnull final PanelShowRequest panelShowRequest)
          {
            // not needed in tests
          }
        @Override
        public void show (@Nonnull final Object object)
          {
            // not needed in tests
          }
      }
    @Test @SuppressWarnings("unchecked")
    public void test()
      {
        // given
        final var panelLeft   = mock(MockPanel.class);
        final var panelCenter = mock(MockPanel.class);
        final var panelBottom = mock(MockPanel.class);
        final var panelRight  = mock(MockPanel.class);
        final var ppLeft1   = createMock(LEFT,   "Left 1");
        final var ppLeft2   = createMock(LEFT,   "Left 2");
        final var ppRight1  = createMock(RIGHT,  "Right 1");
        final var ppRight2  = createMock(RIGHT,  "Right 2");
        final var ppCenter1 = createMock(CENTER, "Center 1");
        final var ppCenter2 = createMock(CENTER, "Center 2");
        final var ppBottom1 = createMock(BOTTOM, "Bottom 1");
        final var ppBottom2 = createMock(BOTTOM, "Bottom 2");
        final var underTest = spy(new Fixture(ignored -> List.of(ppLeft1, ppLeft2, ppRight1, ppRight2, ppCenter1, ppCenter2, ppBottom1, ppBottom2)));
        final var configuration = underTest.config()
                                           .withGroup(LEFT,   panelLeft,   ALWAYS_WRAP)
                                           .withGroup(CENTER, panelCenter, ALWAYS_WRAP)
                                           .withGroup(BOTTOM, panelBottom, ALWAYS_WRAP)
                                           .withGroup(RIGHT,  panelRight,  ALWAYS_WRAP)
                                           .withOptions(DISABLE_ACCORDION_ANIMATION);
        // when
        underTest.setup(configuration);
        // then
        verify(underTest).assemble(eq(LEFT),   eq(List.of(ppLeft1,   ppLeft2)),   same(panelLeft),   any(Map.class), any(List.class));
        verify(underTest).assemble(eq(RIGHT),  eq(List.of(ppRight1,  ppRight2)),  same(panelRight),  any(Map.class), any(List.class));
        verify(underTest).assemble(eq(CENTER), eq(List.of(ppCenter1, ppCenter2)), same(panelCenter), any(Map.class), any(List.class));
        verify(underTest).assemble(eq(BOTTOM), eq(List.of(ppBottom1, ppBottom2)), same(panelBottom), any(Map.class), any(List.class));
      }
    @Nonnull
    private static PanelGroupProviderSupport<MockNode> createMock (@Nonnull final Group group, @Nonnull final String label)
      {
        return new PanelGroupProviderSupport<>(PanelGroupProviderSupport.<MockNode>parameters()
                                               .group(group)
                                               .label(label)
                                               .presentation(new Object())
                                               .supplier(MockNode::new)) {};
      }
  }
