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
package it.tidalwave.ui.core.role.spi;

import javax.annotation.CheckForNull;
import jakarta.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import it.tidalwave.util.As;
import it.tidalwave.util.Id;
import it.tidalwave.util.RoleFactory;
import it.tidalwave.role.Identifiable;
import it.tidalwave.role.SimpleComposite;
import it.tidalwave.role.spi.SystemRoleFactory;
import it.tidalwave.ui.core.role.PresentationModel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static java.util.Collections.emptyList;
import static it.tidalwave.util.Parameters.r;
import static it.tidalwave.ui.core.role.PresentationModel._SimpleCompositeOfPresentationModel_;
import static org.testng.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

/***************************************************************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **************************************************************************************************************************************************************/
@Slf4j
public class SimpleCompositePresentableTest
  {
    /***********************************************************************************************************************************************************
     * 
     **********************************************************************************************************************************************************/
    public static class MockDatum implements As, Identifiable
      {
        @Getter @Nonnull
        private final Id id;

        @CheckForNull
        private SimpleComposite<MockDatum> composite;

        @Getter
        private List<MockDatum> children = new ArrayList<>();

        public MockDatum (@Nonnull final String id)
          {
            this.id = new Id(id);
          }

        @Nonnull
        public MockDatum withChildren (@Nonnull final MockDatum ... children)
          {
            return withChildren(List.of(children));
          }

        @Nonnull
        public MockDatum withChildren (@Nonnull final List<MockDatum> children)
          {
            composite = SimpleComposite.ofCloned(this.children = children);
            return this;
          }

        @Override @Nonnull
        public <T> Optional<T> maybeAs (@Nonnull final Class<? extends T> roleType)
          {
            return roleType.equals(SimpleComposite.class) && (composite != null)
                    ? Optional.of(roleType.cast(composite))
                    : Optional.empty();
          }

        @Override @Nonnull
        public <T> Collection<T> asMany (@Nonnull final Class<? extends T> roleType)
          {
            if (roleType.equals(SimpleComposite.class) && (composite != null))
              {
                return new ArrayList<>(List.of(roleType.cast(composite)));
              }

            return new ArrayList<>();
          }

        @Override @Nonnull
        public String toString()
          {
            return String.format("MockDatum(%s)", id);
          }
      }

    /***********************************************************************************************************************************************************
     * 
     **********************************************************************************************************************************************************/
    static class MockRole1
      {
      }

    /***********************************************************************************************************************************************************
     * 
     **********************************************************************************************************************************************************/
    @Getter @RequiredArgsConstructor
    static class MockRole2
      {
        @Nonnull
        private final MockDatum datum;
      }

    /***********************************************************************************************************************************************************
     * 
     **********************************************************************************************************************************************************/
    static class MockRoleFactory implements RoleFactory<MockDatum, MockRole2>
      {
        @Override @Nonnull
        public Optional<MockRole2> createRoleFor (@Nonnull final MockDatum datum)
          {
            return Optional.of(new MockRole2(datum));
          }
      }

    /***********************************************************************************************************************************************************
     * 
     **********************************************************************************************************************************************************/
    @BeforeMethod
    public void setup()
      {
        SystemRoleFactory.reset();
      }

    /***********************************************************************************************************************************************************
     * 
     **********************************************************************************************************************************************************/
    @Test
    public void must_create_a_PresentationModel_containing_the_proper_children()
      {
        // given
        final var c1 = new MockDatum("c1");
        final var c2 = new MockDatum("c2");
        final var c3 = new MockDatum("c3");

        final var b1 = new MockDatum("b1").withChildren(c1, c2, c3);
        final var b2 = new MockDatum("b2");
        final var b3 = new MockDatum("b3");

        final var a = new MockDatum("a").withChildren(b1, b2, b3);

        final var underTest = new SimpleCompositePresentable(a, new DefaultPresentationModelFactory());
        final var role1 = new MockRole1();
        final var roleFactory = new MockRoleFactory();
        // when
        final var pm = underTest.createPresentationModel(r(role1, roleFactory));
        // then
        assertProperPresentationModel("", pm, a);
      }

    /***********************************************************************************************************************************************************
     * 
     **********************************************************************************************************************************************************/
    private void assertProperPresentationModel (@Nonnull final String indent,
                                                @Nonnull final PresentationModel pm,
                                                @Nonnull final MockDatum datum)
      {
        log.debug("assertProperPresentationModel() - {} {}, {}", indent, pm, datum);
        pm.as(MockRole1.class);                        // must not throw AsException
        final var role = pm.as(MockRole2.class); // must not throw AsException

        assertThat(role.getDatum(), is(sameInstance(datum)));

        final var childrenPm = pm.maybeAs(_SimpleCompositeOfPresentationModel_)
                                 .map(c -> c.findChildren().results())
                                 .orElse(emptyList());
        final var notPMs = new ArrayList<>(childrenPm);
        notPMs.removeIf(object -> object instanceof PresentationModel);

        if (!notPMs.isEmpty())
          {
            fail("Unexpected objects that are not PresentationModel: " + notPMs);
          }

        final var childrenData = datum.getChildren();

        assertThat(childrenPm.size(), is(childrenData.size()));

        for (var i = 0; i < childrenPm.size(); i++)
          {
            assertProperPresentationModel(indent + "    ", childrenPm.get(i), childrenData.get(i));
          }
      }
  }