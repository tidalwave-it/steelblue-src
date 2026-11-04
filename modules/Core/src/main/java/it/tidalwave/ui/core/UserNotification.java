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
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import static it.tidalwave.util.BundleUtilities.getMessage;
import static lombok.AccessLevel.PROTECTED;
/***************************************************************************************************************************************************************
 *
 * This class models a notification that will be presented to a user, without a feedback.
 *
 * @since   2.0-ALPHA-2
 * @author  Fabrizio Giudici
 *
 **************************************************************************************************************************************************************/
@Getter @Immutable
@RequiredArgsConstructor(access = PROTECTED) @ToString
public class UserNotification
  {
    @Nonnull
    protected final String text;
    @Nonnull
    protected final String caption;
    /***********************************************************************************************************************************************************
     * {@return a notification with empty caption and text}.
     **********************************************************************************************************************************************************/
    @Nonnull
    public static UserNotification notification()
      {
        return new UserNotification("", "");
      }
    /***********************************************************************************************************************************************************
     * {@return a notification with a caption}.
     * @param  caption       the caption
     **********************************************************************************************************************************************************/
    @Nonnull
    public UserNotification withCaption (@Nonnull final String caption)
      {
        return new UserNotification(text, caption);
      }
    /***********************************************************************************************************************************************************
     * {@return a notification with a caption from a bundle}.
     * @param  bundleClass   the class where to search the resource bundle from
     * @param  resourceName  the resource name of the caption in the bundle
     * @param  params        some (optional) parameters to the resource
     **********************************************************************************************************************************************************/
    @Nonnull
    public UserNotification withCaption (@Nonnull final Class<?> bundleClass, @Nonnull final String resourceName, @Nonnull final Object... params)
      {
        return new UserNotification(text, getMessage(bundleClass, resourceName, params));
      }
    /***********************************************************************************************************************************************************
     * {@return a notification with a text}.
     * @param  text          the text
     **********************************************************************************************************************************************************/
    @Nonnull
    public UserNotification withText (@Nonnull final String text)
      {
        return new UserNotification(text, caption);
      }
    /***********************************************************************************************************************************************************
     * {@return a notification with a text from a bundle}.
     * @param  bundleClass   the class where to search the resource bundle from
     * @param  resourceName  the resource name of the text in the bundle
     * @param  params        some (optional) parameters to the resource
     **********************************************************************************************************************************************************/
    @Nonnull
    public UserNotification withText (@Nonnull final Class<?> bundleClass,  @Nonnull final String resourceName, @Nonnull final Object... params)
      {
        return new UserNotification(getMessage(bundleClass, resourceName, params), caption);
      }
  }
