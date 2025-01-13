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
package it.tidalwave.ui.core.spi;

import jakarta.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import it.tidalwave.ui.core.PanelGroupControl;
import it.tidalwave.ui.core.PanelGroupProvider;
import org.apiguardian.api.API;
import it.tidalwave.util.As;
import it.tidalwave.util.ShortNames;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import lombok.extern.slf4j.Slf4j;
import static org.apiguardian.api.API.Status.EXPERIMENTAL;
import static java.util.stream.Collectors.*;

/***************************************************************************************************************************************************************
 *
 * A support implementation of {@link PanelGroupControl}.
 *
 * @param   <T> the type of the top container
 * @param   <S>
 * @since       2.0-ALPHA-3
 * @author      Fabrizio Giudici
 *
 **************************************************************************************************************************************************************/
@API(status = EXPERIMENTAL)
@RequiredArgsConstructor @Slf4j @SuppressWarnings("this-escape")
public abstract class PanelGroupControlSupport<T, S> implements As, PanelGroupControl<T>
  {
    @Delegate @Nonnull
    private final As delegate = As.forObject(this);

    @Nonnull
    private final Function<As, Collection<? extends PanelGroupProvider<S>>> pgpProvider;

    /***********************************************************************************************************************************************************
     *
     **********************************************************************************************************************************************************/
    protected PanelGroupControlSupport()
      {
        this(PanelGroupControlSupport::defaultPanelGroupProviders);
      }

    /***********************************************************************************************************************************************************
     * {@inheritDoc}
     **********************************************************************************************************************************************************/
    @Override
    public void setup (@Nonnull final Configuration<T> configuration)
      {
        final var topContainersByGroup = configuration.getTopContainersByGroup();
        final var providersByGroup = new HashMap<Group, List<PanelGroupProvider<S>>>();
        pgpProvider.apply(this).forEach(p -> providersByGroup.computeIfAbsent(p.getGroup(), ignored -> new ArrayList<>()).add(p));
        log.debug("Providers by placement:");
        providersByGroup.forEach((placement, provider) -> log.debug(">>>> {}: {}", placement, ShortNames.shortIds(provider)));
        final var unboundPlacements = providersByGroup.keySet().stream().filter(p -> !topContainersByGroup.containsKey(p)).collect(toList());

        if (!unboundPlacements.isEmpty())
          {
            throw new RuntimeException("No top container(s) provider for " + unboundPlacements);
          }

        providersByGroup.forEach((group, providers) ->
            assemble(group, providers, topContainersByGroup.get(group), configuration.getGroupOptions(), configuration.getOptions()));
      }

    /***********************************************************************************************************************************************************
     * Assemble a set of panes for the given group.
     * @param   group               the {@code Group}
     * @param   panelProviders      the {@code PanelGroupProvider}s associated to the given {@code Group}
     * @param   topContainer        the top container
     * @param   groupOptions        options for each group
     * @param   options             options for doing the job
     **********************************************************************************************************************************************************/
    protected abstract void assemble (@Nonnull final Group group,
                                      @Nonnull final List<? extends PanelGroupProvider<S>> panelProviders,
                                      @Nonnull final T topContainer,
                                      @Nonnull final Map<Group, List<Options>> groupOptions,
                                      @Nonnull final List<Options> options);

    /***********************************************************************************************************************************************************
     *
     **********************************************************************************************************************************************************/
    @Nonnull
    private static <S> Collection<PanelGroupProvider<S>> defaultPanelGroupProviders (@Nonnull final As as)
      {
        return as.asMany(As.<PanelGroupProvider<S>>type(PanelGroupProvider.class));
      }
  }
