/*
 * #%L
 * *********************************************************************************************************************
 *
 * SteelBlue
 * http://steelblue.tidalwave.it - git clone git@bitbucket.org:tidalwave/steelblue-src.git
 * %%
 * Copyright (C) 2015 - 2015 Tidalwave s.a.s. (http://tidalwave.it)
 * %%
 *
 * *********************************************************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * *********************************************************************************************************************
 *
 *
 *
 * *********************************************************************************************************************
 * #L%
 */
package it.tidalwave.role.ui.javafx.impl;

import java.util.concurrent.Executors;
import it.tidalwave.util.spi.AsDelegateProvider;
import it.tidalwave.role.ContextManager;
import it.tidalwave.role.spi.DefaultContextManagerProvider;
import org.testng.annotations.BeforeMethod;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
public class DefaultJavaFXBinderTest
  {
    private DefaultJavaFXBinder underTest;

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @BeforeMethod
    public void setup()
      {
        AsDelegateProvider.Locator.set(AsDelegateProvider.empty());
        ContextManager.Locator.set(new DefaultContextManagerProvider());
        underTest = new DefaultJavaFXBinder(Executors.newSingleThreadExecutor());
      }

    // TODO: tests
  }