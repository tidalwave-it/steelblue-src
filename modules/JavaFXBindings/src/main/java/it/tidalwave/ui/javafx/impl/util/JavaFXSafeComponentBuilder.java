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

import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.application.Platform;
import it.tidalwave.ui.javafx.JavaFXSafeProxyCreator;
import it.tidalwave.util.ReflectionUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import static lombok.AccessLevel.PRIVATE;

/***************************************************************************************************************************************************************
 *
 * @stereotype Factory
 *
 * @author  Fabrizio Giudici
 *
 **************************************************************************************************************************************************************/
@RequiredArgsConstructor(access = PRIVATE) @Slf4j
public final class JavaFXSafeComponentBuilder<I, T extends I>
  {
    @Nonnull
    private final Class<T> componentClass;

    @Nonnull
    private final Class<I> interfaceClass;

    private WeakReference<T> presentationRef = new WeakReference<>(null);

    @Nonnull
    public static <J, X extends J> JavaFXSafeComponentBuilder<J, X> builderFor (@Nonnull final Class<X> componentClass)
      {
        final var interfaceClass = (Class<J>)componentClass.getInterfaces()[0]; // FIXME: guess
        return new JavaFXSafeComponentBuilder<>(componentClass, interfaceClass);
      }

    @Nonnull
    public static <J, X extends J> JavaFXSafeComponentBuilder<J, X> builderFor (@Nonnull final Class<J> interfaceClass,
                                                                                @Nonnull final Class<X> componentClass)
      {
        return new JavaFXSafeComponentBuilder<>(componentClass, interfaceClass);
      }

    /***********************************************************************************************************************************************************
     * Creates an instance of a surrogate JavaFX delegate. JavaFX delegates (controllers in JavaFX jargon) are those
     * objects with fields annotated with {@link @FXML} that are created by the {@link FXMLLoader} starting from a
     * {@code .fxml} file. Sometimes a surrogate delegate is needed, that is a class that is not mapped to any
     * {@link @FXML} file, but whose fields are copied from another existing delegate.
     *
     * @param   componentClass      the class of the surrogate
     * @param   fxmlFieldsSource    the existing JavaFX delegate with {@code @FXML} annotated fields.
     * @return                      the new surrogate delegate
     **********************************************************************************************************************************************************/
    @Nonnull
    public static <J, X extends J> X createInstance (@Nonnull final Class<X> componentClass,
                                                     @Nonnull final Object fxmlFieldsSource)
      {
        final JavaFXSafeComponentBuilder<J, X> builder = builderFor(componentClass);
        return builder.createInstance(fxmlFieldsSource);
      }

    /***********************************************************************************************************************************************************
     * Creates an instance of a surrogate JavaFX delegate. JavaFX delegates (controllers in JavaFX jargon) are those
     * objects with fields annotated with {@link @FXML} that are created by the {@link FXMLLoader} starting from a
     * {@code .fxml} file. Sometimes a surrogate delegate is needed, that is a class that is not mapped to any
     * {@link @FXML} file, but whose fields are copied from another existing delegate.
     *
     * @param   fxmlFieldsSource    the existing JavaFX delegate with {@code @FXML} annotated fields.
     * @return                      the new surrogate delegate
     **********************************************************************************************************************************************************/
    @Nonnull
    public synchronized T createInstance (@Nonnull final Object fxmlFieldsSource)
      {
        log.trace("createInstance({})", fxmlFieldsSource);
        var presentation = presentationRef.get();

        if (presentation == null)
          {
            presentation = Platform.isFxApplicationThread() ? createComponentInstance() : createComponentInstanceInJAT();
            copyFxmlFields(presentation, fxmlFieldsSource); // FIXME: in JFX thread?

            try // FIXME // FIXME: in JFX thread?
              {
                presentation.getClass().getDeclaredMethod("initialize").invoke(presentation);
              }
            catch (NoSuchMethodException | SecurityException | IllegalAccessException
                 | InvocationTargetException e)
              {
                log.warn("No postconstruct in {}", presentation);
              }

            presentation = (T)Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                                                     new Class[] { interfaceClass },
                                                     new JavaFXSafeProxy<>(presentation));
            presentationRef = new WeakReference<>(presentation);
          }

        return presentation;
      }

    /***********************************************************************************************************************************************************
     *
     **********************************************************************************************************************************************************/
    @Nonnull
    private T createComponentInstance()
      {
        return ReflectionUtils.instantiateWithDependencies(componentClass, JavaFXSafeProxyCreator.BEANS);
      }

    /***********************************************************************************************************************************************************
     *
     **********************************************************************************************************************************************************/
    @Nonnull
    private T createComponentInstanceInJAT()
      {
        final var reference = new AtomicReference<T>();
        final var countDownLatch = new CountDownLatch(1);

        Platform.runLater(() ->
          {
            reference.set(createComponentInstance());
            countDownLatch.countDown();
          });

        try
          {
            countDownLatch.await();
          }
        catch (InterruptedException e)
          {
            log.error("", e);
            throw new RuntimeException(e);
          }

        return reference.get();
      }

    /***********************************************************************************************************************************************************
     * Inject fields annotated with {@link FXML} in {@code source} to {@code target}.
     *
     * @param   target  the target object
     * @param   source  the source object
     **********************************************************************************************************************************************************/
    private void copyFxmlFields (@Nonnull final Object target, @Nonnull final Object source)
      {
        log.debug("injecting {} with fields from {}", target, source);
        final Map<String, Object> valuesMapByFieldName = new HashMap<>();

        for (final var field : source.getClass().getDeclaredFields())
          {
            if (field.getAnnotation(FXML.class) != null)
              {
                final var name = field.getName();

                try
                  {
                    field.setAccessible(true);
                    final var value = field.get(source);
                    valuesMapByFieldName.put(name, value);
                    log.trace(">>>> available field {}: {}", name, value);
                  }
                catch (IllegalArgumentException | IllegalAccessException e)
                  {
                    throw new RuntimeException("Cannot read field " + name + " from " + source, e);
                  }
              }
          }

        for (final var field : target.getClass().getDeclaredFields())
          {
            final var fxml = field.getAnnotation(FXML.class);

            if (fxml != null)
              {
                final var name = field.getName();
                final var value = valuesMapByFieldName.get(name);

                if (value == null)
                  {
                    throw new RuntimeException("Can't inject " + name + ": available: " + valuesMapByFieldName.keySet());
                  }

                field.setAccessible(true);

                try
                  {
                    field.set(target, value);
                  }
                catch (IllegalArgumentException | IllegalAccessException e)
                  {
                    throw new RuntimeException("Cannot inject field " + name + " to " + target, e);
                  }
              }
          }

          ReflectionUtils.injectDependencies(target, JavaFXSafeProxyCreator.BEANS);
      }
  }
