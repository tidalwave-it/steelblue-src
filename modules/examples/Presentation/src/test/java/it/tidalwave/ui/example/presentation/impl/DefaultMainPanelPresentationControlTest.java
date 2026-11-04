/*
 * *************************************************************************************************************************************************************
 *
 * SteelBlue: DCI User Interfaces
 * http://tidalwave.it/projects/steelblue
 *
 * Copyright (C) 2015 - 2026 by Tidalwave s.a.s. (http://tidalwave.it)
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
import jakarta.annotation.Nonnull;
import java.util.List;
import java.nio.file.Path;
import it.tidalwave.ui.core.BoundProperty;
import it.tidalwave.ui.core.UserNotificationWithFeedback;
import it.tidalwave.ui.core.role.PresentationModel;
import it.tidalwave.ui.example.model.Dao;
import it.tidalwave.ui.example.model.FileEntity;
import it.tidalwave.ui.example.model.SimpleDciEntity;
import it.tidalwave.ui.example.model.SimpleEntity;
import it.tidalwave.ui.example.presentation.MainPanelPresentation;
import it.tidalwave.ui.test.AsAssert;
import it.tidalwave.util.As;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.mockito.ArgumentCaptor;
import static it.tidalwave.ui.core.role.Presentable._Presentable_;
import static it.tidalwave.ui.core.role.PresentationModel.*;
import static it.tidalwave.ui.core.role.Selectable._Selectable_;
import static it.tidalwave.ui.core.test.UserNotificationWithFeedbackTestHelper.*;
import static it.tidalwave.ui.test.AsAssert.assertThat;
import static org.mockito.Mockito.*;
/***************************************************************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **************************************************************************************************************************************************************/
public class DefaultMainPanelPresentationControlTest
  {
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
        underTest = new DefaultMainPanelPresentationControl(dao, presentation);
        underTest.initialize();
        // A capturer matcher intercepts the method call and stores the argument for later retrieval, by calling getCapture().
        final var captureBindings = ArgumentCaptor.forClass(MainPanelPresentation.Bindings.class);
        verify(presentation).bind(captureBindings.capture());
        bindings = captureBindings.getValue();
      }
    // END SNIPPET: mocks
    /**********************************************************************************************************************************************************/
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
        assertThat(bindings.textProperty).isEqualTo("2");
      }
    // END SNIPPET: test_buttonAction
    /**********************************************************************************************************************************************************/
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
    /**********************************************************************************************************************************************************/
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
    /**********************************************************************************************************************************************************/
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
    /**********************************************************************************************************************************************************/
    // START SNIPPET: test_actionPickFile_confirm
    @Test @SuppressWarnings("unchecked")
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
    /**********************************************************************************************************************************************************/
    @Test @SuppressWarnings("unchecked")
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
    /**********************************************************************************************************************************************************/
    @Test @SuppressWarnings("unchecked")
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
    /**********************************************************************************************************************************************************/
    @Test @SuppressWarnings("unchecked")
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
    /**********************************************************************************************************************************************************/
    // START SNIPPET: test_start
    @Test
    public void test_populate()
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
        final var cpm1 = ArgumentCaptor.forClass(PresentationModel.class);
        final var cpm2 = ArgumentCaptor.forClass(PresentationModel.class);
        final var cpm3 = ArgumentCaptor.forClass(PresentationModel.class);
        verify(presentation).populate(cpm1.capture(), cpm2.capture(), cpm3.capture());
        assertThat(cpm1).hasCompositeOf(_PresentationModel_)
                            .withItemCount(1)
                            .withEachItem(i ->
                                commonPmAssertions(i, "Item #simple entity", "SimpleEntity(simple entity)"));
        assertThat(cpm2).hasCompositeOf(_PresentationModel_)
                            .withItemCount(1)
                            .withEachItem(i ->
                                commonPmAssertions(i, simpleDciEntity.getName(), simpleDciEntity.toString())
                                .hasAggregateOf(_PresentationModel_)
                                    .withExactItemNames("C1", "C2", "C3")
                                    .withNextItemSatisfying(j -> j.hasDisplayName(simpleDciEntity.getName()))
                                    .withNextItemSatisfying(j -> j.hasDisplayName("" + simpleDciEntity.getAttribute1()))
                                    .withNextItemSatisfying(j -> j.hasDisplayName("" + simpleDciEntity.getAttribute2())));
        assertThat(cpm3).hasCompositeOf(_PresentationModel_)
                            .withItemCount(1)
                            .withEachItemSatisfying(i -> i
                                .hasDisplayName(fe.getDisplayName())
                                .hasAggregateOf(_PresentationModel_)
                                    .withExactItemNames("name", "size", "creationDate", "latestModificationDate")
                                    .withNextItemSatisfying(j -> j.hasDisplayName(fe.getDisplayName()))
                                    .withNextItemSatisfying(j -> j.hasDisplayName(() -> "" + fe.getSize())));
                                    // Commented out because timestamps keep changing, but you get the point.
                                    //.withNextItemSatisfying(j -> j.hasDisplayNameOf("12/18/24 11:25 am"))
                                    //.withNextItemSatisfying(j -> j.hasDisplayNameOf("12/18/24 11:28 am")));
        // FIXME verifyNoMoreInteractions(presentation);
      }
    /**********************************************************************************************************************************************************/
    @Nonnull
    private AsAssert<As> commonPmAssertions (@Nonnull final PresentationModel pm,
                                             @Nonnull final String expectedDisplayName,
                                             @Nonnull final String expectedToString)
      {
        assertThat(pm).hasDisplayName(expectedDisplayName);
        final var selectable = pm.as(_Selectable_);
        selectable.select();
        verify(presentation).notify("Selected " + expectedToString);
        assertThat(pm).hasUserActionProvider()
                          .withActionCount(3)
                          .withAll(actions ->
          {
            actions[0].actionPerformed();
            verify(presentation).notify("Action 1 on " + expectedToString);
            actions[1].actionPerformed();
            verify(presentation).notify("Action 2 on " + expectedToString);
            actions[2].actionPerformed();
            verify(presentation).notify("Action 3 on " + expectedToString);
          });
        return assertThat(pm); // trick to allow chaining assertions
      }
    // END SNIPPET: test_start
  }
