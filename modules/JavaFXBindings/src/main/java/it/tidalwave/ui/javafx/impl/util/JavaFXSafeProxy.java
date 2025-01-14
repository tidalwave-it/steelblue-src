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
package it.tidalwave.ui.javafx.impl.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import jakarta.annotation.Nonnull;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import javafx.application.Platform;
import org.slf4j.LoggerFactory;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import static java.util.Arrays.asList;
import static it.tidalwave.util.CollectionUtils.concat;

/***************************************************************************************************************************************************************
 *
 * An {@link InvocationHandler} that safely wraps all method calls with {@link Platform#runLater(Runnable)}. The caller
 * is not blocked if the method is declared as {@code void}; it is blocked otherwise, so it can immediately retrieve
 * the result.
 *
 * This behaviour is required by {@link it.tidalwave.ui.javafx.NodeAndDelegate#of(Class)} ()}.
 *
 * TODO: add support for aysnc returning a Future.
 *
 * @author  Fabrizio Giudici
 *
 **************************************************************************************************************************************************************/
@RequiredArgsConstructor @Slf4j
public class JavaFXSafeProxy implements InvocationHandler
  {
    /***********************************************************************************************************************************************************
     * An auxiliary interface that is always injected to the proxy, allowing to retrive the class of the proxied object.
     **********************************************************************************************************************************************************/
    public interface Proxied
      {
        /** {@return the class of the proxied object}. */
        @Nonnull
        public Class<?> __getProxiedClass();
      }

    @Getter @Setter
    private static boolean logDelegateInvocations = false;

    @Nonnull @Getter @Setter
    private Object delegate;

    /***********************************************************************************************************************************************************
     *
     **********************************************************************************************************************************************************/
    @Nonnull @SuppressWarnings("unchecked")
    public static <T> T of (@Nonnull final T target, @Nonnull final Class<?>[] interfaces)
      {
        final var augmentedInterfaces = concat(asList(interfaces), Proxied.class).toArray(Class<?>[]::new);
        final var contextClassLoader = Thread.currentThread().getContextClassLoader();
        return (T)Proxy.newProxyInstance(contextClassLoader, augmentedInterfaces, new JavaFXSafeProxy(target));
      }

    /***********************************************************************************************************************************************************
     * {@inheritDoc}
     **********************************************************************************************************************************************************/
    @Override
    public Object invoke (@Nonnull final Object proxy, @Nonnull final Method method, @Nonnull final Object[] args)
      throws Throwable
      {
        if ("__getProxiedClass".equals(method.getName()))
          {
            return delegate.getClass();
          }

        final var result = new AtomicReference<>();
        final var throwable = new AtomicReference<Throwable>();
        final var waitForReturn = new CountDownLatch(1);

        JavaFXSafeRunner.runSafely(() ->
          {
            try
              {
                if (logDelegateInvocations)
                  {
                    logInvocation(delegate.getClass(), method, args);
                  }

                result.set(method.invoke(delegate, args));
              }
            catch (Throwable t)
              {
                throwable.set(t);
                log.error("Exception while calling JavaFX", t);
              }
            finally
              {
                waitForReturn.countDown();
              }
          });

        log.trace(">>>> waiting for method completion");
        waitForReturn.await();

        // This is probably useless - void methods return asynchronously
        if (throwable.get() != null)
          {
            throw throwable.get();
          }

        return method.getReturnType().equals(void.class) ? null : result.get();
      }

    /***********************************************************************************************************************************************************
     *
     **********************************************************************************************************************************************************/
    private static void logInvocation (@Nonnull final Class<?> clazz, @Nonnull final Method method, @Nonnull final Object[] args)
      {
        final var logger = LoggerFactory.getLogger(clazz);

        if (logger.isDebugEnabled())
          {
            final var builder = new StringBuilder();
            builder.append(method.getName());
            builder.append("(");
            var separator = "";

            for (final Object arg : args)
              {
                builder.append(separator);
                builder.append(arg != null ? arg.toString() : null);
                separator = ", ";
              }

            builder.append(")");
            logger.debug(builder.toString());
          }
      }
  }

