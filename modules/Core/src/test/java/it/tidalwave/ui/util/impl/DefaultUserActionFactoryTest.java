/*
 * *************************************************************************************************************************************************************
 *
 * blueMarine III: Semantic DAM
 * http://tidalwave.it/projects/bluemarine3
 *
 * Copyright (C) 2024 - 2026 by Tidalwave s.a.s. (http://tidalwave.it)
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
 * git clone https://bitbucket.org/tidalwave/bluemarine3-src
 * git clone https://github.com/tidalwave-it/bluemarine3-src
 *
 * *************************************************************************************************************************************************************
 */
package it.tidalwave.ui.util.impl;
import jakarta.annotation.Nonnull;
import java.util.Optional;
import it.tidalwave.ui.core.BoundProperty;
import it.tidalwave.ui.core.MenuBarControl.MenuIndex;
import it.tidalwave.ui.core.MenuBarControl.MenuPlacement;
import it.tidalwave.ui.core.message.PanelHiddenNotification;
import it.tidalwave.ui.core.message.PanelShowRequest;
import it.tidalwave.ui.core.message.PanelShownNotification;
import it.tidalwave.ui.core.role.UserAction;
import it.tidalwave.messagebus.MessageBus;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static it.tidalwave.ui.core.PanelGroupControl.DefaultGroups.LEFT;
import static it.tidalwave.ui.core.role.Displayable._Displayable_;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
/***************************************************************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **************************************************************************************************************************************************************/
public class DefaultUserActionFactoryTest
  {
    private final Object presentation = new Object();
    private final String label = "Test Panel";
    private final MenuIndex menuIndex = MenuIndex.FILE;
    private final String mainMenu = "File";
    private DefaultUserActionFactory underTest;
    private MessageBus messageBus;
    /**********************************************************************************************************************************************************/
    @BeforeMethod
    public void setUp()
      {
        messageBus = mock(MessageBus.class);
        underTest = new DefaultUserActionFactory(Optional.of(messageBus));
      }
    /**********************************************************************************************************************************************************/
    @Test
    public void must_properly_create_action_with_MenuIndex_and_label()
      {
        // when
        final var action = underTest.createViewActionFor(presentation)
                                    .label(label)
                                    .menuIndex(menuIndex)
                                    .build();
        // then
        assertThat(action).isNotNull();
        assertThat(action.as(MenuPlacement.class).getPath()).isEqualTo(menuIndex.getLabel());
        assertDisplayable(action, label);
        assertThat(action.enabled()).isEqualTo(BoundProperty.of(true));
      }
    /**********************************************************************************************************************************************************/
    @Test
    public void must_properly_create_action_with_MenuIndex_and_label_from_bundle()
      {
        // when
        final var action = underTest.createViewActionFor(presentation)
                                    .labelFromBundle(DefaultUserActionFactoryTest.class, "label")
                                    .menuIndex(menuIndex)
                                    .build();
        // then
        assertThat(action).isNotNull();
        assertThat(action.as(MenuPlacement.class).getPath()).isEqualTo(menuIndex.getLabel());
        assertDisplayable(action, label);
        assertThat(action.enabled()).isEqualTo(BoundProperty.of(true));
      }
    /**********************************************************************************************************************************************************/
    @Test
    public void must_properly_create_action_with_MainMenu_and_label()
      {
        // when
        final var action = underTest.createViewActionFor(presentation)
                                    .label(label)
                                    .menuPlacement(mainMenu)
                                    .build();
        // then
        assertThat(action).isNotNull();
        assertThat(action.as(MenuPlacement.class).getPath()).isEqualTo(menuIndex.getLabel());
        assertDisplayable(action, label);
        assertThat(action.enabled()).isEqualTo(BoundProperty.of(true));
      }
    // TODO: following variant tests can be rewritten as tests of UserActionParameters
    /**********************************************************************************************************************************************************/
    @Test
    public void must_properly_create_action_with_MainMenu_and_label_from_bundle()
      {
        // when
        final var action = underTest.createViewActionFor(presentation)
                                    .labelFromBundle(DefaultUserActionFactoryTest.class, "label")
                                    .menuPlacement(mainMenu)
                                    .build();
        // then
        assertThat(action).isNotNull();
        assertThat(action.as(MenuPlacement.class).getPath()).isEqualTo(menuIndex.getLabel());
        assertDisplayable(action, label);
        assertThat(action.enabled()).isEqualTo(BoundProperty.of(true));
      }
    /**********************************************************************************************************************************************************/
    @Test
    public void created_UserAction_must_fire_a_PanelShowRequest()
      {
        // given
        final var action = underTest.createViewActionFor(presentation)
                                    .label(label)
                                    .menuIndex(menuIndex)
                                    .build();
        // when
        action.actionPerformed();
        // then
        verify(messageBus).publish(new PanelShowRequest(presentation));
        verifyNoMoreInteractions(messageBus);
      }
    /**********************************************************************************************************************************************************/
    @Test
    public void must_disable_action_bound_to_shown_panel()
      {
        // given
        final var requestor1 = new Object();
        final var requestor2 = new Object();
        final var requestor3 = new Object();
        final var action1 = underTest.createViewActionFor(requestor1).menuPlacement(mainMenu).label(label).build();
        final var action2 = underTest.createViewActionFor(requestor2).menuPlacement(mainMenu).label(label).build();
        final var action3 = underTest.createViewActionFor(requestor3).menuPlacement(mainMenu).label(label).build();
        assertThat(action1.enabled()).isEqualTo(BoundProperty.of(true));
        assertThat(action2.enabled()).isEqualTo(BoundProperty.of(true));
        assertThat(action3.enabled()).isEqualTo(BoundProperty.of(true));
        // when
        underTest.onPanelShown(new PanelShownNotification(requestor1, LEFT));
        // then
        assertThat(action1.enabled()).isEqualTo(BoundProperty.of(false));
        assertThat(action2.enabled()).isEqualTo(BoundProperty.of(true));
        assertThat(action3.enabled()).isEqualTo(BoundProperty.of(true));
      }
    /**********************************************************************************************************************************************************/
    @Test
    public void must_enable_action_bound_to_hidden_panel()
      {
        // given
        final var requestor1 = new Object();
        final var requestor2 = new Object();
        final var requestor3 = new Object();
        final var action1 = underTest.createViewActionFor(requestor1).menuPlacement(mainMenu).label(label).build();
        final var action2 = underTest.createViewActionFor(requestor2).menuPlacement(mainMenu).label(label).build();
        final var action3 = underTest.createViewActionFor(requestor3).menuPlacement(mainMenu).label(label).build();
        underTest.onPanelShown(new PanelShownNotification(requestor1, LEFT));
        assertThat(action1.enabled()).isEqualTo(BoundProperty.of(false));
        assertThat(action2.enabled()).isEqualTo(BoundProperty.of(true));
        assertThat(action3.enabled()).isEqualTo(BoundProperty.of(true));
        // when
        underTest.onPanelHidden(new PanelHiddenNotification(requestor1, LEFT));
        // then
        assertThat(action1.enabled()).isEqualTo(BoundProperty.of(true));
        assertThat(action2.enabled()).isEqualTo(BoundProperty.of(true));
        assertThat(action3.enabled()).isEqualTo(BoundProperty.of(true));
      }
    /**********************************************************************************************************************************************************/
    private void assertDisplayable (@Nonnull final UserAction action, @Nonnull final String label)
      {
        assertThat(action.maybeAs(_Displayable_)).isPresent();
        assertThat(action.as(_Displayable_).getDisplayName()).isEqualTo(label);
      }
  }
