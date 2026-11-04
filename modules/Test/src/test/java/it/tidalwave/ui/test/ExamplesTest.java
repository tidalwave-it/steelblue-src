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
package it.tidalwave.ui.test;
import jakarta.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import it.tidalwave.ui.core.role.Displayable;
import it.tidalwave.ui.core.role.PresentationModel;
import it.tidalwave.ui.core.role.UserAction;
import it.tidalwave.ui.core.role.UserActionProvider;
import org.assertj.core.api.Assertions;
import it.tidalwave.util.As;
import it.tidalwave.util.Callback;
import it.tidalwave.util.Finder;
import it.tidalwave.role.Aggregate;
import it.tidalwave.role.SimpleComposite;
import it.tidalwave.role.spi.SystemRoleFactory;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import org.testng.annotations.Test;
import static it.tidalwave.ui.core.role.Displayable._Displayable_;
import static it.tidalwave.ui.core.role.PresentationModel._PresentationModel_;
import static it.tidalwave.ui.test.AsAssert.assertThat;
import static org.mockito.Mockito.*;
/***************************************************************************************************************************************************************
 *
 * @author Fabrizio Giudici
 *
 **************************************************************************************************************************************************************/
public class ExamplesTest
  {
    @Test
    public void test_example_code()
      {
        final var leonardo = new Object();
        final var michelangelo = new Object();
        final var action1 = UserAction.of(mock(Callback.class), List.of(Displayable.of("Show bio")));
        final var action2 = UserAction.of(mock(Callback.class), List.of(Displayable.of("Show picture gallery")));
        final var pmL = PresentationModel.of(leonardo, List.of(Displayable.of("Leonardo da Vinci"),
                                                               UserActionProvider.of(action1, action2)));
        final var pmM = PresentationModel.of(michelangelo, List.of(Displayable.of("Michelangelo Buonarroti"),
                                                                   UserActionProvider.of(action1, action2)));
        org.assertj.core.api.Assertions.
                // START SNIPPET: displayable
                        assertThat(pmL.as(_Displayable_).getDisplayName()).isEqualTo("Leonardo da Vinci");
        // END SNIPPET: displayable
        // START SNIPPET: hasDisplayable1
        assertThat(pmL).hasDisplayable().withDisplayName("Leonardo da Vinci");
        // END SNIPPET: hasDisplayable1
        // START SNIPPET: hasDisplayable2
        assertThat(pmL).hasDisplayName("Leonardo da Vinci");
        // END SNIPPET: hasDisplayable2
        // START SNIPPET: hasUserActionProvider
        assertThat(pmL).hasUserActionProvider()
                       .withActionCount(2)
                       .withNextItemSatisfying(i -> i.hasDisplayName("Show bio"))
                       .withNextItemSatisfying(i -> i.hasDisplayName("Show picture gallery"));
        // END SNIPPET: hasUserActionProvider
        final var pm2 = PresentationModel.of(leonardo, SimpleComposite.of(Finder.ofCloned(List.of(pmL, pmM))));
        // START SNIPPET: hasComposite1
        assertThat(pm2).hasCompositeOf(_PresentationModel_)
                       .withItemCount(2)
                       .withNextItemSatisfying(i -> i
                               .hasDisplayName("Leonardo da Vinci")
                               .hasUserActionProvider()
                               .withActionCount(2)
                               .withNextItemSatisfying(j -> j.hasDisplayName("Show bio"))
                               .withNextItemSatisfying(j -> j.hasDisplayName("Show picture gallery")))
                       .withNextItemSatisfying(i -> i
                               .hasDisplayName("Michelangelo Buonarroti")
                               .hasUserActionProvider()
                               .withActionCount(2)
                               .withNextItemSatisfying(j -> j.hasDisplayName("Show bio"))
                               .withNextItemSatisfying(j -> j.hasDisplayName("Show picture gallery")));
        // END SNIPPET: hasComposite1
        // START SNIPPET: hasComposite2
        assertThat(pm2).hasCompositeOf(_PresentationModel_)
                       .withItemCount(2)
                       .withEachItemSatisfying(i -> i
                               .hasUserActionProvider()
                               .withActionCount(2)
                               .withNextItemSatisfying(j -> j.hasDisplayName("Show bio"))
                               .withNextItemSatisfying(j -> j.hasDisplayName("Show picture gallery")))
                       .withNextItemSatisfying(i -> i.hasDisplayName("Leonardo da Vinci"))
                       .withNextItemSatisfying(i -> i.hasDisplayName("Michelangelo Buonarroti"));
        // END SNIPPET: hasComposite2
        final var pmA = PresentationModel.of(leonardo, Displayable.of("Michelangelo"));
        final var pmB = PresentationModel.of(leonardo, Displayable.of("Buonarroti"));
        final var pmC = PresentationModel.of(leonardo, Displayable.of("March 6, 1475"));
        final var pm3 = PresentationModel.of(leonardo, Aggregate.of(Map.of("firstName", pmA,
                                                                           "lastName", pmB,
                                                                           "birthDate", pmC)));
        // START SNIPPET: hasAggregate
        assertThat(pm3).hasAggregateOf(_PresentationModel_)
                       .withExactItemNames("firstName", "lastName", "birthDate")
                       .withNextItemSatisfying(i -> i.hasDisplayName("Michelangelo"))
                       .withNextItemSatisfying(i -> i.hasDisplayName("Buonarroti"))
                       .withNextItemSatisfying(i -> i.hasDisplayName("March 6, 1475"));
        // END SNIPPET: hasAggregate
      }
    @SuppressWarnings("this-escape")
    // START SNIPPET: Painter
    @RequiredArgsConstructor @Getter
    static class Painter implements As
      {
        @Delegate
        private final As asSupport = As.forObject(this);
        @Nonnull
        private final String name;
      }
    // END SNIPPET: Painter
    @Test
    public void test_example_code_2()
      {
        // START SNIPPET: MockSystemRoleFactory
        SystemRoleFactory.reset();
        SystemRoleFactory.set(MockSystemRoleFactory.newInstance()
                                                   .with(Painter.class, Displayable.class, o -> List.of(Displayable.of((o.getName())))));
        // END SNIPPET: MockSystemRoleFactory
        // START SNIPPET: as
        final var painter = new Painter("Michelangelo Buonarroti");
        final var displayName = painter.as(_Displayable_).getDisplayName();
        assertThat(painter).hasDisplayName("Michelangelo Buonarroti");
        SystemRoleFactory.reset(); // don't wreck other tests
        // END SNIPPET: as
        Assertions.assertThat(displayName).isEqualTo("Michelangelo Buonarroti");
      }
  }
