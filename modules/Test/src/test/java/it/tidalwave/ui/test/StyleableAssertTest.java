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
import it.tidalwave.ui.core.role.Styleable;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
/***************************************************************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **************************************************************************************************************************************************************/
public class StyleableAssertTest
  {
    private StyleableAssert underTest;
    /**********************************************************************************************************************************************************/
    @BeforeMethod
    public void setup()
      {
        final var styleable = Styleable.of("style-1", "style-2", "style-3");
        underTest = StyleableAssert.of(styleable);
      }
    /**********************************************************************************************************************************************************/
    @Test
    public void testGood()
      {
        // when
        underTest.withExactStyles("style-1", "style-2", "style-3");
      }
    /**********************************************************************************************************************************************************/
    @Test(expectedExceptions = AssertionError.class)
    public void testBad()
      {
        // then
        underTest.withExactStyles("style-1", "style-2");
      }
  }
