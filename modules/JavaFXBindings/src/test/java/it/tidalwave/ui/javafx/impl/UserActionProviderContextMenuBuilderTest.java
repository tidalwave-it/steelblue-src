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
package it.tidalwave.ui.javafx.impl;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import javafx.event.ActionEvent;
import javafx.application.Platform;
import it.tidalwave.util.ContextManager;
import it.tidalwave.role.impl.DefaultContextManagerProvider;
import it.tidalwave.role.spi.SystemRoleFactory;
import it.tidalwave.ui.core.role.Displayable;
import it.tidalwave.ui.core.role.UserAction;
import it.tidalwave.ui.core.role.UserActionProvider;
import it.tidalwave.ui.javafx.impl.common.DefaultCellBinder;
import it.tidalwave.ui.javafx.impl.common.RoleBag;
import it.tidalwave.ui.core.role.spi.DefaultUserActionProvider;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.mockito.Mockito.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

/***************************************************************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **************************************************************************************************************************************************************/
public class UserActionProviderContextMenuBuilderTest
  {
    @RequiredArgsConstructor
    static class TestExecutorService implements ExecutorService
      {
        public final List<AssertionError> assertionErrors = new ArrayList<>();

        static interface Exclusions
          {
            public Future<?> submit (Runnable runnable);
          }

        @Delegate(excludes = Exclusions.class)
        private final ExecutorService delegate;

        @Override @Nonnull
        public Future<?> submit (final Runnable task)
          {
            return delegate.submit(() ->
               {
                 try
                   {
                     task.run();
                   }
                 catch (AssertionError e)
                   {
                     assertionErrors.add(e);
                   }
               });
          }
      }

    private DefaultCellBinder underTest;

    private List<UserAction> actions;

    private RoleBag roleMapWithoutUserActionProvider;

    private RoleBag roleMapWithUserActionProvider;

    private TestExecutorService executorService;

    private void checkThread()
      {
        assertThat("Must not be in the FX thread!", Platform.isFxApplicationThread(), is(false));
      }

    /***********************************************************************************************************************************************************
     *
     **********************************************************************************************************************************************************/
    @BeforeMethod
    public void setup()
      {
        SystemRoleFactory.reset();
        ContextManager.set(new DefaultContextManagerProvider()); // TODO: possibly drop this
        actions = new ArrayList<>();

        for (var i = 0; i < 10; i++)
          {
            actions.add(spy(UserAction.of(this::checkThread, Displayable.of("Action #" + i))));
          }

        final UserActionProvider userActionProvider = new DefaultUserActionProvider()
          {
            @Override @Nonnull
            public Collection<? extends UserAction> getActions()
              {
                return actions;
              }
          };

        roleMapWithoutUserActionProvider = new RoleBag();
        roleMapWithUserActionProvider = new RoleBag();
        roleMapWithUserActionProvider.put(UserActionProvider.class, userActionProvider);

        executorService = new TestExecutorService(Executors.newSingleThreadExecutor());

        underTest = new DefaultCellBinder(executorService);
      }

    /***********************************************************************************************************************************************************
     *
     **********************************************************************************************************************************************************/
    @Test
    public void must_return_empty_list_when_UserActionProvider_is_not_present()
      {
        // when
        final var menuItems = underTest.createMenuItems(roleMapWithoutUserActionProvider);
        // then
        assertThat(menuItems, is(notNullValue()));
        assertThat(menuItems.isEmpty(), is(true));
      }

    /***********************************************************************************************************************************************************
     *
     **********************************************************************************************************************************************************/
    @Test
    public void must_set_the_MenuItem_text_from_UserAction_Displayable()
      {
        // when
        final var menuItems = underTest.createMenuItems(roleMapWithUserActionProvider);
        // then
        assertThat(menuItems, is(not(nullValue())));
        assertThat(menuItems.size(), is(actions.size()));

        for (var i = 0; i < menuItems.size(); i++)
          {
            final var menuItem = menuItems.get(i);
            assertThat(menuItem, is(not(nullValue())));
            assertThat(menuItem.getText(), is("Action #" + i));
          }
      }

    /***********************************************************************************************************************************************************
     *
     **********************************************************************************************************************************************************/
    @Test
    public void must_invoke_callbacks_in_a_non_FX_thread()
      throws InterruptedException
      {
        // when
        final var menuItems = underTest.createMenuItems(roleMapWithUserActionProvider);
        // then
        assertThat(menuItems, is(not(nullValue())));
        assertThat(menuItems.size(), is(actions.size()));

        for (final var menuItem : menuItems)
          {
            menuItem.getOnAction().handle(new ActionEvent());
          }

        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.SECONDS);

        for (var i = 0; i < menuItems.size(); i++)
          {
            verify(actions.get(i), times(1)).actionPerformed();
//            verifyNoMoreInteractions(actions.get(i));
          }

        if (!executorService.assertionErrors.isEmpty())
          {
            throw executorService.assertionErrors.get(0);
          }
      }
  }