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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.application.Application;
import javafx.application.Platform;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import it.tidalwave.ui.core.annotation.Assemble;
import it.tidalwave.ui.core.annotation.PresentationAssembler;
import it.tidalwave.ui.core.message.PowerOffEvent;
import it.tidalwave.ui.core.message.PowerOnEvent;
import it.tidalwave.ui.javafx.JavaFXBinder;
import it.tidalwave.ui.javafx.JavaFXMenuBarControl;
import it.tidalwave.ui.javafx.JavaFXToolBarControl;
import it.tidalwave.ui.javafx.NodeAndDelegate;
import it.tidalwave.ui.javafx.impl.JavaFXSafeProxy.Proxied;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import it.tidalwave.util.Key;
import it.tidalwave.util.PreferencesHandler;
import it.tidalwave.util.TypeSafeMap;
import it.tidalwave.util.annotation.VisibleForTesting;
import it.tidalwave.messagebus.MessageBus;
import lombok.RequiredArgsConstructor;
import lombok.With;
import static java.util.stream.Collectors.toList;
import static it.tidalwave.util.CollectionUtils.concat;
import static it.tidalwave.util.FunctionalCheckedExceptionWrappers.*;
import static it.tidalwave.util.ShortNames.shortIds;
import static lombok.AccessLevel.PRIVATE;

/***************************************************************************************************************************************************************
 *
 * A base class for all variants of JavaFX applications with Spring.
 *
 * @author  Fabrizio Giudici
 *
 **************************************************************************************************************************************************************/
