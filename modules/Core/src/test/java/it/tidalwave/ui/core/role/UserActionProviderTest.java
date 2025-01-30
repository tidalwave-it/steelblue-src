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
package it.tidalwave.ui.core.role;

import java.util.stream.IntStream;
import it.tidalwave.util.NotFoundException;
import org.testng.annotations.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.*;
import static it.tidalwave.util.StreamUtils.zip;
import static org.testng.FileAssert.fail;
import static org.mockito.Mockito.*;

/***************************************************************************************************************************************************************
 *
 *
 **************************************************************************************************************************************************************/
public class UserActionProviderTest
  {
    private final UserAction[] ACTIONS = IntStream.range(0, 10)
                                                  .mapToObj(i -> mock(UserAction.class))
                                                  .collect(toList())
                                                  .toArray(new UserAction[0]);

    private final UserAction[] NO_ACTIONS = new UserAction[0];

    @Test
    public void works_with_actions()
      throws NotFoundException
      {
        // when
        final var underTest = UserActionProvider.of(ACTIONS);
        // then
        zip(underTest.getActions().stream(), stream(ACTIONS)).forEach(p -> assertThat(p.a).isSameAs(p.b));
        assertThat(underTest.getDefaultAction()).isSameAs(ACTIONS[0]);
        assertThat(underTest.getOptionalDefaultAction().orElseThrow()).isSameAs(ACTIONS[0]);
      }

    @Test
    public void works_with_no_actions()
      {
        // when
        final var underTest = UserActionProvider.of(NO_ACTIONS);
        // then
        assertThat(underTest.getActions()).isEmpty();
        assertThat(underTest.getOptionalDefaultAction()).isNotPresent();

        try
          {
            underTest.getDefaultAction();
            fail("Expected NotFoundException");
          }
        catch (NotFoundException e)
          {
            // ok
          }
      }
  }
