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
import java.util.Map;
import it.tidalwave.ui.core.BoundProperty;
import it.tidalwave.ui.core.role.Displayable;
import it.tidalwave.ui.core.role.Styleable;
import it.tidalwave.ui.core.role.UserActionProvider;
import it.tidalwave.util.As;
import it.tidalwave.util.Finder;
import it.tidalwave.role.Aggregate;
import it.tidalwave.role.SimpleComposite;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.testng.annotations.Test;
import static org.assertj.core.api.Assertions.assertThat;
/***************************************************************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **************************************************************************************************************************************************************/
public class AsAssertTest
  {
    /**********************************************************************************************************************************************************/
    @Test
    public void test_assertThat_with_As()
      {
        final var mock = Mock.newInstance("mock");
        final var assertAs = AsAssert.assertThat(mock);
        assertThat(assertAs.object).isSameAs(mock);
      }
    /**********************************************************************************************************************************************************/
    @Test
    public void test_assertThat_with_BoundProperty()
      {
        final var property = new BoundProperty<>("foo bar");
        final var objectAssert = AsAssert.assertThat(property);
        objectAssert.isEqualTo("foo bar");
      }
    /**********************************************************************************************************************************************************/
    @Test
    public void test_hasDisplayable()
      {
        // given
        final var displayable = Displayable.of("foo bar");
        final var mock = As.forObject("owner", displayable);
        // when
        final var displayableAssert = AsAssert.assertThat(mock).hasDisplayable();
        // then
        assertThat(displayableAssert.displayable).isSameAs(displayable);
      }
    /**********************************************************************************************************************************************************/
    @Test(expectedExceptions = AssertionError.class)
    public void test_hasDisplayable_failure()
      {
        // given
        final var mock = As.forObject("owner");
        // when
        AsAssert.assertThat(mock).hasDisplayable();
        // then
        // failure
      }
    /**********************************************************************************************************************************************************/
    @Test
    public void test_hasDisplayName_1()
      {
        // given
        final var mock = As.forObject("owner", Displayable.of("foo bar"));
        // when
        AsAssert.assertThat(mock).hasDisplayName("foo bar");
        // then
        // no failure
      }
    /**********************************************************************************************************************************************************/
    @Test
    public void test_hasDisplayName_2()
      {
        // given
        final var mock = As.forObject("owner", Displayable.of("foo bar"));
        // when
        AsAssert.assertThat(mock).hasDisplayName(() -> "foo bar");
        // then
        // no failure
      }
    /**********************************************************************************************************************************************************/
    @Test(expectedExceptions = AssertionError.class)
    public void test_hasDisplayName_bad()
      {
        // given
        final var mock = As.forObject("owner", Displayable.of("foo bar"));
        // then
        AsAssert.assertThat(mock).hasDisplayName("bar foo");
        // then
        // failure
      }
    /**********************************************************************************************************************************************************/
    @Test
    public void test_hasStyleable()
      {
        // given
        final var styleable = Styleable.of("foo-bar");
        final var mock = As.forObject("owner", styleable);
        // when
        final var styleableAssert = AsAssert.assertThat(mock).hasStyleable();
        // then
        assertThat(styleableAssert.styleable).isSameAs(styleable);
      }
    /**********************************************************************************************************************************************************/
    @Test(expectedExceptions = AssertionError.class)
    public void test_hasStyleable_failure()
      {
        // given
        final var mock = As.forObject("owner");
        // when
        AsAssert.assertThat(mock).hasStyleable();
      }
    /**********************************************************************************************************************************************************/
    @Test
    public void test_hasCompositeOf ()
      {
        // given
        final var composite = SimpleComposite.of(Finder.empty());
        final var mock = As.forObject("owner", composite);
        // when
        final var compositeAssert = AsAssert.assertThat(mock).hasCompositeOf(Mock.class);
        // then
        assertThat(compositeAssert.compositeType).isEqualTo(Mock.class);
        assertThat(compositeAssert.composite).isSameAs(composite);
      }
    /**********************************************************************************************************************************************************/
    @Test(expectedExceptions = AssertionError.class)
    public void test_hasCompositeOf_failure ()
      {
        // given
        final var mock = As.forObject("owner");
        // when
        AsAssert.assertThat(mock).hasCompositeOf(Mock.class);
      }
    /**********************************************************************************************************************************************************/
    @Test
    public void test_hasAggregateOf()
      {
        // given
        final var aggregate = Aggregate.of(Map.of());
        final var mock = As.forObject("owner", aggregate);
        // when
        final var aggregateAssert = AsAssert.assertThat(mock).hasAggregateOf(Mock.class);
        // then
        assertThat(aggregateAssert.aggregateType).isEqualTo(Mock.class);
        assertThat(aggregateAssert.aggregate).isSameAs(aggregate);
      }
    /**********************************************************************************************************************************************************/
    @Test(expectedExceptions = AssertionError.class)
    public void test_hasAggregateOf_failure ()
      {
        // given
        final var mock = As.forObject("owner");
        // when
        AsAssert.assertThat(mock).hasAggregateOf(Mock.class);
      }
    /**********************************************************************************************************************************************************/
    @Test
    public void test_hasUserActionProvider()
      {
        // given
        final var userActionProvider = UserActionProvider.of();
        final var mock = As.forObject("owner", userActionProvider);
        // when
        final var userActionProviderAssert = AsAssert.assertThat(mock).hasUserActionProvider();
        // then
        assertThat(userActionProviderAssert.userActionProvider).isSameAs(userActionProvider);
      }
    /**********************************************************************************************************************************************************/
    @Test(expectedExceptions = AssertionError.class)
    public void test_hasUserActionProvider_failure()
      {
        // given
        final var mock = As.forObject("owner");
        // when
        AsAssert.assertThat(mock).hasUserActionProvider();
      }
    /**********************************************************************************************************************************************************/
    @RequiredArgsConstructor @Getter
    static class Role
      {
        @Nonnull
        private final String attribute;
      }
    @Test
    public void test_hasRole()
      {
        // given
        final var mock = As.forObject("owner", new Role("foo bar"));
        // when
        final var objectAssert = AsAssert.assertThat(mock).hasRole(As.type(Role.class));
        // then
        objectAssert.hasFieldOrPropertyWithValue("attribute", "foo bar");
      }
  }
