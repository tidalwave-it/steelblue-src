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
package it.tidalwave.ui.core;

import it.tidalwave.util.Callback;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static it.tidalwave.ui.core.UserNotificationWithFeedback.*;
import static org.mockito.Mockito.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/***************************************************************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **************************************************************************************************************************************************************/
public class UserNotificationWithFeedbackTest
  {
    private Callback onConfirm;

    private Callback onCancel;

    /**********************************************************************************************************************************************************/
    @BeforeMethod
    public void setup()
      {
        onConfirm = mock(Callback.class);
        onCancel = mock(Callback.class);
      }

    /**********************************************************************************************************************************************************/
    @Test
    public void test_default_notification()
      {
        // when
        final var underTest = notificationWithFeedback();
        // then
        assertThat(underTest.getText(), is(""));
        assertThat(underTest.getCaption(), is(""));
        assertThat(underTest.getFeedback().canCancel(), is(false));
        assertThat(underTest.getFeedback().canConfirm(), is(false));
      }

    /**********************************************************************************************************************************************************/
    @Test
    public void test_notification_with_hardwired_attributes()
      {
        // when
        final var underTest = notificationWithFeedback().withText("text")
                                                        .withCaption("caption");
        // then
        assertThat(underTest.getText(), is("text"));
        assertThat(underTest.getCaption(), is("caption"));
        assertThat(underTest.getFeedback().canCancel(), is(false));
        assertThat(underTest.getFeedback().canConfirm(), is(false));
      }

    /**********************************************************************************************************************************************************/
    @Test
    public void test_notification_with_bundle_attributes()
      {
        // when
        final var underTest = notificationWithFeedback().withText(UserNotificationWithFeedbackTest.class, "text", "param")
                                                        .withCaption(UserNotificationWithFeedbackTest.class, "caption", "param");
        // then
        assertThat(underTest.getText(), is("The text with param"));
        assertThat(underTest.getCaption(), is("The caption with param"));
        assertThat(underTest.getFeedback().canCancel(), is(false));
        assertThat(underTest.getFeedback().canConfirm(), is(false));
      }

    /**********************************************************************************************************************************************************/
    @Test
    public void test_notification_with_confirm()
            throws Throwable
      {
        // given
        final var underTest = notificationWithFeedback().withFeedback(feedback().withOnConfirm(onConfirm));
        // when
        underTest.confirm();
        // then
        assertThat(underTest.getFeedback().canCancel(), is(false));
        assertThat(underTest.getFeedback().canConfirm(), is(true));
        verify(onConfirm).call();
        verifyNoInteractions(onCancel);
      }

    /**********************************************************************************************************************************************************/
    @Test
    public void test_notification_with_cancel()
            throws Throwable
      {
        // given
        final var underTest = notificationWithFeedback().withFeedback(feedback().withOnCancel(onCancel));
        // when
        underTest.cancel();
        // then
        assertThat(underTest.getFeedback().canCancel(), is(true));
        assertThat(underTest.getFeedback().canConfirm(), is(false));
        verify(onCancel).call();
        verifyNoInteractions(onConfirm);
      }
  }