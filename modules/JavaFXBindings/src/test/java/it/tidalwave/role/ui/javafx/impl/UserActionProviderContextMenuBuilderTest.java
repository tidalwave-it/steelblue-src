/*
 * #%L
 * *********************************************************************************************************************
 *
 * SteelBlue
 * http://steelblue.tidalwave.it - git clone git@bitbucket.org:tidalwave/steelblue-src.git
 * %%
 * Copyright (C) 2015 - 2015 Tidalwave s.a.s. (http://tidalwave.it)
 * %%
 *
 * *********************************************************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * *********************************************************************************************************************
 *
 *
 *
 * *********************************************************************************************************************
 * #L%
 */
package it.tidalwave.role.ui.javafx.impl;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import it.tidalwave.role.ui.Displayable;
import it.tidalwave.role.ui.javafx.impl.common.DefaultCellBinder;
import it.tidalwave.role.ui.javafx.impl.common.RoleBag;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.MenuItem;
import it.tidalwave.role.ContextManager;
import it.tidalwave.role.spi.DefaultContextManagerProvider;
import it.tidalwave.role.ui.UserAction;
import it.tidalwave.role.ui.UserActionProvider;
import it.tidalwave.role.ui.spi.DefaultUserActionProvider;
import it.tidalwave.util.spi.AsDelegateProvider;
import lombok.experimental.Delegate;
import lombok.RequiredArgsConstructor;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.CoreMatchers.*;
import static org.mockito.Mockito.*;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
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

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @BeforeMethod
    public void setup()
      {
        AsDelegateProvider.Locator.set(AsDelegateProvider.empty());
        ContextManager.Locator.set(new DefaultContextManagerProvider()); // TODO: possibly drop this
        actions = new ArrayList<>();

        for (int i = 0; i < 10; i++)
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

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Test
    public void must_return_empty_list_when_UserActionProvider_is_not_present()
      {
        final List<MenuItem> menuItems = underTest.createMenuItems(roleMapWithoutUserActionProvider);

        assertThat(menuItems, is(notNullValue()));
        assertThat(menuItems.isEmpty(), is(true));
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Test
    public void must_set_the_MenuItem_text_from_UserAction_Displayable()
      {
        final List<MenuItem> menuItems = underTest.createMenuItems(roleMapWithUserActionProvider);

        assertThat(menuItems, is(not(nullValue())));
        assertThat(menuItems.size(), is(actions.size()));

        for (int i = 0; i < menuItems.size(); i++)
          {
            final MenuItem menuItem = menuItems.get(i);
            assertThat(menuItem, is(not(nullValue())));
            assertThat(menuItem.getText(), is("Action #" + i));
          }
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Test
    public void must_invoke_callbacks_in_a_non_FX_thread()
      throws InterruptedException
      {
        final List<MenuItem> menuItems = underTest.createMenuItems(roleMapWithUserActionProvider);

        assertThat(menuItems, is(not(nullValue())));
        assertThat(menuItems.size(), is(actions.size()));

        for (final MenuItem menuItem : menuItems)
          {
            menuItem.getOnAction().handle(new ActionEvent());
          }

        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.SECONDS);

        for (int i = 0; i < menuItems.size(); i++)
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