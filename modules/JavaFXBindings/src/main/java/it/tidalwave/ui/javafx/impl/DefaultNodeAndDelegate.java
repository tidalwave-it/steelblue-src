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
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.application.Platform;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import it.tidalwave.ui.javafx.JavaFXBinder;
import it.tidalwave.ui.javafx.JavaFXMenuBarControl;
import it.tidalwave.ui.javafx.JavaFXToolBarControl;
import it.tidalwave.ui.javafx.NodeAndDelegate;
import it.tidalwave.ui.javafx.impl.util.JavaFXSafeProxy;
import org.slf4j.LoggerFactory;
import it.tidalwave.util.PreferencesHandler;
import it.tidalwave.util.ReflectionUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/***************************************************************************************************************************************************************
 *
 * The implementation of {@link NodeAndDelegate}.
 *
 * @author  Fabrizio Giudici
 *
 **************************************************************************************************************************************************************/
@RequiredArgsConstructor @Getter @Slf4j
public class DefaultNodeAndDelegate<T> implements NodeAndDelegate<T>
  {
    private static final String P_TIMEOUT = DefaultNodeAndDelegate.class.getName() + ".initTimeout";
    private static final int INITIALIZER_TIMEOUT = Integer.getInteger(P_TIMEOUT, 10);

    public static final Map<Class<?>, Object> BEANS = new HashMap<>();

    @Getter
    private static final ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

    @Getter
    private static final JavaFXBinder javaFxBinder = new DefaultJavaFXBinder(executor);

    @Getter
    private static final JavaFXToolBarControl toolBarControl = new DefaultJavaFXToolBarControl();

    @Getter
    private static final JavaFXMenuBarControl menuBarControl = new DefaultJavaFXMenuBarControl();

    // @Getter
    // private static final JavaFXPanelGroupControl panelGroupControl = new DefaultJavaFXPanelGroupControl();

    @Getter @Setter
    private static boolean logDelegateInvocations = false;

    static
      {
        executor.setWaitForTasksToCompleteOnShutdown(false);
        executor.setThreadNamePrefix("javafxBinder-");
        // Fix for STB-26
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(1);
        executor.setQueueCapacity(10000);
        BEANS.put(JavaFXBinder.class, javaFxBinder);
        BEANS.put(Executor.class, executor);
        BEANS.put(JavaFXToolBarControl.class, toolBarControl);
        BEANS.put(JavaFXMenuBarControl.class, menuBarControl);
        // BEANS.put(JavaFXPanelGroupControl.class, panelGroupControl);
        BEANS.put(PreferencesHandler.class, PreferencesHandler.getInstance());
      }

    @Nonnull
    private final Node node;

    @Nonnull
    private final T delegate;

    /***********************************************************************************************************************************************************
     * Creates a {@link NodeAndDelegate} for the given presentation class. The FXML resource name is inferred by
     * default, For instance, is the class is named {@code JavaFXFooBarPresentation}, the resource name is
     * {@code FooBar.fxml} and searched in the same packages as the class.
     *
     * @see #of(java.lang.Class, java.lang.String)
     *
     * @since 1.0-ALPHA-13
     *
     * @param   presentationClass   the class of the presentation for which the resources must be created.
     **********************************************************************************************************************************************************/
    @Nonnull
    public static <T> NodeAndDelegate<T> of (@Nonnull final Class<T> presentationClass)
      {
        final var resource = presentationClass.getSimpleName().replaceAll("^JavaFX", "")
                                              .replaceAll("^JavaFx", "")
                                              .replaceAll("Presentation$", "")
                             + ".fxml";
        return of(presentationClass, resource);
      }

    /***********************************************************************************************************************************************************
     * Creates a {@link NodeAndDelegate} for the given presentation class.
     *
     * @param   presentationClass   the class of the presentation for which the resources must be created.
     * @param   fxmlResourcePath    the path of the FXML resource
     **********************************************************************************************************************************************************/
    @Nonnull
    public static <T> NodeAndDelegate<T> of (@Nonnull final Class<T> presentationClass, @Nonnull final String fxmlResourcePath)
      {
        final var log = LoggerFactory.getLogger(NodeAndDelegate.class);
        log.debug("of({}, {})", presentationClass, fxmlResourcePath);

        final var latch = new CountDownLatch(1);
        final var nad = new AtomicReference<NodeAndDelegate<T>>();
        final var exception = new AtomicReference<RuntimeException>();

        if (Platform.isFxApplicationThread())
          {
            try
              {
                return load(presentationClass, fxmlResourcePath);
              }
            catch (IOException e)
              {
                exception.set(new RuntimeException(e));
              }
          }

        Platform.runLater(() ->
          {
            try
              {
                nad.set(load(presentationClass, fxmlResourcePath));
              }
            catch (RuntimeException e)
              {
                exception.set(e);
              }
            catch (Exception e)
              {
                exception.set(new RuntimeException(e));
              }

            latch.countDown();
          });

        try
          {
            log.debug("Waiting for NodeAndDelegate initialisation in JavaFX thread...");
            log.debug("If deadlocks and you need longer time with the debugger, set {} (current value: {})", P_TIMEOUT, INITIALIZER_TIMEOUT);
            latch.await(INITIALIZER_TIMEOUT, TimeUnit.SECONDS); // FIXME
          }
        catch (InterruptedException e)
          {
            throw new RuntimeException(e);
          }

        if (exception.get() != null)
          {
            throw exception.get();
          }

        if (nad.get() == null)
          {
            final var message = String.format("Likely deadlock in the JavaFX Thread: couldn't create NodeAndDelegate: %s, %s",
                                              presentationClass, fxmlResourcePath);
            throw new RuntimeException(message);
          }

        return nad.get();
      }

    @Nonnull
    public static <T> NodeAndDelegate<T> load (@Nonnull final Class<T> clazz, @Nonnull final String resource)
            throws IOException
      {
        final var log = LoggerFactory.getLogger(NodeAndDelegate.class);
        log.debug("NodeAndDelegate({}, {})", clazz, resource);
        assert Platform.isFxApplicationThread() : "Not in JavaFX UI Thread";
        final var loader = new FXMLLoader(clazz.getResource(resource), null, null,
                                          type -> ReflectionUtils.instantiateWithDependencies(type, BEANS));
        try
          {
            final Node node = loader.load();
            final T jfxController = loader.getController();
            ReflectionUtils.injectDependencies(jfxController, BEANS);
            final var interfaces = jfxController.getClass().getInterfaces();

            if (interfaces.length == 0)
              {
                log.warn("{} has no interface: not creating safe proxy", jfxController.getClass());
                log.debug(">>>> load({}, {}) completed", clazz, resource);
                return new DefaultNodeAndDelegate<>(node, jfxController);
              }
            else
              {
                final var safeDelegate = JavaFXSafeProxy.of(jfxController, interfaces);
                log.debug(">>>> load({}, {}) completed", clazz, resource);
                return new DefaultNodeAndDelegate<>(node, safeDelegate);
              }
          }
        catch (IllegalStateException e)
          {
            final var message = String.format("ERROR: Cannot find resource: %s/%s", clazz.getPackageName().replace('.','/'), resource);
            log.error("ERROR: Cannot find resource: {}", message);
            throw new IllegalStateException(message);
          }
      }
  }
