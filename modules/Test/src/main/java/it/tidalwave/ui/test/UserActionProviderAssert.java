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
import jakarta.annotation.Nonnull;
import it.tidalwave.ui.core.role.UserAction;
import it.tidalwave.ui.core.role.UserActionProvider;
import org.apiguardian.api.API;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import static org.assertj.core.api.Assertions.assertThat;
import static lombok.AccessLevel.PROTECTED;
/***************************************************************************************************************************************************************
 *
 * Assertions for the {@link UserActionProvider} role.
 * @since   3.0-ALPHA-3
 * @author  Fabrizio Giudici
 *
 **************************************************************************************************************************************************************/
@API(status = API.Status.EXPERIMENTAL)
@RequiredArgsConstructor(staticName = "of", access = PROTECTED) @ToString
public class UserActionProviderAssert
  {
    /** The UserActionProvider under inspection. */
    @Nonnull
    protected final UserActionProvider userActionProvider;
    /***********************************************************************************************************************************************************
     * Asserts that the object under inspection has the given number of actions.
     * @param   count           the expected count of actions
     * @return                  an object for fluent further assertions
     **********************************************************************************************************************************************************/
    @Nonnull
    public ItemsAssert<UserAction> withActionCount (final int count)
      {
        final var userActions = userActionProvider.getActions();
        assertThat(userActions).hasSize(count);
        return ItemsAssert.of(UserAction.class, userActions);
      }
  }
