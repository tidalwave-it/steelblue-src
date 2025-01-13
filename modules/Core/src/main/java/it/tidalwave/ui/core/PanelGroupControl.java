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
package it.tidalwave.ui.core;

import jakarta.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apiguardian.api.API;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import static org.apiguardian.api.API.Status.EXPERIMENTAL;
import static lombok.AccessLevel.PRIVATE;

/***************************************************************************************************************************************************************
 *
 * This service looks up {@link PanelGroupProvider}s that describe portions of the User Interface and places them in the desired region of the main window.
 * Calling the method {@link #setup(Configuration)} with the proper configuration, the desired panel groups can be bound to existing UI controls. The runtime
 * will scan for instances of {@link PanelGroupProvider} that declare and instantiate panels to be associated to each group.
 * Calling the method {@link #show(Object)} one of possibly many panels in the same group is brought to visibility. If the system includes a
 * message bus implementing {@link it.tidalwave.messagebus.MessageBus}, it is also possible to pick the shown panel by publishing the event
 * {@link it.tidalwave.ui.core.message.PanelShowRequest}; the event {@link it.tidalwave.ui.core.message.PanelShownNotification} will be published back to
 * confirm the operation.
 *
 * @param   <T> the concrete type of the top container (depending on the UI technology)
 * @since       2.0-ALPHA-3
 * @see         PanelGroupProvider
 * @author      Fabrizio Giudici
 *
 **************************************************************************************************************************************************************/
@API(status = EXPERIMENTAL)
public interface PanelGroupControl<T>
  {
    /***********************************************************************************************************************************************************
     * The group in the main window.
     **********************************************************************************************************************************************************/
    public static interface Group {}

    /***********************************************************************************************************************************************************
     * Standard groups.
     **********************************************************************************************************************************************************/
    public enum DefaultGroups implements Group
      {
        LEFT, RIGHT, CENTER, BOTTOM
      }

    /***********************************************************************************************************************************************************
     * A bag of configuration settings for {@code PanelGroupProvider}.
     **********************************************************************************************************************************************************/
    @RequiredArgsConstructor(access = PRIVATE) @Getter @ToString
    public static class Configuration<T>
      {
        @Nonnull
        private final Map<Group, T> topContainersByGroup;

        @Nonnull
        private final Map<Group, List<Options>> groupOptions;

        @Nonnull
        private final List<Options> options;

        /*******************************************************************************************************************************************************
         * Specifies the configuration for a group.
         * @see       Options
         * @param     group           the group
         * @param     topContainer    the concrete UI container that will be associated to the group
         * @param     groupOptions    {@link Options} for this group
         * @return                    a copy of itself in chaining invocation fashion
         ******************************************************************************************************************************************************/
        @Nonnull
        public Configuration<T> withGroup (@Nonnull final Group group, @Nonnull final T topContainer, @Nonnull final Options ... groupOptions)
          {
            final var tcbs = new HashMap<>(topContainersByGroup);
            final var so = new HashMap<>(this.groupOptions);
            tcbs.put(group, topContainer);
            so.computeIfAbsent(group, s -> new ArrayList<>()).addAll(List.of(groupOptions));
            return new Configuration<>(tcbs, so, this.options);
          }

        /*******************************************************************************************************************************************************
         * Specifies global configuration {@link Options} that are applied to all groups.
         * @see       Options
         * @param     options         the options
         * @return                    a copy of itself in chaining invocation fashion
         ******************************************************************************************************************************************************/
        @Nonnull
        public final Configuration<T> withOptions (@Nonnull final Options ... options)
          {
            return new Configuration<>(topContainersByGroup, groupOptions, List.of(options));
          }
      }

    /***********************************************************************************************************************************************************
     * Options for the {@link #setup(Configuration)} method.
     * @see   Configuration#withGroup(Group, Object, Options...) 
     * @see   Configuration#withOptions(Options...)
     **********************************************************************************************************************************************************/
    public enum Options
      {
        /** Use a wrapper container even when there is a single element in a group. */
        ALWAYS_WRAP,
        /** Use an Accordion wrapper. */
        USE_ACCORDION,
        /** Disable animation when expanding/collapsing the Accordion. */
        DISABLE_ACCORDION_ANIMATION
      }

    /***********************************************************************************************************************************************************
     * Sets up panel groups according to the given {@link Configuration}.
     * @param   configuration   the configuration
     **********************************************************************************************************************************************************/
    public void setup (@Nonnull final Configuration<T> configuration);

    /***********************************************************************************************************************************************************
     * Sets the shown object in a group.
     * @param   object    the object to show
     **********************************************************************************************************************************************************/
    public void show (@Nonnull Object object);

    /***********************************************************************************************************************************************************
     * {@return an empty {@link Configuration}}.
     **********************************************************************************************************************************************************/
    @Nonnull
    public default Configuration<T> config()
      {
        return new Configuration<>(Map.of(), Map.of(), List.of());
      }
  }
