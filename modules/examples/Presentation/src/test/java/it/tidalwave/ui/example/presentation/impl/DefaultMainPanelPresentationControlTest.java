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
package it.tidalwave.ui.example.presentation.impl;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.nio.file.Path;
import it.tidalwave.util.ui.UserNotificationWithFeedback;
import it.tidalwave.role.Aggregate;
import it.tidalwave.role.SimpleComposite;
import it.tidalwave.role.ui.BoundProperty;
import it.tidalwave.role.ui.PresentationModel;
import it.tidalwave.ui.example.model.Dao;
import it.tidalwave.ui.example.model.FileEntity;
import it.tidalwave.ui.example.model.SimpleDciEntity;
import it.tidalwave.ui.example.model.SimpleEntity;
import it.tidalwave.ui.example.presentation.MainPanelPresentation;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.mockito.ArgumentMatcher;
import static it.tidalwave.util.FunctionalCheckedExceptionWrappers.*;
import static it.tidalwave.util.ui.UserNotificationWithFeedbackTestHelper.*;
import static it.tidalwave.role.Aggregate._Aggregate_;
import static it.tidalwave.role.SimpleComposite._SimpleComposite_;
import static it.tidalwave.role.ui.Displayable._Displayable_;
import static it.tidalwave.role.ui.Presentable._Presentable_;
import static it.tidalwave.role.ui.Selectable._Selectable_;
import static it.tidalwave.role.ui.UserActionProvider._UserActionProvider_;
import static org.mockito.Mockito.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/***************************************************************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **************************************************************************************************************************************************************/
public class DefaultMainPanelPresentationControlTest
  {
    @Getter @RequiredArgsConstructor
    static class CapturerMatcher<T> implements ArgumentMatcher<T>
      {
        private T captured;

        @Override
        public boolean matches (final T t)
          {
            captured = t;
            return true;
          }
      }

    // START SNIPPET: mocks
    private Dao dao;

    private MainPanelPresentation presentation;

    private DefaultMainPanelPresentationControl underTest;

    private MainPanelPresentation.Bindings bindings;

    @BeforeMethod
    public void setup()
      {
        dao = mock(Dao.class);
        presentation = mock(MainPanelPresentation.class);
        // A capturer matcher intercepts the method call and stores the argument for later retrieval, by calling getCapture().
        final var captureBindings = new CapturerMatcher<MainPanelPresentation.Bindings>();
        underTest = new DefaultMainPanelPresentationControl(dao, presentation);
        underTest.initialize();
        verify(presentation).bind(argThat(captureBindings));
        bindings = captureBindings.getCaptured();
      }
    // END SNIPPET: mocks

    /******************************************************************************************************************/
    // START SNIPPET: test_buttonAction
    @Test
    public void test_buttonAction()
      {
        // given
        underTest.status = 1;
        // when
        bindings.actionButton.actionPerformed();
        // then
        verifyNoInteractions(dao);
        verify(presentation).notify("Button pressed");
        // TBD: test bound property incremented
        verifyNoMoreInteractions(presentation);
        assertThat(bindings.textProperty.get(), is("2"));
      }
    // END SNIPPET: test_buttonAction

    /******************************************************************************************************************/
    // START SNIPPET: test_actionDialogOk_confirm
    @Test
    public void test_actionDialogOk_confirm()
      {
        // given
        doAnswer(confirm()).when(presentation).notify(any(UserNotificationWithFeedback.class));
        // when
        bindings.actionDialogOk.actionPerformed();
        // then
        verifyNoInteractions(dao);
        verify(presentation).notify(argThat(notificationWithFeedback("Notification", "Now press the button")));
        verify(presentation).notify("Pressed ok");
        verifyNoMoreInteractions(presentation);
      }
    // END SNIPPET: test_actionDialogOk_confirm

    /******************************************************************************************************************/
    @Test
    public void test_actionDialogCancelOk_confirm()
      {
        // given
        doAnswer(confirm()).when(presentation).notify(any(UserNotificationWithFeedback.class));
        // when
        bindings.actionDialogCancelOk.actionPerformed();
        // then
        verifyNoInteractions(dao);
        verify(presentation).notify(argThat(notificationWithFeedback("Notification", "Now press the button")));
        verify(presentation).notify("Pressed ok");
        verifyNoMoreInteractions(presentation);
      }

    /******************************************************************************************************************/
    // START SNIPPET: test_actionDialogCancelOk_cancel
    @Test
    public void test_actionDialogCancelOk_cancel()
      {
        // given
        doAnswer(cancel()).when(presentation).notify(any(UserNotificationWithFeedback.class));
        // when
        bindings.actionDialogCancelOk.actionPerformed();
        // then
        verifyNoInteractions(dao);
        verify(presentation).notify(argThat(notificationWithFeedback("Notification", "Now press the button")));
        verify(presentation).notify("Pressed cancel");
        verifyNoMoreInteractions(presentation);
      }
    // END SNIPPET: test_actionDialogCancelOk_cancel

    /******************************************************************************************************************/
    // START SNIPPET: test_actionPickFile_confirm
    @Test
    public void test_actionPickFile_confirm()
      {
        // given
        final var home = Path.of(System.getProperty("user.home"));
        //final var picked = Path.of("file.txt");
        // final Consumer<InvocationOnMock> responseSetter = i -> i.getArgument(0, BoundProperty.class).set(picked);
        // doAnswer(doAndConfirm(responseSetter)).when(presentation).pickFile(any(BoundProperty.class), any(UserNotificationWithFeedback.class));
        doAnswer(confirm()).when(presentation).pickFile(any(BoundProperty.class), any(UserNotificationWithFeedback.class));
        // when
        bindings.actionPickFile.actionPerformed();
        // then
        verifyNoInteractions(dao);
        verify(presentation).pickFile(eq(new BoundProperty<>(home)), argThat(notificationWithFeedback("Pick a file", "")));
        verify(presentation).notify("Selected file: " + home);
        verifyNoMoreInteractions(presentation);
      }
    // END SNIPPET: test_actionPickFile_confirm

    /******************************************************************************************************************/
    @Test
    public void test_actionPickFile_cancel()
      {
        // given
        final var home = Path.of(System.getProperty("user.home"));
        doAnswer(cancel()).when(presentation).pickFile(any(BoundProperty.class), any(UserNotificationWithFeedback.class));
        // when
        bindings.actionPickFile.actionPerformed();
        // then
        verifyNoInteractions(dao);
        verify(presentation).pickFile(eq(new BoundProperty<>(home)), argThat(notificationWithFeedback("Pick a file", "")));
        verify(presentation).notify("Selection cancelled");
        verifyNoMoreInteractions(presentation);
      }

    /******************************************************************************************************************/
    @Test
    public void test_actionPickDirectory_confirm()
      {
        // given
        final var home = Path.of(System.getProperty("user.home"));
        doAnswer(confirm()).when(presentation).pickDirectory(any(BoundProperty.class), any(UserNotificationWithFeedback.class));
        // when
        bindings.actionPickDirectory.actionPerformed();
        // then
        verifyNoInteractions(dao);
        verify(presentation).pickDirectory(eq(new BoundProperty<>(home)), argThat(notificationWithFeedback("Pick a directory", "")));
        //verify(presentation).notify("Selected directory: " + home);
        //verifyNoMoreInteractions(presentation);
      }

    /******************************************************************************************************************/
    @Test
    public void test_actionPickDirectory_cancel()
      {
        // given
        final var home = Path.of(System.getProperty("user.home"));
        doAnswer(cancel()).when(presentation).pickDirectory(any(BoundProperty.class), any(UserNotificationWithFeedback.class));
        // when
        bindings.actionPickDirectory.actionPerformed();
        // then
        verifyNoInteractions(dao);
        verify(presentation).pickDirectory(eq(new BoundProperty<>(home)), argThat(notificationWithFeedback("Pick a directory", "")));
        verify(presentation).notify("Selection cancelled");
        verifyNoMoreInteractions(presentation);
      }

    /******************************************************************************************************************/
    // START SNIPPET: test_start
    @Test
    public void test_populate ()
      {
        // given
        final var simpleEntity = new SimpleEntity("simple entity");
        final var simpleDciEntity = new SimpleDciEntity("id", 3, 4);
        final var fe = FileEntity.of(Path.of("src/test/resources/test-file.txt"));
        final var fes = spy(fe);
        doReturn(new FileEntityPresentable(fe)).when(fes).as(_Presentable_);

        // TODO: should create lists with more than 1 element...
        when(dao.getSimpleEntities()).thenReturn(List.of(simpleEntity));
        when(dao.getDciEntities()).thenReturn(List.of(simpleDciEntity));
        when(dao.getFiles()).thenReturn(List.of(fes));
        // when
        underTest.populate();
        // then
        verify(dao).getSimpleEntities();
        verify(dao).getDciEntities();
        verify(dao).getFiles();
        verifyNoMoreInteractions(dao);
        final var cpm1 = new CapturerMatcher<PresentationModel>();
        final var cpm2 = new CapturerMatcher<PresentationModel>();
        final var cpm3 = new CapturerMatcher<PresentationModel>();
        verify(presentation).populate(argThat(cpm1), argThat(cpm2), argThat(cpm3));

        final var pmList1 = toPmList(cpm1.getCaptured());
        assertThat(pmList1.size(), is(1));
        pmList1.forEach(pm -> assertPresentationModel(pm, "Item #simple entity", "SimpleEntity(simple entity)"));

        final var pmList2 = toPmList(cpm2.getCaptured());
        assertThat(pmList2.size(), is(1));
        pmList2.forEach(pm ->
          {
            assertPresentationModel(pm, simpleDciEntity.getName(), simpleDciEntity.toString());

            final Aggregate<PresentationModel> aggregate = pm.as(_Aggregate_);
            assertThat(aggregate.getNames(), is(Set.of("C1", "C2", "C3")));
            final var pmc1 = aggregate.getByName("C1").orElseThrow();
            final var pmc2 = aggregate.getByName("C2").orElseThrow();
            final var pmc3 = aggregate.getByName("C3").orElseThrow();
            assertThat(pmc1.as(_Displayable_).getDisplayName(), is(simpleDciEntity.getName()));
            assertThat(pmc2.as(_Displayable_).getDisplayName(), is("" + simpleDciEntity.getAttribute1()));
            assertThat(pmc3.as(_Displayable_).getDisplayName(), is("" + simpleDciEntity.getAttribute2()));
          });

        final var pmList3 = toPmList(cpm3.getCaptured());
        assertThat(pmList3.size(), is(1));
        pmList3.forEach(_c(pm ->
          {
            final var displayable = pm.as(_Displayable_);
            assertThat(displayable.getDisplayName(), is(fe.getDisplayName()));

            final Aggregate<PresentationModel> aggregate = pm.as(_Aggregate_);
            assertThat(aggregate.getNames(), is(Set.of("name", "size", "creationDate", "latestModificationDate")));
            final var pmc1 = aggregate.getByName("name").orElseThrow();
            final var pmc2 = aggregate.getByName("size").orElseThrow();
            final var pmc3 = aggregate.getByName("creationDate").orElseThrow();
            final var pmc4 = aggregate.getByName("latestModificationDate").orElseThrow();
            assertThat(pmc1.as(_Displayable_).getDisplayName(), is(fe.getDisplayName()));
            assertThat(pmc2.as(_Displayable_).getDisplayName(), is("" + fe.getSize()));
            // Commented out because they keep changing, but you get the point.
            // assertThat(pmc3.as(_Displayable_).getDisplayName(), is("12/18/24 11:25 am"));
            // assertThat(pmc4.as(_Displayable_).getDisplayName(), is("12/18/24 11:28 am"));
          }));

        // FIXME verifyNoMoreInteractions(presentation);
      }

    private void assertPresentationModel (@Nonnull final PresentationModel pm,
                                          @Nonnull final String expectedDisplayName,
                                          @Nonnull final String expectedToString)
      {
        final var displayable = pm.as(_Displayable_);
        final var selectable = pm.as(_Selectable_);
        final var userActionProvider = pm.as(_UserActionProvider_);

        assertThat(displayable.getDisplayName(), is(expectedDisplayName));

        selectable.select();
        verify(presentation).notify("Selected " + expectedToString);

        final var actions = new ArrayList<>(userActionProvider.getActions());
        assertThat(actions.size(), is(3));
        actions.get(0).actionPerformed();
        verify(presentation).notify("Action 1 on " + expectedToString);
        actions.get(1).actionPerformed();
        verify(presentation).notify("Action 2 on " + expectedToString);
        actions.get(2).actionPerformed();
        verify(presentation).notify("Action 3 on " + expectedToString);
      }

    @Nonnull
    private static List<PresentationModel> toPmList (@Nonnull final PresentationModel pm)
      {
        final SimpleComposite<PresentationModel> sc = pm.as(_SimpleComposite_);
        return sc.stream().collect(Collectors.toList());
      }
    // END SNIPPET: test_start
  }
