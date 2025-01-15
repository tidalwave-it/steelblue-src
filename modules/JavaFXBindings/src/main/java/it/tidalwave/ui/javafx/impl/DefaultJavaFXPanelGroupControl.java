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
package it.tidalwave.ui.javafx.impl;

import java.lang.reflect.InvocationTargetException;
import jakarta.annotation.Nonnull;
import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javafx.scene.Node;
import javafx.scene.control.Accordion;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import org.springframework.beans.factory.BeanFactory;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import it.tidalwave.ui.core.PanelGroupControl;
import it.tidalwave.ui.core.PanelGroupProvider;
import it.tidalwave.ui.core.message.PanelHiddenNotification;
import it.tidalwave.ui.core.message.PanelShownNotification;
import it.tidalwave.ui.core.spi.PanelGroupControlSupport;
import it.tidalwave.ui.core.message.PanelShowRequest;
import it.tidalwave.ui.javafx.JavaFXPanelGroupControl;
import it.tidalwave.ui.javafx.NodeAndDelegate;
import it.tidalwave.util.Pair;
import lombok.extern.slf4j.Slf4j;
import static it.tidalwave.ui.core.PanelGroupControl.Options.*;
import static it.tidalwave.ui.javafx.impl.DefaultJavaFXBinder.enforceFxApplicationThread;
import static it.tidalwave.ui.javafx.spi.AbstractJavaFXSpringApplication.APPLICATION_MESSAGE_BUS_BEAN_NAME;
import static java.util.Comparator.*;
import static java.util.stream.Collectors.*;
import static it.tidalwave.util.CollectionUtils.concatAll;

/***************************************************************************************************************************************************************
 *
 * The JavaFX implementation of {@link PanelGroupControl}.
 *
 * @since       2.0-ALPHA-3
 * @author      Fabrizio Giudici
 *
 **************************************************************************************************************************************************************/
