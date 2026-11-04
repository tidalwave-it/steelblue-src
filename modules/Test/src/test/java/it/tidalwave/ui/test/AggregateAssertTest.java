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
import java.util.Map;
import it.tidalwave.role.Aggregate;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.assertj.core.api.Assertions.assertThat;
/***************************************************************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **************************************************************************************************************************************************************/
public class AggregateAssertTest
  {
    private AggregateAssert<Mock> underTest;
    private Mock aggregateA;
    private Mock aggregateB;
    private Mock aggregateC;
    /**********************************************************************************************************************************************************/
    @BeforeMethod
    public void setup()
      {
        aggregateA = Mock.newInstance("aggregateA");
        aggregateB = Mock.newInstance("aggregateB");
        aggregateC = Mock.newInstance("aggregateC");
        final var aggregate = Aggregate.of(Map.of("a", aggregateA, "b", aggregateB, "c", aggregateC));
        underTest = AggregateAssert.of(Mock.class, aggregate);
      }
    /**********************************************************************************************************************************************************/
    @Test
    public void test_good()
      {
        // when
        final var enumerator = underTest.withExactItemNames("a", "b", "c");
        // then
        assertThat(enumerator.itemType).isEqualTo(Mock.class);
        assertThat(enumerator.items).containsExactly(aggregateA, aggregateB, aggregateC);
      }
    /**********************************************************************************************************************************************************/
    @Test(expectedExceptions = AssertionError.class)
    public void test_bad()
      {
        // when
        underTest.withExactItemNames("a", "c");
        // then assertion failed
      }
  }
