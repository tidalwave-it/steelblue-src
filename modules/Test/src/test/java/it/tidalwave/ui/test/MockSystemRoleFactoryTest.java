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
package it.tidalwave.ui.test;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.testng.annotations.Test;
import static org.assertj.core.api.Assertions.assertThat;
/***************************************************************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **************************************************************************************************************************************************************/
public class MockSystemRoleFactoryTest
  {
    static class Mock1 {}
    static class Mock2 {}
    static interface Role1 {}
    static interface Role2 {}
    @RequiredArgsConstructor @Getter @ToString
    static class MockRole1a implements Role1 { final Object owner; }
    @RequiredArgsConstructor @Getter @ToString
    static class MockRole1b implements Role1 { final Object owner; }
    @RequiredArgsConstructor @Getter @ToString
    static class MockRole2a implements Role2 { final Object owner; }
    @RequiredArgsConstructor @Getter @ToString
    static class MockRole2b implements Role2 { final Object owner; }
    @Test
    public void test()
      {
        // given
        final var mock1 = new Mock1();
        final var mock2 = new Mock2();
        final var underTest = MockSystemRoleFactory.newInstance()
                                                   .with(Mock1.class, Role1.class, o -> List.of(new MockRole1a(o), new MockRole1b(o)))
                                                   .with(Mock1.class, Role2.class, o -> List.of(new MockRole2a(o), new MockRole2b(o)))
                                                   .with(Mock2.class, Role1.class, o -> List.of(new MockRole1a(o), new MockRole1b(o)))
                                                   .with(Mock2.class, Role2.class, o -> List.of(new MockRole2a(o)));
        // when
        final var m1r1 = underTest.findRoles(mock1, Role1.class);
        final var m1r2 = underTest.findRoles(mock1, Role2.class);
        final var m2r1 = underTest.findRoles(mock2, Role1.class);
        final var m2r2 = underTest.findRoles(mock2, Role2.class);
        // then
        assertThat(m1r1).hasExactlyElementsOfTypes(MockRole1a.class, MockRole1b.class)
                        .allSatisfy(r -> assertThat(r).hasFieldOrPropertyWithValue("owner", mock1));
        assertThat(m1r2).hasExactlyElementsOfTypes(MockRole2a.class, MockRole2b.class)
                        .allSatisfy(r -> assertThat(r).hasFieldOrPropertyWithValue("owner", mock1));
        assertThat(m2r1).hasExactlyElementsOfTypes(MockRole1a.class, MockRole1b.class)
                        .allSatisfy(r -> assertThat(r).hasFieldOrPropertyWithValue("owner", mock2));
        assertThat(m2r2).hasExactlyElementsOfTypes(MockRole2a.class)
                        .allSatisfy(r -> assertThat(r).hasFieldOrPropertyWithValue("owner", mock2));
      }
  }
