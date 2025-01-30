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

import javax.annotation.concurrent.Immutable;
import jakarta.annotation.Nonnull;
import it.tidalwave.util.Callback;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.ToString;
import lombok.With;
import static it.tidalwave.util.BundleUtilities.getMessage;

/***************************************************************************************************************************************************************
 *
 * This class models a notification that will be presented to a user, and a feedback is expected (confirmation or cancellation).
 *
 * @since   2.0-ALPHA-2
 * @author  Fabrizio Giudici
 *
 **************************************************************************************************************************************************************/
@Getter @Immutable
@ToString(callSuper = true)
public class UserNotificationWithFeedback extends UserNotification
  {
    /***********************************************************************************************************************************************************
     * This class provides a few callback methods to notify a choice from the user.
     **********************************************************************************************************************************************************/
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Feedback
      {
        @With
        private final Callback onConfirm;

        @With
        private final Callback onCancel;

        /*******************************************************************************************************************************************************
         * {@return {@code true} if the current instance has a callback for confirmation}.
         ******************************************************************************************************************************************************/
        public boolean canConfirm()
          {
            return onConfirm != Callback.EMPTY;
          }

        /*******************************************************************************************************************************************************
         * {@return {@code true} if the current instance has a callback for cancellation}.
         ******************************************************************************************************************************************************/
        public boolean canCancel()
          {
            return onCancel != Callback.EMPTY;
          }

        /*******************************************************************************************************************************************************
         * Callback method invoked when the user confirms an operation.
         * @throws  Exception  in cases of error
         ******************************************************************************************************************************************************/
        @SuppressWarnings("RedundantThrows")
        @SneakyThrows(Throwable.class)
        private void onConfirm()
                throws Exception
          {
            onConfirm.call();
          }

        /*******************************************************************************************************************************************************
         * Callback method invoked when the user cancels an operation.
         * @throws  Exception  in cases of error
         ******************************************************************************************************************************************************/
        @SuppressWarnings("RedundantThrows")
        @SneakyThrows(Throwable.class)
        private void onCancel()
                throws Exception
          {
            onCancel.call();
          }
      }

    @Nonnull
    protected final Feedback feedback;

    /***********************************************************************************************************************************************************
     * @param text          the notification text
     * @param caption       the notification caption
     * @param feedback      the feedback
     **********************************************************************************************************************************************************/
    protected UserNotificationWithFeedback (@Nonnull final String text, @Nonnull final String caption, @Nonnull final Feedback feedback)
      {
        super(text, caption);
        this.feedback = feedback;
      }

    /***********************************************************************************************************************************************************
     * {@return a notification with empty caption and text}.
     **********************************************************************************************************************************************************/
    @Nonnull
    public static UserNotificationWithFeedback notificationWithFeedback()
      {
        return new UserNotificationWithFeedback("", "", feedback());
      }

    /***********************************************************************************************************************************************************
     * {@return a notification with a caption}.
     * @param  caption       the caption
     **********************************************************************************************************************************************************/
    @Override @Nonnull
    public UserNotificationWithFeedback withCaption (@Nonnull final String caption)
      {
        return new UserNotificationWithFeedback(text, caption, feedback);
      }

    /***********************************************************************************************************************************************************
     * {@return a notification with a caption from a bundle}.
     * @param  bundleClass   the class where to search the resource bundle from
     * @param  resourceName  the resource name of the caption in the bundle
     * @param  params        some (optional) parameters to the resource
     **********************************************************************************************************************************************************/
    @Override @Nonnull
    public UserNotificationWithFeedback withCaption (@Nonnull final Class<?> bundleClass, @Nonnull final String resourceName, @Nonnull final Object ... params)
      {
        return new UserNotificationWithFeedback(text, getMessage(bundleClass, resourceName, params), feedback);
      }

    /***********************************************************************************************************************************************************
     * {@return a notification with a text}.
     * @param  text          the text
     **********************************************************************************************************************************************************/
    @Override @Nonnull
    public UserNotificationWithFeedback withText (@Nonnull final String text)
      {
        return new UserNotificationWithFeedback(text, caption, feedback);
      }

    /***********************************************************************************************************************************************************
     * {@return a notification with a text from a bundle}.
     * @param  bundleClass   the class where to search the resource bundle from
     * @param  resourceName  the resource name of the text in the bundle
     * @param  params        some (optional) parameters to the resource
     **********************************************************************************************************************************************************/
    @Override @Nonnull
    public UserNotificationWithFeedback withText (@Nonnull final Class<?> bundleClass, @Nonnull final String resourceName, @Nonnull final Object ... params)
      {
        return new UserNotificationWithFeedback(getMessage(bundleClass, resourceName, params), caption, feedback);
      }

    /***********************************************************************************************************************************************************
     * {@return a notification {@link Feedback}}.
     * @param  feedback    the {@code Feedback} to associate
     **********************************************************************************************************************************************************/
    @Nonnull
    public UserNotificationWithFeedback withFeedback (@Nonnull final Feedback feedback)
      {
        return new UserNotificationWithFeedback(text, caption, feedback);
      }

    /***********************************************************************************************************************************************************
     * Notifies a confirmation to the user notification.
     * @throws  Exception  in cases of error
     **********************************************************************************************************************************************************/
    public void confirm()
      throws Exception
      {
        feedback.onConfirm();
      }

    /***********************************************************************************************************************************************************
     * Notifies a cancellation to the user notification.
     * @throws  Exception  in cases of error
     **********************************************************************************************************************************************************/
    public void cancel()
      throws Exception
      {
        feedback.onCancel();
      }

    /***********************************************************************************************************************************************************
     * {@return a new {@code Feedback} that does nothing}. This method should be chained with {@code withOnConfirm()} and/or {@code withOnCancel(Callback)} to
     * specify the relative callbacks.
     * <pre>{@code}
     *   feedback().withOnConfirm(this::doSomething).withOnCancel(this::doSomethingElse);
     * }</pre>
     **********************************************************************************************************************************************************/
    @Nonnull
    public static Feedback feedback()
      {
        return new Feedback(Callback.EMPTY, Callback.EMPTY);
      }
  }
