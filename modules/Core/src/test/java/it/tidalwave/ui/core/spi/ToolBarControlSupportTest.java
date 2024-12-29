/*
 * *************************************************************************************************************************************************************
 *
 * SteelBlue: DCI User Interfaces
 * http://tidalwave.it/projects/steelblue
 *
 * Copyright (C) 2015 - 2024 by Tidalwave s.a.s. (http://tidalwave.it)
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

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;
import it.tidalwave.ui.core.role.UserAction;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.testng.annotations.Test;
import static it.tidalwave.ui.core.role.Displayable._Displayable_;
import static org.mockito.Mockito.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class BinderMock2
  {
  }

@Getter @RequiredArgsConstructor
class ToolbarMock
  {
    @Nonnull
    private final List<ButtonMock> items = new ArrayList<>();
  }

@Getter @RequiredArgsConstructor
class ButtonMock
  {
    @Nonnull
    private final String text;
  }

class UnderTest extends ToolBarControlSupport<BinderMock2, ToolbarMock, ButtonMock>
  {
    public UnderTest (@Nonnull final Supplier<Collection<? extends UserAction>> userActionsSupplier)
      {
        super(userActionsSupplier);
      }

    @Override @Nonnull
    protected ButtonMock createButton (@Nonnull final BinderMock2 binder, @Nonnull final UserAction action)
      {
        return new ButtonMock(action.as(_Displayable_).getDisplayName());
      }

    @Override
    protected void addButtonsToToolBar (@Nonnull final ToolbarMock toolBar, @Nonnull final List<ButtonMock> buttons)
      {
        toolBar.getItems().addAll(buttons);
      }
  }

/***************************************************************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **************************************************************************************************************************************************************/
public class ToolBarControlSupportTest
  {
    private final TestUserActions a = new TestUserActions();

    @Test
    public void test_populate()
      {
        // given
        final var underTest = new UnderTest(() -> List.of(a.actionFileOpen, a.actionFileClose, a.actionFileCloseAll));
        final var binder = mock(BinderMock2.class);
        final var toolBar = new ToolbarMock();
        // when
        underTest.populate(binder, toolBar);
        // then
        final var buttons = toolBar.getItems();
        assertThat(buttons.size(), is(3));
        final var button1 = buttons.get(0);
        final var button2 = buttons.get(1);
        final var button3 = buttons.get(2);

        assertThat(button1.getText(), is("Open"));
        assertThat(button2.getText(), is("Close"));
        assertThat(button3.getText(), is("Close all"));
      }
  }
