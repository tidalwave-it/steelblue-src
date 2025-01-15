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

import jakarta.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.springframework.beans.factory.BeanFactory;
import it.tidalwave.ui.core.PanelGroupControl;
import it.tidalwave.ui.core.PanelGroupProvider;
import it.tidalwave.ui.core.role.impl.AbstractPanelNotification;
import it.tidalwave.ui.core.message.PanelHiddenNotification;
import it.tidalwave.ui.core.message.PanelShownNotification;
import it.tidalwave.ui.core.spi.PanelGroupProviderSupport;
import it.tidalwave.ui.javafx.NodeAndDelegate;
import it.tidalwave.messagebus.MessageBus;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.mockito.ArgumentCaptor;
import static it.tidalwave.ui.core.PanelGroupControl.Options.*;
import static it.tidalwave.ui.core.PanelGroupControl.DefaultGroups.LEFT;
import static it.tidalwave.ui.javafx.spi.AbstractJavaFXSpringApplication.APPLICATION_MESSAGE_BUS_BEAN_NAME;
import static org.testfx.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/***************************************************************************************************************************************************************
 *
 * @author      Fabrizio Giudici
 *
 **************************************************************************************************************************************************************/
@Slf4j
public class DefaultJavaFXPanelGroupControlTest extends JavaFXTestSupport
  {
    @Getter
    static class MockPresentation
      {
        @SuppressWarnings("unchecked")
        private final NodeAndDelegate<Node> nad = mock(NodeAndDelegate.class);

        MockPresentation (@Nonnull final Node node)
          {
            when(nad.getNode()).thenReturn(node);
          }
      }

    private DefaultJavaFXPanelGroupControl underTest;

    private AnchorPane topContainer;

    private PanelGroupProvider<Node> provider1;

    private PanelGroupProvider<Node> provider2;

    private MockPresentation presentation1;

    private MockPresentation presentation2;

    private BeanFactory beanFactory;

    /***********************************************************************************************************************************************************
     * Prepares the {@link Stage for the test}.
     **********************************************************************************************************************************************************/
    @Override
    public void start (@Nonnull final Stage stage)
      {
        topContainer = new AnchorPane();
        stage.setScene(new Scene(new StackPane(topContainer), 800, 600));
        stage.show();
      }

    /***********************************************************************************************************************************************************
     *
     **********************************************************************************************************************************************************/
    @BeforeMethod(groups = "display")
    public void setup()
      {
        beanFactory = mock(BeanFactory.class);
        underTest = new DefaultJavaFXPanelGroupControl(beanFactory);
        presentation1 = new MockPresentation(new StackPane(new Label("1")));
        presentation2 = new MockPresentation(new StackPane(new Label("2")));
        provider1 = createMockPanelGroupProvider("Panel 1", presentation1);
        provider2 = createMockPanelGroupProvider("Panel 2", presentation2);
      }

    /**********************************************************************************************************************************************************/
    @Test(groups = "display")
    public void a_single_pane_should_be_added_directly()
      {
        // when
        runSafelyAndWait("assemble", () -> underTest.assemble(LEFT, List.of(provider1), topContainer, Map.of(), List.of()));
        // when
        assertThat(new ArrayList<>(topContainer.getChildren())).containsExactly(provider1.getComponent());
        assertThat(provider1.getComponent()).isVisible();
      }

    /**********************************************************************************************************************************************************/
    @Test(groups = "display")
    public void a_single_pane_with_ALWAYS_WRAP_should_be_wrapped_in_StackPane()
      {
        // when
        runSafelyAndWait("assemble", () -> underTest.assemble(LEFT, List.of(provider1), topContainer, Map.of(), List.of(ALWAYS_WRAP)));
        // then
        assertThat(topContainer).hasExactlyNumChildren(1);
        final var stackPane = topContainer.getChildren().get(0);
        assertThat(stackPane).isInstanceOf(StackPane.class);
        assertThat((StackPane)stackPane).hasExactlyNumChildren(1);
        final var children = ((StackPane)stackPane).getChildrenUnmodifiable();
        assertThat(children.get(0)).isSameAs(provider1.getComponent());
        assertThat(stackPane).isVisible();
        assertThat(provider1.getComponent()).isInvisible();
      }

    /**********************************************************************************************************************************************************/
    @Test(groups = "display")
    public void a_single_pane_with_ALWAYS_WRAP_and_USE_ACCORDION_must_be_wrapped_in_TitlePane_and_placed_in_Accordion()
      {
        // when
        runSafelyAndWait("assemble", () -> underTest.assemble(LEFT, List.of(provider1), topContainer, Map.of(), List.of(ALWAYS_WRAP, USE_ACCORDION)));
        // then
        assertThat(topContainer).hasExactlyNumChildren(1);
        final var accordion = topContainer.getChildren().get(0);
        assertThat(accordion).isInstanceOf(Accordion.class);
        final var panes = ((Accordion)accordion).getPanes();
        assertThat(panes).hasSize(1);
        final var tp1 = panes.get(0);
        assertThat(tp1.getText()).isEqualTo("Panel 1");
        assertThat(tp1.getContent()).isSameAs(provider1.getComponent());
        assertThat(tp1).isVisible();
        // assertThat(tp1.getContent()).isVisible(); // TOOD: not sure
        assertThat(accordion).isVisible();
        assertThat(((Accordion)accordion).getExpandedPane()).isNull();
      }

    /**********************************************************************************************************************************************************/
    @Test(groups = "display")
    public void two_panes_with_same_selector_must_be_wrapped_in_StackPane()
      {
        // when
        runSafelyAndWait("assemble", () -> underTest.assemble(LEFT, List.of(provider1, provider2), topContainer, Map.of(), List.of()));
        // then
        assertThat(topContainer).hasExactlyNumChildren(1);
        final var stackPane = topContainer.getChildren().get(0);
        assertThat(stackPane).isInstanceOf(StackPane.class);
        assertThat((StackPane)stackPane).hasExactlyNumChildren(2);
        final var children = ((StackPane)stackPane).getChildrenUnmodifiable();
        assertThat(children.get(0)).isSameAs(provider1.getComponent());
        assertThat(children.get(1)).isSameAs(provider2.getComponent());
        assertThat(stackPane).isVisible();
        assertThat(provider1.getComponent()).isInvisible();
        assertThat(provider2.getComponent()).isInvisible();
      }

    /**********************************************************************************************************************************************************/
    @Test(groups = "display")
    public void two_panes_with_USE_ACCORDION_must_be_wrapped_in_TitlePane_and_placed_in_Accordion()
      {
        // when
        runSafelyAndWait("assemble", () -> underTest.assemble(LEFT, List.of(provider1, provider2), topContainer, Map.of(), List.of(USE_ACCORDION)));
        // then
        assertThat(topContainer).hasExactlyNumChildren(1);
        final var accordion = topContainer.getChildren().get(0);
        assertThat(accordion).isInstanceOf(Accordion.class);
        final var panes = ((Accordion)accordion).getPanes();
        assertThat(panes).hasSize(2);
        final var tp1 = panes.get(0);
        final var tp2 = panes.get(1);
        assertThat(tp1.getText()).isEqualTo("Panel 1");
        assertThat(tp2.getText()).isEqualTo("Panel 2");
        assertThat(tp1.getContent()).isSameAs(provider1.getComponent());
        assertThat(tp2.getContent()).isSameAs(provider2.getComponent());
        assertThat(accordion).isVisible();
        assertThat(((Accordion)accordion).getExpandedPane()).isNull();
      }

    /**********************************************************************************************************************************************************/
    @Test(groups = "display", dependsOnMethods = "two_panes_with_same_selector_must_be_wrapped_in_StackPane")
    public void must_properly_show_when_StackPane_is_used()
      {
        // given
        runSafelyAndWait("assemble", () -> underTest.assemble(LEFT, List.of(provider1, provider2), topContainer, Map.of(), List.of()));
        // when
        runSafelyAndWait("show", () -> underTest.show(presentation1));
        // then
        assertThat(provider1.getComponent()).isVisible();
        assertThat(provider2.getComponent()).isInvisible();
      }

    /**********************************************************************************************************************************************************/
    @Test(groups = "display", dependsOnMethods = "two_panes_with_USE_ACCORDION_must_be_wrapped_in_TitlePane_and_placed_in_Accordion")
    public void must_properly_show_when_Accordion_is_used()
      {
        // given
        runSafelyAndWait("assemble", () -> underTest.assemble(LEFT, List.of(provider1, provider2), topContainer, Map.of(), List.of(USE_ACCORDION)));
        // when
        runSafelyAndWait("show", () -> underTest.show(presentation2));
        // then
        final var accordion = (Accordion)topContainer.getChildren().get(0);
        assertThat(accordion.getExpandedPane()).isNotNull();
        assertThat(accordion.getExpandedPane().getContent()).isSameAs(provider2.getComponent());
      }

    /**********************************************************************************************************************************************************/
    @Test(groups = "display", dataProvider = "showOptions")
    public void show_mush_publish_proper_messages_at_first (@Nonnull final List<PanelGroupControl.Options> options)
      {
        // given
        final var messageBus = createUnderTestWithMessageBus();
        runSafelyAndWait("assemble", () -> underTest.assemble(LEFT, List.of(provider1, provider2), topContainer, Map.of(), List.of(USE_ACCORDION)));
        // when
        runSafelyAndWait("show", () -> underTest.show(presentation1));
        // then
        final var messages = getMessageSequence(messageBus, 1);
        assertThat(messages).allMatch(m -> m.getGroup().equals(LEFT));
        assertThat(messages[0]).isInstanceOf(PanelShownNotification.class).matches(m -> m.getTarget() == presentation1);
      }

    /**********************************************************************************************************************************************************/
    @Test(groups = "display", dataProvider = "showOptions")
    public void show_mush_publish_proper_messages (@Nonnull final List<PanelGroupControl.Options> options)
      {
        // given
        final var messageBus = createUnderTestWithMessageBus();
        runSafelyAndWait("assemble", () -> underTest.assemble(LEFT, List.of(provider1, provider2), topContainer, Map.of(), options));
        runSafelyAndWait("show1", () -> underTest.show(presentation1));
        reset(messageBus);
        // when
        runSafelyAndWait("show2", () -> underTest.show(presentation2));
        // then
        final var messages = getMessageSequence(messageBus, 2);
        assertThat(messages).allMatch(m -> m.getGroup().equals(LEFT));
        assertThat(messages[0]).isInstanceOf(PanelHiddenNotification.class).matches(m -> m.getTarget() == presentation1);
        assertThat(messages[1]).isInstanceOf(PanelShownNotification.class).matches(m -> m.getTarget() == presentation2);
      }

    /**********************************************************************************************************************************************************/
    @Test(groups = "display")
    public void show_mush_publish_proper_messages_when_expanded_from_UI()
      {
        // given
        final var messageBus = createUnderTestWithMessageBus();
        runSafelyAndWait("assemble", () -> underTest.assemble(LEFT, List.of(provider1, provider2), topContainer, Map.of(), List.of(USE_ACCORDION)));
        final var accordion = (Accordion)topContainer.getChildren().get(0);
        final var titledPane2 = accordion.getPanes().get(1);
        runSafelyAndWait("show1", () -> underTest.show(presentation1));
        reset(messageBus);
        // when
        runSafelyAndWait("show2", () -> accordion.setExpandedPane(titledPane2));
        // then
        final var messages = getMessageSequence(messageBus, 2);
        assertThat(messages).allMatch(m -> m.getGroup().equals(LEFT));
        assertThat(messages[0]).isInstanceOf(PanelHiddenNotification.class).matches(m -> m.getTarget() == presentation1);
        assertThat(messages[1]).isInstanceOf(PanelShownNotification.class).matches(m -> m.getTarget() == presentation2);
      }

    /**********************************************************************************************************************************************************/
    @Nonnull
    private static AbstractPanelNotification[] getMessageSequence (@Nonnull final MessageBus messageBus, final int wantedNumberOfInvocations)
      {
        final var messageCaptor = ArgumentCaptor.forClass(AbstractPanelNotification.class);
        verify(messageBus, times(wantedNumberOfInvocations)).publish(messageCaptor.capture());
        return messageCaptor.getAllValues().toArray(AbstractPanelNotification[]::new);
      }

    /**********************************************************************************************************************************************************/
    @Nonnull
    private MessageBus createUnderTestWithMessageBus()
      {
        final var messageBus = mock(MessageBus.class);
        beanFactory = mock(BeanFactory.class);
        when(beanFactory.containsBean(APPLICATION_MESSAGE_BUS_BEAN_NAME)).thenReturn(true);
        when(beanFactory.getBean(APPLICATION_MESSAGE_BUS_BEAN_NAME, MessageBus.class)).thenReturn(messageBus);
        underTest = new DefaultJavaFXPanelGroupControl(beanFactory);
        return messageBus;
      }

    /**********************************************************************************************************************************************************/
    @Nonnull
    private static PanelGroupProvider<Node> createMockPanelGroupProvider (@Nonnull final String name, @Nonnull final MockPresentation presentation)
      {
        return new PanelGroupProviderSupport<>(LEFT, name, presentation, presentation.nad::getNode) {};
      }

    /**********************************************************************************************************************************************************/
    @DataProvider
    private static Object[][] showOptions()
      {
        return new Object[][]
          {
            { List.of() },
            { List.of(USE_ACCORDION) },
          };
      }
  }