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
import it.tidalwave.util.Finder;
import it.tidalwave.role.SimpleComposite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.assertj.core.api.Assertions.assertThat;
/***************************************************************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **************************************************************************************************************************************************************/
public class CompositeAssertTest
  {
    private CompositeAssert<Mock> underTest;
    private Mock childA;
    private Mock childB;
    private Mock childC;
    /**********************************************************************************************************************************************************/
    @BeforeMethod
    public void setup()
      {
        childA = Mock.newInstance("childA");
        childB = Mock.newInstance("childB");
        childC = Mock.newInstance("childC");
        final var composite = SimpleComposite.of(Finder.ofSupplier(() -> List.of(childA, childB, childC)));
        underTest = CompositeAssert.of(Mock.class, composite);
      }
    /**********************************************************************************************************************************************************/
    @Test
    public void testGood()
      {
        // when
        final var enumerator = underTest.withItemCount(3);
        // then
        assertThat(enumerator.itemType).isEqualTo(Mock.class);
        assertThat(enumerator.items).containsExactly(childA, childB, childC);
      }
    /**********************************************************************************************************************************************************/
    @Test(expectedExceptions = AssertionError.class)
    public void testBad()
      {
        // when
        underTest.withItemCount(2);
        // then assertion failed
      }
  }
