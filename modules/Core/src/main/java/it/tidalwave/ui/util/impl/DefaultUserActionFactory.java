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
package it.tidalwave.ui.util.impl;

import jakarta.annotation.Nonnull;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Optional;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import it.tidalwave.ui.core.message.PanelHiddenNotification;
import it.tidalwave.ui.core.message.PanelShowRequest;
import it.tidalwave.ui.core.message.PanelShownNotification;
import it.tidalwave.ui.core.role.Displayable;
import it.tidalwave.ui.core.role.UserAction;
import it.tidalwave.ui.util.UserActionFactory;
import it.tidalwave.util.annotation.VisibleForTesting;
import it.tidalwave.messagebus.MessageBus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/***************************************************************************************************************************************************************
 *
 * The default implementation of {@link UserActionFactory}.
 *
 * @author Fabrizio Giudici
 *
 **************************************************************************************************************************************************************/
@RequiredArgsConstructor @Slf4j
public class DefaultUserActionFactory implements UserActionFactory
  {
    @Nonnull
    private final Optional<MessageBus> messageBus;

    private final IdentityHashMap<Object, UserAction> actionsByRequestor = new IdentityHashMap<>();

    private final Collection<Object> currentlyShownPanels = new ArrayList<>();

    /** The listener to the message bus - stored to a field otherwise weak listeners will lose it. */
    private final MessageBus.Listener<PanelShownNotification> panelShownNotificationListener = this::onPanelShown;

    /** The listener to the message bus - stored to a field otherwise weak listeners will lose it. */
    private final MessageBus.Listener<PanelHiddenNotification> panelHiddenNotificationListener = this::onPanelHidden;

    /***********************************************************************************************************************************************************
     * {@inheritDoc}
     **********************************************************************************************************************************************************/
    @Override @Nonnull
    public UserActionParameters.UserActionParametersBuilder createViewActionFor (@Nonnull final Object presentation)
      {
        messageBus.ifPresentOrElse(ignored -> {}, () -> log.warn("NO MESSAGEBUS, action won't work"));
        return UserActionParameters.builder().callback(this::createViewAction).presentation(presentation);
      }

    /***********************************************************************************************************************************************************
     * Registers listeners if message bus is present. Since message bus dependency is optional, we can't rely on annotations, which require Aspect support.
     **********************************************************************************************************************************************************/
    @PostConstruct @SuppressFBWarnings("UPM_UNCALLED_PRIVATE_METHOD")
    private void subscribe()
      {
        messageBus.ifPresent(m -> m.subscribe(PanelShownNotification.class, panelShownNotificationListener));
        messageBus.ifPresent(m -> m.subscribe(PanelHiddenNotification.class, panelHiddenNotificationListener));
      }

    /***********************************************************************************************************************************************************
     * Unregisters listeners if message bus is present. Since message bus dependency is optional, we can't rely on annotations, which require Aspect support.
     **********************************************************************************************************************************************************/
    @PreDestroy @SuppressFBWarnings("UPM_UNCALLED_PRIVATE_METHOD")
    private void unsubscribe()
      {
        messageBus.ifPresent(m -> m.unsubscribe(panelShownNotificationListener));
        messageBus.ifPresent(m -> m.unsubscribe(panelHiddenNotificationListener));
      }

    /***********************************************************************************************************************************************************
     * {@return a new {@UserAction} created out of the given parameters}. This method is called back by {@code build()}.
     * @param     parameters      the parameters
     **********************************************************************************************************************************************************/
    @Nonnull
    private UserAction createViewAction (@Nonnull final UserActionParameters parameters)
      {
        final var presentation = parameters.getPresentation();
        final var label = parameters.getLabel();
        final var menuPlacement = parameters.getComputedMenuPlacement();
        final var userAction = UserAction.of(() -> requestShowPanel(presentation), List.of(Displayable.of(label), menuPlacement));
        actionsByRequestor.put(presentation, userAction);
        userAction.enabled().set(!currentlyShownPanels.contains(presentation));
        return userAction;
      }
    
    /***********************************************************************************************************************************************************
     * Receives notification that a panel has been shown.
     * @param     notification  the notification
     **********************************************************************************************************************************************************/
    @VisibleForTesting void onPanelShown (final PanelShownNotification notification)
      {
        log.debug("onPanelShown({})", notification);
        currentlyShownPanels.add(notification.getTarget());
        Optional.ofNullable(actionsByRequestor.get(notification.getTarget())).ifPresent(a -> a.enabled().set(false));
//        actionsByRequestor.forEach((requestor, action) -> action.enabled().set(requestor != notification.getTarget()));
      }

    /***********************************************************************************************************************************************************
     * Receives notification that a panel has been hidden.
     * @param     notification  the notification
     **********************************************************************************************************************************************************/
    @VisibleForTesting void onPanelHidden (final PanelHiddenNotification notification)
      {
        log.debug("onPanelHidden({})", notification);
        currentlyShownPanels.remove(notification.getTarget());
        Optional.ofNullable(actionsByRequestor.get(notification.getTarget())).ifPresent(a -> a.enabled().set(true));
      }

    /***********************************************************************************************************************************************************
     * Fires a request for showing up the given presentation.
     * @param     presentation  the presentation
     **********************************************************************************************************************************************************/
    private void requestShowPanel (@Nonnull final Object presentation)
      {
        messageBus.ifPresent(m -> m.publish(new PanelShowRequest(presentation)));
      }
  }
