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
package it.tidalwave.role.ui.javafx.impl.util;

import javax.annotation.Nonnull;
import javafx.application.Platform;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
@Slf4j @NoArgsConstructor(access=AccessLevel.PRIVATE)
public final class JavaFXSafeRunner
  {
    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    public static void runSafely (@Nonnull final Runnable runnable)
      {
        final Runnable guardedRunnable = () ->
          {
            try
              {
                runnable.run();
              }
            catch (Throwable t)
              {
                log.warn("", t);
              }
          };

        if (Platform.isFxApplicationThread())
          {
            guardedRunnable.run();
          }
        else
          {
            Platform.runLater(guardedRunnable);
          }
      }
  }
