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
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import org.springframework.beans.factory.BeanFactory;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import it.tidalwave.ui.core.PanelGroupControl;
import it.tidalwave.ui.core.PanelGroupProvider;
import it.tidalwave.ui.core.message.PanelShowRequest;
import org.apiguardian.api.API;
import it.tidalwave.util.As;
import it.tidalwave.util.annotation.VisibleForTesting;
import it.tidalwave.messagebus.MessageBus;
import lombok.experimental.Delegate;
import lombok.extern.slf4j.Slf4j;
import static org.apiguardian.api.API.Status.EXPERIMENTAL;
import static java.util.stream.Collectors.*;
import static it.tidalwave.util.ShortNames.shortIds;

/***************************************************************************************************************************************************************
 *
 * A support implementation of {@link PanelGroupControl}.
 *
 * @param   <T> the type of the control
 * @param   <S> the type of the top container
 * @since       2.0-ALPHA-3
 * @author      Fabrizio Giudici
 *
 **************************************************************************************************************************************************************/
@API(status = EXPERIMENTAL)
@Slf4j @SuppressWarnings("this-escape")
public abstract class PanelGroupControlSupport<T, S> implements As, PanelGroupControl<T>
  {
    @Delegate @Nonnull
    private final As delegate = As.forObject(this);

    @Nonnull
    private final Function<As, Collection<PanelGroupProvider<S>>> pgProvider;

    /** The message bus, if present on the system. */
    @Nonnull
    private final Optional<MessageBus> messageBus;

    /** The listener to the message bus - stored to a field otherwise weak listeners will lose it. */
    private final MessageBus.Listener<PanelShowRequest> messageListener = this::onShowRequest;

    /***********************************************************************************************************************************************************
     * This class doesn't rely on the @SimpleSubscriber/@ListensTo annotations to avoid having a dependency on the MessageBus runtime, so it dynamically
     * queries a {@link BeanFactory}.
     **********************************************************************************************************************************************************/
    @SuppressFBWarnings("CT_CONSTRUCTOR_THROW")
    protected PanelGroupControlSupport (@Nonnull final BeanFactory beanFactory, @Nonnull final String messageBusBeanName)
      {
        this(PanelGroupControlSupport::defaultPanelGroupProviders, beanFactory, messageBusBeanName);
      }

    /***********************************************************************************************************************************************************
     * Constructor for tets.
     **********************************************************************************************************************************************************/
    @SuppressFBWarnings("CT_CONSTRUCTOR_THROW")
    protected PanelGroupControlSupport (@Nonnull final Function<As, Collection<PanelGroupProvider<S>>> pgProvider,
                                        @Nonnull final BeanFactory beanFactory,
                                        @Nonnull final String messageBusBeanName)
      {
        this.pgProvider = pgProvider;
        messageBus = getMessageBus(beanFactory, messageBusBeanName);
      }

    /***********************************************************************************************************************************************************
     * {@inheritDoc}
     **********************************************************************************************************************************************************/
    @Override
    public void setup (@Nonnull final Configuration<T> configuration)
      {
        final var topContainersByGroup = configuration.getTopContainersByGroup();
        final var providersByGroup = new HashMap<Group, List<PanelGroupProvider<S>>>();
        pgProvider.apply(this).forEach(p -> providersByGroup.computeIfAbsent(p.getGroup(), ignored -> new ArrayList<>()).add(p));
        log.debug("Providers by placement:");
        providersByGroup.forEach((placement, provider) -> log.debug(">>>> {}: {}", placement, shortIds(provider)));
        final var unboundGroups = providersByGroup.keySet().stream().filter(p -> !topContainersByGroup.containsKey(p)).collect(toList());

        if (!unboundGroups.isEmpty())
          {
            throw new IllegalArgumentException("No top container(s) provider for " + unboundGroups);
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
    protected abstract void onShowRequest (@Nonnull PanelShowRequest panelShowRequest);

    /***********************************************************************************************************************************************************
     *
     **********************************************************************************************************************************************************/
    protected void publish (@Nonnull final Object message)
      {
        messageBus.ifPresent(mb -> mb.publish(message));
      }

    /***********************************************************************************************************************************************************
     *
     **********************************************************************************************************************************************************/
    @PostConstruct
    @VisibleForTesting void initialize()
      {
        messageBus.ifPresent(mb -> mb.subscribe(PanelShowRequest.class, messageListener));
      }

    /***********************************************************************************************************************************************************
     *
     **********************************************************************************************************************************************************/
    @PreDestroy
    @VisibleForTesting void destroy()
      {
        messageBus.ifPresent(mb -> mb.unsubscribe(messageListener));
      }

    /***********************************************************************************************************************************************************
     *
     **********************************************************************************************************************************************************/
    @Nonnull
    private static Optional<MessageBus> getMessageBus (@Nonnull final BeanFactory beanFactory, @Nonnull final String messageBusBeanName)
      {
        if (beanFactory.containsBean(messageBusBeanName))
          {
            return Optional.of(beanFactory.getBean(messageBusBeanName, MessageBus.class));
          }
        else
          {
            log.warn("No message bus");
            return Optional.empty();
          }
      }

    /***********************************************************************************************************************************************************
     *
     **********************************************************************************************************************************************************/
    @Nonnull
    private static <S> Collection<PanelGroupProvider<S>> defaultPanelGroupProviders (@Nonnull final As as)
      {
        return as.asMany(As.type(PanelGroupProvider.class));
      }
  }
