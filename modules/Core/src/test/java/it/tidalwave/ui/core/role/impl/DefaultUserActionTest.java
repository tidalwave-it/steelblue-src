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
package it.tidalwave.ui.core.role.impl;

import java.util.List;
import it.tidalwave.util.Callback;
import it.tidalwave.ui.core.role.Displayable;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;
import static org.mockito.Mockito.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/***************************************************************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **************************************************************************************************************************************************************/
@Slf4j
public class DefaultUserActionTest
  {
    final Callback callback = () -> log.info("CALLBACK");

    /**********************************************************************************************************************************************************/
    @Test
    public void test_toString_without_Displayable()
      {
        // given
        final var underTest = new DefaultUserAction(callback, List.of());
        // when
        final var string = underTest.toString();
        // then
        assertThat(string, is("DefaultUserAction@" + Integer.toHexString(System.identityHashCode(underTest))));
      }

    /**********************************************************************************************************************************************************/
    @Test
    public void test_toString_with_Displayable()
      {
        // given
        final var underTest = new DefaultUserAction(callback, List.of(Displayable.of("Action")));
        // when
        final var string = underTest.toString();
        // then
        assertThat(string, is("DefaultUserAction[\"Action\"]"));
      }

    /**********************************************************************************************************************************************************/
    @Test
    public void test_toString_with_broken_Displayable()
      {
        // given
        final var displayable = mock(Displayable.class);
        when(displayable.getDisplayName()).thenThrow(new RuntimeException("purportedly genereated exception"));
        final var underTest = new DefaultUserAction(callback, List.of(displayable));
        // when
        final var string = underTest.toString();
        // then
        assertThat(string, is("DefaultUserAction@" + Integer.toHexString(System.identityHashCode(underTest)) + ",broken"));
      }
  }
