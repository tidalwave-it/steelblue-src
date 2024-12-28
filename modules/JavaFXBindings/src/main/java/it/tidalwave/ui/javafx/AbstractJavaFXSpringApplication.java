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
package it.tidalwave.ui.javafx;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.io.IOException;
import javafx.stage.Stage;
import javafx.application.Application;
import javafx.application.Platform;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import it.tidalwave.message.PowerOffEvent;
import it.tidalwave.message.PowerOnEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import it.tidalwave.util.Key;
import it.tidalwave.util.PreferencesHandler;
import it.tidalwave.util.TypeSafeMap;
import it.tidalwave.util.annotation.VisibleForTesting;
import it.tidalwave.messagebus.MessageBus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.With;
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
        public <T> InitParameters withProperty (@Nonnull final Key<T> key, @Nonnull final T value)
          {
            return new InitParameters(args, applicationName, logFolderPropertyName, implicitExit, propertyMap.with(key, value));
          }

        public void validate()
          {
            requireNotEmpty(applicationName, "applicationName");
            requireNotEmpty(logFolderPropertyName, "logFolderPropertyName");
          }

        private void requireNotEmpty (@CheckForNull final String name, @Nonnull final String message)
          {
            if (name == null || name.isEmpty())
              {
                throw new IllegalArgumentException(message);
              }
          }
      }

    protected static final String APPLICATION_MESSAGE_BUS = "applicationMessageBus";

    // Don't use Slf4j and its static logger - give Main a chance to initialize things
    private final Logger log = LoggerFactory.getLogger(AbstractJavaFXSpringApplication.class);

    private ConfigurableApplicationContext applicationContext;

    private Optional<MessageBus> messageBus = Optional.empty();

    @Getter(AccessLevel.PACKAGE) @Nonnull
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

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
            Application.launch(appClass, initParameters.args);
          }
        catch (Throwable t)
          {
            // Don't use logging facilities here, they could be not initialized
            t.printStackTrace();
            System.exit(-1);
          }
      }

    /***********************************************************************************************************************************************************
     * {@return an empty set of parameters} to populate and pass to {@link #launch(Class, InitParameters)}
     * @since   1.1-ALPHA-6
     **********************************************************************************************************************************************************/
    @Nonnull
    protected static InitParameters params()
      {
        return new InitParameters(new String[0], "", "", true, TypeSafeMap.newInstance());
      }

    /***********************************************************************************************************************************************************
     *
     **********************************************************************************************************************************************************/
    @Override @Nonnull
    protected NodeAndDelegate<?> createParent()
            throws IOException
      {
        return NodeAndDelegate.load(getClass(), applicationFxml);
      }

    /***********************************************************************************************************************************************************
     *
     **********************************************************************************************************************************************************/
    @Override
    protected void initializeInBackground()
      {
        log.info("initializeInBackground()");

        try
          {
            logProperties();
            // TODO: workaround for NWRCA-41
            System.setProperty("it.tidalwave.util.spring.ClassScanner.basePackages", "it");
            applicationContext = createApplicationContext();
            applicationContext.registerShutdownHook(); // this actually seems not working, onClosing() does

            if (applicationContext.getBeanFactory().containsBean(APPLICATION_MESSAGE_BUS))
              {
                messageBus = Optional.of(applicationContext.getBeanFactory().getBean(APPLICATION_MESSAGE_BUS, MessageBus.class));
              }
          }
        catch (Throwable t)
          {
            log.error("", t);
          }
      }

    /***********************************************************************************************************************************************************
     * {@return a created application context.}
     **********************************************************************************************************************************************************/
    @Nonnull
    protected abstract ConfigurableApplicationContext createApplicationContext();

    /***********************************************************************************************************************************************************
     * {@inheritDoc}
     **********************************************************************************************************************************************************/
    @Override
    protected final void onStageCreated (@Nonnull final Stage stage, @Nonnull final NodeAndDelegate<?> applicationNad)
      {
        assert Platform.isFxApplicationThread();
        JavaFXSafeProxyCreator.getJavaFxBinder().setMainWindow(stage);
        onStageCreated2(applicationNad);
      }

    /***********************************************************************************************************************************************************
     * This method is separated to make testing simpler (it does not depend on JavaFX stuff).
     * @param   applicationNad
     **********************************************************************************************************************************************************/
    @VisibleForTesting final void onStageCreated2 (@Nonnull final NodeAndDelegate<?> applicationNad)
      {
        final var delegate = applicationNad.getDelegate();

        if (PresentationAssembler.class.isAssignableFrom(delegate.getClass()))
          {
            ((PresentationAssembler)delegate).assemble(applicationContext);
          }

        runApplicationAssemblers(applicationNad);
        executorService.execute(() ->
          {
            onStageCreated(applicationContext);
            messageBus.ifPresent(mb -> mb.publish(new PowerOnEvent()));
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
    protected void onClosing()
      {
        messageBus.ifPresent(mb -> mb.publish(new PowerOffEvent()));
        applicationContext.close();
      }

    /***********************************************************************************************************************************************************
     *
     **********************************************************************************************************************************************************/
    private void runApplicationAssemblers (@Nonnull final NodeAndDelegate applicationNad)
      {
        Objects.requireNonNull(applicationContext, "applicationContext is null");
        applicationContext.getBeansOfType(ApplicationPresentationAssembler.class).values().forEach(a -> a.assemble(applicationNad.getDelegate()));
      }

    /***********************************************************************************************************************************************************
     * Logs all the system properties.
     **********************************************************************************************************************************************************/
    private void logProperties()
      {
        for (final var e : new TreeMap<>(System.getProperties()).entrySet())
          {
            log.debug("{}: {}", e.getKey(), e.getValue());
          }
      }
  }
