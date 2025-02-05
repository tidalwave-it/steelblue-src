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
import jakarta.annotation.Nullable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;
import javafx.stage.Window;
import javafx.application.Platform;
import lombok.extern.slf4j.Slf4j;

/***************************************************************************************************************************************************************
 *
 * A support class for all JavaFX integration tests with TestFX.
 *
 * @author  Fabrizio Giudici
 *
 **************************************************************************************************************************************************************/
@Slf4j
public abstract class JavaFXTestSupport extends TestNGApplicationTest
  {
    protected Supplier<Window> windowSupplier = () -> null;

    /***********************************************************************************************************************************************************
     * Safely runs a task, ensuring that is executed in the JavaFX thread, and wait for the completion.
     * @param     name      the name of the task, used in logs
     * @param     runnable  the task
     **********************************************************************************************************************************************************/
    protected void runSafelyAndWait (@Nonnull final String name, @Nonnull final Runnable runnable)
      {
        runSafelyAndWait(name, null, runnable);
      }

    /***********************************************************************************************************************************************************
     * Safely runs a task, ensuring that is executed in the JavaFX thread, and wait for the completion. This version also waits for the executor service to
     * complete all pending tasks.
     * @param     executor  the executor (optional)
     * @param     name      the name of the task, used in logs
     * @param     runnable  the task
     **********************************************************************************************************************************************************/
    protected void runSafelyAndWait (@Nonnull final String name, @Nullable final ExecutorService executor, @Nonnull final Runnable runnable)
      {
        log.info("runSafelyAndWait({})", name);

        if (Platform.isFxApplicationThread())
          {
            runnable.run();
          }
        else
          {
            Platform.runLater(runnable);
            waitForJavaFXJobsDone(name);
          }

        for (int i = 0; i < 10; i++)
          {
            if (executor != null)
              {
                waitForExecutorJobsDone(name, executor);
              }

            waitForJavaFXJobsDone(name); // possibly submitted by the JavaFX thread.
          }

        try
          {
            Thread.sleep(1000); // give time for the UI component to be seen in attended tests
          }
        catch (InterruptedException e)
          {
            throw new RuntimeException(e);
          }
      }

    /***********************************************************************************************************************************************************
     *
     **********************************************************************************************************************************************************/
    private static void waitForExecutorJobsDone (@Nonnull final String name, @Nonnull final ExecutorService executorService)
      {
        final var latch = new CountDownLatch(1);
        executorService.execute(latch::countDown);
        log.info(">>>> [{}] waiting for executor to complete...", name);
        waitForLatch(latch);
        log.info(">>>> [{}] executor completed...", name);
      }

    /***********************************************************************************************************************************************************
     *
     **********************************************************************************************************************************************************/
    private static void waitForJavaFXJobsDone (@Nonnull final String name)
      {
        if (!Platform.isFxApplicationThread())
          {
            final var latch = new CountDownLatch(1);
            Platform.runLater(latch::countDown);
            log.info(">>>> [{}] waiting for JavaFX thread to complete...", name);
            waitForLatch(latch);
            log.info(">>>> [{}] JavaFX thread completed...", name);
          }
      }

    /***********************************************************************************************************************************************************
     *
     **********************************************************************************************************************************************************/
    private static void waitForLatch (@Nonnull final CountDownLatch latch)
      {
        try
          {
            if (!latch.await(1, TimeUnit.SECONDS))
              {
                throw new RuntimeException(new TimeoutException());
              }
          }
        catch (InterruptedException e)
          {
            throw new RuntimeException(e);
          }
      }
  }