@Slf4j @SuppressFBWarnings("MC_OVERRIDABLE_METHOD_CALL_IN_CONSTRUCTOR")
public class DefaultJavaFXPanelGroupControl extends PanelGroupControlSupport<AnchorPane, Node> implements JavaFXPanelGroupControl
  {
    private static final Runnable DO_NOTHING = () -> {};

    /** A map associating to each managed {@code Node} a callback that makes it visible. */
    private final Map<Node, Runnable> expanderByNode = new IdentityHashMap<>();

    /* A map associating to each managed {@Code Node} the group it belongs to. */
    private final Map<Node, Group> groupByNode = new IdentityHashMap<>();

    /* A map associating to each managed {@Code Node} its provider. */
    private final Map<Node, PanelGroupProvider<Node>> providerByNode = new IdentityHashMap<>();

    /***********************************************************************************************************************************************************
     *
     **********************************************************************************************************************************************************/
    public DefaultJavaFXPanelGroupControl (@Nonnull final BeanFactory beanFactory)
      {
        super(beanFactory, APPLICATION_MESSAGE_BUS_BEAN_NAME);
      }

    /***********************************************************************************************************************************************************
     * {@inheritDoc}
     **********************************************************************************************************************************************************/
    @Override
    public void show (@Nonnull final Object requestor)
      {
        expanderByNode.getOrDefault(findNode(requestor), DO_NOTHING).run();
      }

    /***********************************************************************************************************************************************************
     * {@inheritDoc}
     **********************************************************************************************************************************************************/
    @Override
    protected void assemble (@Nonnull final Group group,
                             @Nonnull final List<? extends PanelGroupProvider<Node>> panelProviders,
                             @Nonnull final AnchorPane topContainer,
                             @Nonnull final Map<Group, List<Options>> groupOptions,
                             @Nonnull final List<Options> globalOptions)
      {
        enforceFxApplicationThread();
        final var options = concatAll(globalOptions, groupOptions.getOrDefault(group, List.of()));
        panelProviders.forEach(p -> providerByNode.put(p.getComponent(), p));

        if (!options.contains(ALWAYS_WRAP) && panelProviders.size() == 1)
          {
            addDirectly(group, panelProviders, topContainer, options);
          }
        else if (options.contains(USE_ACCORDION))
          {
            addInAccordion(group, panelProviders, topContainer, options);
          }
        else
          {
            addInStackPane(group, panelProviders, topContainer, options);
          }
      }

    /***********************************************************************************************************************************************************
     *
     **********************************************************************************************************************************************************/
    @Override
    protected void onShowRequest (@Nonnull final PanelShowRequest panelShowRequest)
      {
        log.info("onShowRequest({})", panelShowRequest);
        expanderByNode.getOrDefault(findNode(panelShowRequest.getRequestor()), DO_NOTHING).run();
      }

    /***********************************************************************************************************************************************************
     *
     **********************************************************************************************************************************************************/
    private void addDirectly (@Nonnull final Group group,
                              @Nonnull final List<? extends PanelGroupProvider<Node>> panelProviders,
                              @Nonnull final AnchorPane topContainer,
                              @Nonnull final List<Options> options)
      {
        final var node = put(topContainer, panelProviders.get(0).getComponent());
        groupByNode.put(node, group);
        log.info("{}: options: {} --- no wrapper for {}", group, options, node);
      }

    /***********************************************************************************************************************************************************
     *
     **********************************************************************************************************************************************************/
    private void addInAccordion (@Nonnull final Group group,
                                 @Nonnull final List<? extends PanelGroupProvider<Node>> panelProviders,
                                 @Nonnull final AnchorPane topContainer,
                                 @Nonnull final List<Options> options)
      {
        final var accordion = put(topContainer, new Accordion());
        final var nodesAndTitlePanes = panelProviders.stream()
                                                     .sorted(comparing(PanelGroupProvider::getLabel))
                                                     .map(p -> Pair.of(p.getComponent(), createTitledPane(p, options)))
                                                     .collect(toList()); // pair of (Node, TitledPane)
        final var titlePanes = nodesAndTitlePanes.stream().map(Pair::getB).collect(toList());
        accordion.setVisible(!titlePanes.isEmpty());
        accordion.getPanes().setAll(titlePanes);
        nodesAndTitlePanes.forEach(p ->
          {
            groupByNode.put(p.a, group);
            expanderByNode.put(p.a, () -> accordion.setExpandedPane(p.b));
          });
        // Needs a listener because the change might be originated by the user
        accordion.expandedPaneProperty().addListener((observable, collapsed, expanded) ->
                fireUpdateMessages(Optional.ofNullable(collapsed).map(TitledPane::getContent),
                                   Optional.ofNullable(expanded).map(TitledPane::getContent)));
        log.info("{}: options: {} --- Accordion for {}", group, options, titlePanes);
      }

    /***********************************************************************************************************************************************************
     *
     **********************************************************************************************************************************************************/
    private void addInStackPane (@Nonnull final Group group,
                                 @Nonnull final List<? extends PanelGroupProvider<Node>> panelProviders,
                                 @Nonnull final AnchorPane topContainer,
                                 @Nonnull final List<Options> options)
      {
        final var stackPane = put(topContainer, new StackPane());
        final var nodes = panelProviders.stream()
                                        .sorted(comparing(PanelGroupProvider::getLabel))
                                        .map(PanelGroupProvider::getComponent)
                                        .collect(toList());
        nodes.forEach(n ->
          {
            n.setVisible(false);
            groupByNode.put(n, group);
            expanderByNode.put(n, () -> n.setVisible(true));
            n.visibleProperty().addListener((observable, oldVisible, visible) ->
                    fireUpdateMessages(visible ? Optional.empty() : Optional.of(n),
                                       visible ? Optional.of(n) : Optional.empty()));
          });

        stackPane.setVisible(!nodes.isEmpty());
        stackPane.getChildren().setAll(nodes);
        log.info("{}: options: {} --- StackPane for {}", group, options, nodes);
      }

    /***********************************************************************************************************************************************************
     * Fires update messages after a change.
     * @param     hiddenNode         the old node
     * @param     shownNode         the new node
     **********************************************************************************************************************************************************/
    private void fireUpdateMessages (@Nonnull final Optional<Node> hiddenNode, @Nonnull final Optional<Node> shownNode)
      {
        hiddenNode.ifPresent(n -> publish(new PanelHiddenNotification(providerByNode.get(n).getPresentation(), groupByNode.get(n))));
        shownNode.ifPresent(n -> publish(new PanelShownNotification(providerByNode.get(n).getPresentation(), groupByNode.get(n))));
      }

    /***********************************************************************************************************************************************************
     * {@return a {@link Node} extracted from a presentation}. If the presentation is not a {@code Node} itself, it is searched for a method returning
     * {@link NodeAndDelegate}.
     * @param     presentation      the presentation
     **********************************************************************************************************************************************************/
    @Nonnull
    private static Node findNode (@Nonnull final Object presentation)
      {
        if (presentation instanceof Node)
          {
            return (Node)presentation;
          }

        final var method = Arrays.stream(presentation.getClass().getDeclaredMethods())
                                 .filter(m -> m.getReturnType().equals(NodeAndDelegate.class))
                                 .findFirst()
                                 .orElseThrow(() -> new RuntimeException("Can't find method returning NodeAndDelegate in " + presentation));
        try
          {
            final var nad = (NodeAndDelegate<?>)method.invoke(presentation);
            return nad.getNode();
          }
        catch (IllegalAccessException | InvocationTargetException e)
          {
            final var message = "Couldn't extract a Node out of " + presentation;
            log.error(message, e);
            throw new RuntimeException(message, e);
          }
      }

    /***********************************************************************************************************************************************************
     * {@return a {@link TitledPane} wrapping the panel provided by the given provider.
     * @param   panelGroupProvider   the provider
     * @param   options         the options
     **********************************************************************************************************************************************************/
    @Nonnull
    private TitledPane createTitledPane (@Nonnull final PanelGroupProvider<? extends Node> panelGroupProvider, @Nonnull final List<Options> options)
      {
        final var titledPane = new TitledPane();
        titledPane.setText(panelGroupProvider.getLabel());
        titledPane.animatedProperty().set(!options.contains(DISABLE_ACCORDION_ANIMATION));
        titledPane.setContent(panelGroupProvider.getComponent());
        return titledPane;
      }

    /***********************************************************************************************************************************************************
     * Puts the given {@link Node} inside an {@link AnchorPane}.
     * @param   anchorPane    the {@code AnchorPane}
     * @param   node          the {@code Node}
     **********************************************************************************************************************************************************/
    @Nonnull
    private static <T extends Node> T put (@Nonnull final AnchorPane anchorPane, @Nonnull final T node)
      {
        AnchorPane.setLeftAnchor(node, 0.0); // TODO: maybe useless?
        AnchorPane.setRightAnchor(node, 0.0);
        AnchorPane.setTopAnchor(node, 0.0);
        AnchorPane.setBottomAnchor(node, 0.0);
        anchorPane.getChildren().setAll(node);
        return node;
      }
  }
