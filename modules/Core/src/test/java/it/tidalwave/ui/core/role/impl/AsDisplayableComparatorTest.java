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
package it.tidalwave.ui.core.role.impl;

import jakarta.annotation.Nonnull;
import java.util.Comparator;
import java.util.List;
import it.tidalwave.util.As;
import it.tidalwave.ui.core.role.Displayable;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import static it.tidalwave.util.mock.MockAsFactory.mockWithAs;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/***************************************************************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **************************************************************************************************************************************************************/
public class AsDisplayableComparatorTest
  {
    private final As a  = createMockAs(new DefaultDisplayable("a", "a"));
    private final As b  = createMockAs(new DefaultDisplayable("b", "b"));
    private final As c1 = createMockAs(new DefaultDisplayable("c", "c"));
    private final As c2 = createMockAs(new DefaultDisplayable("c", "c"));

    @Test(dataProvider = "data")
    public void test (@Nonnull final As a1, @Nonnull final As a2, final int expectedResult)
      {
        // given
        final Comparator<As> underTest = AsDisplayableComparator.getInstance();
        // when
        final var actualResult = underTest.compare(a1, a2);
        // then
        assertThat(actualResult, is(expectedResult));
      }

    @Nonnull
    private static As createMockAs (@Nonnull final Displayable displayable)
      {
        return mockWithAs(As.class, List.of(displayable));
      }

    @DataProvider
    private Object[][] data()
      {
        return new Object[][]
          {
            {  a,  b, -1 },
            {  b,  a, +1 },
            {  a,  a,  0 },
            { c1, c2,  0 },
            { c2, c1,  0 }
          };
      }
  }
