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

import it.tidalwave.ui.core.role.Displayable;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.assertj.core.api.Assertions.assertThat;

/***************************************************************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **************************************************************************************************************************************************************/
public class DisplayableAssertTest
  {
    private DisplayableAssert underTest;

    /**********************************************************************************************************************************************************/
    @BeforeMethod
    public void setup()
      {
        final var displayable = Displayable.of("foo bar");
        underTest = DisplayableAssert.of(displayable);
      }

    /**********************************************************************************************************************************************************/
    @Test
    public void testGood1()
      {
        // when
        final var fluent = underTest.withDisplayName("foo bar");
        // then
        assertThat(fluent).isSameAs(underTest);
      }

    /**********************************************************************************************************************************************************/
    @Test
    public void testGood2()
      {
        // when
        final var fluent = underTest.withDisplayName(() -> "foo bar");
        // then
        assertThat(fluent).isSameAs(underTest);
      }

    /**********************************************************************************************************************************************************/
    @Test(expectedExceptions = AssertionError.class)
    public void testBad()
      {
        final var fluent = underTest.withDisplayName("bar foo");
        // then
        assertThat(fluent).isSameAs(underTest);
      }
  }