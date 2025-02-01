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
package it.tidalwave.ui.javafx.impl;

import jakarta.annotation.Nonnull;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import javafx.application.Platform;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

/***************************************************************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **************************************************************************************************************************************************************/
@UtilityClass @Slf4j
public final class JavaFXSafeRunner
  {
    private static final String P_TIMEOUT = DefaultNodeAndDelegate.class.getName() + ".initTimeout";
    private static final int INITIALIZER_TIMEOUT = Integer.getInteger(P_TIMEOUT, 10);

    /***********************************************************************************************************************************************************
     * {@return the value provided by the given {@link Callable}, running it in the JavaFX thread}.
     * @param   callable    the callable
     **********************************************************************************************************************************************************/
    @SneakyThrows
    public static <T> T runSafelyAndWait (@Nonnull final Callable<T> callable)
          throws Exception
      {
        if (Platform.isFxApplicationThread())
          {
            return callable.call();
          }

        final var latch = new CountDownLatch(1);
        final var result = new AtomicReference<T>();
        final var exception = new AtomicReference<Throwable>();
        final var timeoutMessage = String.format("Likely deadlock in the JavaFX Thread. If you need longer time with the debugger, set -D%s=<v> (current : %s)",
                                                 P_TIMEOUT, INITIALIZER_TIMEOUT);
        Platform.runLater(() ->
          {
            try
              {
                result.set(callable.call());
              }
            catch (Throwable e)
              {
                exception.set(e);
              }
            finally
              {
                latch.countDown();
              }
          });

        try
          {
            log.debug("Waiting for JavaFX thread...");

            if (!latch.await(INITIALIZER_TIMEOUT, TimeUnit.SECONDS))
              {
                throw new RuntimeException(timeoutMessage);
              }
          }
        catch (InterruptedException e)
          {
            log.info("Interrupted");
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
          }

        if (exception.get() != null)
          {
            throw exception.get();
          }

        return result.get();
      }
  }
