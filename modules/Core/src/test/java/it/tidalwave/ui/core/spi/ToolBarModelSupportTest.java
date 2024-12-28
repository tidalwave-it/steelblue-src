/*
 * *************************************************************************************************************************************************************
 *
 * SteelBlue: DCI User Interfaces
 * http://tidalwave.it/projects/steelblue
 *
 * Copyright (C) 2015 - 2024 by Tidalwave s.a.s. (http://tidalwave.it)
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
package it.tidalwave.ui.core.spi;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.function.Supplier;
import it.tidalwave.role.ui.UserAction;
import org.testng.annotations.Test;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class Binder
  {
  }

class Control
  {
  }

class Button
  {
  }

class UnderTest extends ToolBarModelSupport<Binder, Control>
  {
    public UnderTest (@Nonnull final Supplier<Collection<? extends UserAction>> userActionsSupplier)
      {
        super(userActionsSupplier);
      }

    @Override
    protected void populateImpl (@Nonnull final Binder binder, @Nonnull final Control toolBar)
      {

      }
  }

/***************************************************************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **************************************************************************************************************************************************************/
public class ToolBarModelSupportTest
  {
    private final TestUserActions a = new TestUserActions();

    @Test
    public void test_populate()
      {
//        // when
//        underTest.populateImpl(binder, control);
//        // then
//        final var buttons = control.getItems();
//        assertThat(buttons.size(), is(3));
//        final var button1 = (Button)buttons.get(0);
//        final var button2 = (Button)buttons.get(1);
//        final var button3 = (Button)buttons.get(2);
//
//        verify(binder).bind(button1, actionFileOpen);
//        verify(binder).bind(button2, actionFileClose);
//        verify(binder).bind(button3, actionFileCloseAll);
//        verifyNoMoreInteractions(binder);
      }
  }