public abstract class AbstractJavaFXSpringApplication extends JavaFXApplicationWithSplash
  {
    /** Configures the JMetro style, light mode. @since 3.0-ALPHA-1 */
    public static final Consumer<Scene> STYLE_METRO_LIGHT = scene ->  new JMetro(Style.LIGHT).setScene(scene);

    /** Configures the JMetro style, dark mode.  @since 3.0-ALPHA-1 */
    public static final Consumer<Scene> STYLE_METRO_DARK = scene ->  new JMetro(Style.DARK).setScene(scene);

    /***********************************************************************************************************************************************************
     * The initialisation parameters to pass to {@link #launch(Class, InitParameters)}.
     * @since   1.1-ALPHA-6
     **********************************************************************************************************************************************************/
    @RequiredArgsConstructor(access = PRIVATE) @With
    public static class InitParameters
      {
        @Nonnull
        private final String[] args;

        @Nonnull
        private final String applicationName;

        @Nonnull
        private final String logFolderPropertyName;

        private final boolean implicitExit;

        @Nonnull
        private final TypeSafeMap propertyMap;

        @Nonnull
        private final List<Consumer<Scene>> sceneFinalizers;

        @Nonnull
        public <T> InitParameters withProperty (@Nonnull final Key<T> key, @Nonnull final T value)
          {
            return new InitParameters(args, applicationName, logFolderPropertyName, implicitExit, propertyMap.with(key, value), sceneFinalizers);
          }

        @Nonnull
        public InitParameters withSceneFinalizer (@Nonnull final Consumer<Scene> stageFinalizer)
          {
            return new InitParameters(args, applicationName, logFolderPropertyName, implicitExit, propertyMap, concat(sceneFinalizers, stageFinalizer));
          }

        private void validate()
          {
            requireNotEmpty(applicationName, "applicationName");
            requireNotEmpty(logFolderPropertyName, "logFolderPropertyName");
          }

        private static void requireNotEmpty (@Nullable final String name, @Nonnull final String message)
          {
            if (name == null || name.isEmpty())
              {
                throw new IllegalArgumentException(message);
              }
          }
      }

    public static final String APPLICATION_MESSAGE_BUS_BEAN_NAME = "applicationMessageBus";

    private static final Map<Class<?>, Object> BEANS = new HashMap<>();

    private static final int QUEUE_CAPACITY = 10000;

    private static InitParameters initParameters;

    protected final ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

    protected final JavaFXBinder javaFxBinder = new DefaultJavaFXBinder(executor);

    protected final JavaFXToolBarControl toolBarControl = new DefaultJavaFXToolBarControl();

    protected final JavaFXMenuBarControl menuBarControl = new DefaultJavaFXMenuBarControl();

    // Don't use Lombok and its static logger - give Main a chance to initialize things
    private final Logger log = LoggerFactory.getLogger(AbstractJavaFXSpringApplication.class);

    private ConfigurableApplicationContext applicationContext;

    private Optional<MessageBus> messageBus = Optional.empty();

    /***********************************************************************************************************************************************************
     * Launches the application.
     * @param   appClass          the class of the application to instantiate
     * @param   initParameters    the initialisation parameters
     **********************************************************************************************************************************************************/
    @SuppressFBWarnings("DM_EXIT")
    public static void launch (@Nonnull final Class<? extends Application> appClass, @Nonnull final InitParameters initParameters)
      {
        try
          {
            initParameters.validate();
            System.setProperty(PreferencesHandler.PROP_APP_NAME, initParameters.applicationName);
            Platform.setImplicitExit(initParameters.implicitExit);
            final var preferencesHandler = PreferencesHandler.getInstance();
            initParameters.propertyMap.forEach(preferencesHandler::setProperty);
            System.setProperty(initParameters.logFolderPropertyName, preferencesHandler.getLogFolder().toAbsolutePath().toString());
            JavaFXSafeProxy.setLogDelegateInvocations(initParameters.propertyMap.getOptional(K_LOG_DELEGATE_INVOCATIONS).orElse(false));
            AbstractJavaFXSpringApplication.initParameters = initParameters;
            launch(appClass, initParameters.args);
          }
        catch (Throwable t)
          {
            // Don't use logging facilities here, they could be not initialized
            t.printStackTrace();
            System.exit(-1);
          }
      }

    /***********************************************************************************************************************************************************
     *
     **********************************************************************************************************************************************************/
    @Nonnull
    public static Map<Class<?>, Object> getBeans()
      {
        return Collections.unmodifiableMap(BEANS);
      }

    /***********************************************************************************************************************************************************
     *
     **********************************************************************************************************************************************************/
    protected AbstractJavaFXSpringApplication()
      {
        executor.setWaitForTasksToCompleteOnShutdown(false);
        executor.setAwaitTerminationMillis(2000); // FIXME
        executor.setThreadNamePrefix("ui-service-pool-");
        // Fix for STB-26
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(1);
        executor.setQueueCapacity(QUEUE_CAPACITY);
        executor.initialize(); // it's used before Spring completes initialisation
        BEANS.put(JavaFXBinder.class, javaFxBinder);
        BEANS.put(JavaFXToolBarControl.class, toolBarControl);
        BEANS.put(JavaFXMenuBarControl.class, menuBarControl);
        BEANS.put(PreferencesHandler.class, PreferencesHandler.getInstance());
      }

    /***********************************************************************************************************************************************************
     * {@return an empty set of parameters} to populate and pass to {@link #launch(Class, InitParameters)}
     * @since   1.1-ALPHA-6
     **********************************************************************************************************************************************************/
    @Nonnull
    protected static InitParameters params()
      {
        return new InitParameters(new String[0], "", "", true, TypeSafeMap.newInstance(), List.of());
      }

    /***********************************************************************************************************************************************************
     *
     **********************************************************************************************************************************************************/
    @Override @Nonnull
    protected NodeAndDelegate<?> createParent()
      {
        return NodeAndDelegate.of(getClass(), applicationFxml);
      }

    /***********************************************************************************************************************************************************
     *
     **********************************************************************************************************************************************************/
    @Override
    protected void initializeInBackground()
      {
        log.info("initializeInBackground()");
        System.getProperties().forEach((name, value) -> log.debug("{}: {}", name, value));
        // TODO: workaround for NWRCA-41
        System.setProperty("it.tidalwave.util.spring.ClassScanner.basePackages", "it");
        applicationContext = createApplicationContext();
        applicationContext.registerShutdownHook(); // this actually seems not working, onClosing() does

        if (applicationContext.containsBean(APPLICATION_MESSAGE_BUS_BEAN_NAME))
          {
            messageBus = Optional.of(applicationContext.getBean(APPLICATION_MESSAGE_BUS_BEAN_NAME, MessageBus.class));
          }
      }

    /***********************************************************************************************************************************************************
     * {@return a created application context.}
     **********************************************************************************************************************************************************/
    @Nonnull
    protected abstract ConfigurableApplicationContext createApplicationContext();

    /***********************************************************************************************************************************************************
     *
     **********************************************************************************************************************************************************/
    @Override @Nonnull
    protected Scene createScene (@Nonnull final Parent parent)
      {
        final var scene = super.createScene(parent);
        initParameters.sceneFinalizers.forEach(f -> f.accept(scene));
        return scene;
      }

    /***********************************************************************************************************************************************************
     * {@inheritDoc}
     **********************************************************************************************************************************************************/
    @Override
    protected final void onStageCreated (@Nonnull final Stage stage, @Nonnull final NodeAndDelegate<?> applicationNad)
      {
        assert Platform.isFxApplicationThread();
        javaFxBinder.setMainWindow(stage);
        onStageCreated2(applicationNad);
      }

    /***********************************************************************************************************************************************************
     * This method is separated to make testing simpler (it does not depend on JavaFX stuff).
     * @param   applicationNad
     **********************************************************************************************************************************************************/
    @VisibleForTesting final void onStageCreated2 (@Nonnull final NodeAndDelegate<?> applicationNad)
      {
        Objects.requireNonNull(applicationContext, "applicationContext is null");
        final var delegate = applicationNad.getDelegate();
        final var actualDelegate = getActualDelegate(delegate);
        log.info("Application presentation delegate: {} --- actual: {}", delegate, actualDelegate);

        if (actualDelegate.getClass().getAnnotation(PresentationAssembler.class) != null)
          {
            callAssemble(actualDelegate);
          }

        callPresentationAssemblers();
        executor.execute(() ->
          {
            onStageCreated(applicationContext);
            messageBus.ifPresent(mb -> mb.publish(new PowerOnEvent())); // must be after onStageCreated()
          });
      }

    /***********************************************************************************************************************************************************
     * Invoked when the {@link Stage} is created and the {@link ApplicationContext} has been initialized. Typically, the main class overrides this, retrieves
     * a reference to the main controller and boots it. This method is executed in a background thread.
     * @param   applicationContext  the application context
     **********************************************************************************************************************************************************/
    protected void onStageCreated (@Nonnull final ApplicationContext applicationContext)
      {
      }

    /***********************************************************************************************************************************************************
     * {@inheritDoc}
     **********************************************************************************************************************************************************/
    @Override
    protected final void onCloseRequest()
      {
        log.info("onCloseRequest()");
        messageBus.ifPresent(mb -> mb.publish(new PowerOffEvent()));
        executor.execute(() ->
          {
            // applicationContext.close();
            Platform.runLater(() ->
              {
                Platform.exit();
                exit();
              });
          });
      }

    /***********************************************************************************************************************************************************
     *
     **********************************************************************************************************************************************************/
    protected void exit()
      {
        System.exit(0);
      }

    /***********************************************************************************************************************************************************
     * Finds all classes annotated with {@link PresentationAssembler} and invokes methods annotated with {@link Assemble}.
     **********************************************************************************************************************************************************/
    private void callPresentationAssemblers()
      {
        applicationContext.getBeansWithAnnotation(PresentationAssembler.class).values().forEach(this::callAssemble);
      }

    /***********************************************************************************************************************************************************
     * Call a method annotated with {@link Assemble} in the given object.
     * @param     assembler       the assembler
     **********************************************************************************************************************************************************/
    private void callAssemble (@Nonnull final Object assembler)
      {
        log.info("Calling presentation assembler: {}", assembler);
        Arrays.stream(assembler.getClass().getDeclaredMethods())
              .filter(_p(m -> m.getDeclaredAnnotation(Assemble.class) != null))
              .forEach(_c(m -> invokeInjecting(m, assembler, this::resolveBean)));
      }

    /***********************************************************************************************************************************************************
     * Instantiates an object of the given class performing dependency injections through the constructor.
     * TODO: possibly replace with a Spring utility doing method injection.
     * @throws        RuntimeException if something fails
     **********************************************************************************************************************************************************/
    private void invokeInjecting (@Nonnull final Method method, @Nonnull final Object object, @Nonnull final Function<Class<?>, Object> beanFactory)
      {
        try
          {
            final var parameters = Arrays.stream(method.getParameterTypes()).map(beanFactory).collect(toList());
            log.info(">>>> calling {}({})", method.getName(), shortIds(parameters));
            method.invoke(object, parameters.toArray());
          }
        catch (IllegalAccessException | InvocationTargetException e)
          {
            throw new RuntimeException(e);
          }
      }

    /***********************************************************************************************************************************************************
     *
     **********************************************************************************************************************************************************/
    @Nonnull
    private <T> T resolveBean (@Nonnull final Class<T> type)
      {
        return type.cast(Optional.ofNullable(BEANS.get(type)).orElseGet(() -> applicationContext.getBean(type)));
      }

    /***********************************************************************************************************************************************************
     *
     **********************************************************************************************************************************************************/
    @Nonnull
    private static Object getActualDelegate (@Nonnull final Object delegate)
      {
        return delegate instanceof Proxied ? ((Proxied)delegate).__getProxiedObject() : delegate;
      }
  }
