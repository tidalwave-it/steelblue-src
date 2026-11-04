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
package it.tidalwave.ui.util;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import it.tidalwave.ui.core.MenuBarControl;
import it.tidalwave.ui.core.MenuBarControl.MenuIndex;
import it.tidalwave.ui.core.role.UserAction;
import org.apiguardian.api.API;
import it.tidalwave.util.BundleUtilities;
import lombok.Builder;
import lombok.Getter;
import static org.apiguardian.api.API.Status.EXPERIMENTAL;
/***************************************************************************************************************************************************************
 *
 * A facility that creates commonly used {@link UserAction}s.
 *
 * @since   3.0-ALPHA-5
 * @author  Fabrizio Giudici
 *
 **************************************************************************************************************************************************************/
@API(status = EXPERIMENTAL)
public interface UserActionFactory
  {
    @Builder(toBuilder = true, buildMethodName = "buildBuilder")
    public static class UserActionParameters
      {
        // @Nonnull FIXME make it private
        private final Function<UserActionParameters, UserAction> callback;
        @Nonnull @Builder.Default @Getter
        private final Object presentation = new Object();
        @Nullable @Builder.Default
        private final MenuIndex menuIndex = null;
        @Nullable @Builder.Default
        private final String menuPlacement = null;
        @Nullable @Builder.Default @Getter
        private final String label = null;
        @Nonnull
        public MenuBarControl.MenuPlacement getComputedMenuPlacement()
          {
            return MenuBarControl.MenuPlacement.under(menuIndex == null ? Objects.requireNonNull(menuPlacement) : menuIndex.getLabel());
          }
        /*******************************************************************************************************************************************************
         * Customisation of the Lombok-generated builder.
         ******************************************************************************************************************************************************/
        public static class UserActionParametersBuilder
          {
            /***************************************************************************************************************************************************
             * {@return the new {@link UserAction}}
             **************************************************************************************************************************************************/
            @Nonnull
            public UserAction build()
              {
                return buildBuilder().build();
              }
            /***************************************************************************************************************************************************
             * {@return the new {@link UserAction} as a singleton list}
             **************************************************************************************************************************************************/
            @Nonnull
            public List<UserAction> buildAsList()
              {
                return List.of(build());
              }
            /***************************************************************************************************************************************************
             * Sets a localised label.
             * @param   clazz     the owner of the bundle
             * @param   key       the key of the resource inside the bundle
             * @param   params    the parameters (used if the string in the bundle is a {@code String} format)
             **************************************************************************************************************************************************/
            @Nonnull
            public UserActionParametersBuilder labelFromBundle (@Nonnull final Class<?> clazz, @Nonnull final String key, @Nonnull final Object... params)
              {
                return label(BundleUtilities.getMessage(clazz, key, params));
              }
          }
        /*******************************************************************************************************************************************************
         *
         ******************************************************************************************************************************************************/
        @Nonnull
        private UserAction build()
          {
            return callback.apply(this);
          }
      }
    /***********************************************************************************************************************************************************
     * Creates an action that shows the given presentation in a panel group.
     * The action only works if a message bus is installed on the system.
     * @param     presentation  the presentation
     * @return                  a fluent builder
     * @since     3.0-ALPHA-5
     **********************************************************************************************************************************************************/
    @Nonnull
    public UserActionParameters.UserActionParametersBuilder createViewActionFor (@Nonnull Object presentation);
  }
