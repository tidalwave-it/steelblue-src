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
package it.tidalwave.ui.javafx.impl.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import javax.annotation.Nonnull;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import javafx.application.Platform;
import it.tidalwave.ui.javafx.JavaFXSafeProxyCreator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/***************************************************************************************************************************************************************
 *
 * An {@link InvocationHandler} that safely wraps all method calls with {@link Platform#runLater(Runnable)}. The caller
 * is not blocked if the method is declared as {@code void}; it is blocked otherwise, so it can immediately retrieve
 * the result.
 *
 * This behaviour is required by {@link JavaFXSafeProxyCreator#createNodeAndDelegate(Class)} ()}.
 *
 * TODO: add support for aysnc returning a Future.
 *
 * @author  Fabrizio Giudici
 *
 **************************************************************************************************************************************************************/
@RequiredArgsConstructor @Slf4j
public class JavaFXSafeProxy<T> implements InvocationHandler
  {
    @Nonnull @Getter @Setter
    private T delegate;

    @Override
    public Object invoke (@Nonnull final Object proxy, @Nonnull final Method method, @Nonnull final Object[] args)
      throws Throwable
      {
        final var result = new AtomicReference<>();
        final var throwable = new AtomicReference<Throwable>();
        final var waitForReturn = new CountDownLatch(1);

        JavaFXSafeRunner.runSafely(() ->
          {
            try
              {
                log.trace(">>>> safely invoking {}", method);
                result.set(method.invoke(delegate, args));
              }
            catch (Throwable t)
              {
                throwable.set(t);
                log.warn("Exception while calling JavaFX", t);
              }
            finally
              {
                waitForReturn.countDown();
              }
          });

        if (method.getReturnType().equals(void.class))
          {
            return null;
          }

        log.trace(">>>> waiting for method completion");
        waitForReturn.await();

        // This is probably useless - void methods return asynchronously
        if (throwable.get() != null)
          {
            throw throwable.get();
          }

        return result.get();
      }
  }

