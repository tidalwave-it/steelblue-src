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
package it.tidalwave.ui.javafx;
import jakarta.annotation.Nonnull;
import jakarta.inject.Named;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import it.tidalwave.ui.core.annotation.EnableMessageBus;
import it.tidalwave.ui.javafx.impl.AbstractJavaFXSpringApplication;
import it.tidalwave.ui.javafx.impl.DefaultJavaFXPanelGroupControl;
import it.tidalwave.ui.util.UserActionFactory;
import it.tidalwave.ui.util.impl.DefaultUserActionFactory;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.annotation.AnnotationDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import it.tidalwave.util.PreferencesHandler;
import it.tidalwave.messagebus.MessageBus;
import it.tidalwave.messagebus.spi.SimpleMessageBus;
import lombok.Setter;
/***************************************************************************************************************************************************************
 *
 * A base class for JavaFX applications with use Spring annotation scanning.
 *
 * @author  Fabrizio Giudici
 * @since   1.1-ALPHA-4
 *
 **************************************************************************************************************************************************************/
public class JavaFXSpringAnnotationApplication extends AbstractJavaFXSpringApplication
  {
    /***********************************************************************************************************************************************************
     * Base bean factory.
     **********************************************************************************************************************************************************/
    private static class Beans
      {
        @Setter
        private static AbstractJavaFXSpringApplication application;
        @Bean
        public ThreadPoolTaskExecutor javafxBinderExecutor()
          {
            return application.getExecutor();
          }
        @Bean
        public PreferencesHandler preferencesHandler()
          {
            return PreferencesHandler.getInstance();
          }
        @Bean
        public JavaFXToolBarControl toolBarControl()
          {
            return application.getToolBarControl();
          }
        @Bean
        public JavaFXMenuBarControl menuBarControl()
          {
            return application.getMenuBarControl();
          }
        @Bean
        public JavaFXPanelGroupControl panelGroupControl (@Nonnull final BeanFactory beanFactory)
          {
            return new DefaultJavaFXPanelGroupControl(beanFactory);
          }
        @Bean
        public UserActionFactory userActionFactory (@Nonnull @Named(APPLICATION_MESSAGE_BUS_BEAN_NAME) final Optional<MessageBus> messageBus)
          {
            return new DefaultUserActionFactory(messageBus);
          }
      }
    /***********************************************************************************************************************************************************
     * Auxiliary bean factory for message bus.
     **********************************************************************************************************************************************************/
    private static class MessageBusBeans
      {
        @Bean(name = MESSAGE_BUS_EXECUTOR_BEAN_NAME)
        public ThreadPoolTaskExecutor applicationMessageBusExecutor()
          {
            final var executor = new ThreadPoolTaskExecutor();
            executor.setWaitForTasksToCompleteOnShutdown(false);
            executor.setThreadNamePrefix("messageBus-");
            executor.setCorePoolSize(10);
            executor.setMaxPoolSize(10);
            executor.setQueueCapacity(10);
            return executor;
          }
        @Bean(name = APPLICATION_MESSAGE_BUS_BEAN_NAME)
        public MessageBus applicationMessageBus (@Named(MESSAGE_BUS_EXECUTOR_BEAN_NAME) final ThreadPoolTaskExecutor executor)
          {
            return new SimpleMessageBus(executor);
          }
      }
    public static final String MESSAGE_BUS_EXECUTOR_BEAN_NAME = "applicationMessageBusExecutor";
    // Don't use Slf4j and its static logger - give Main a chance to initialize things
    private final Logger log = LoggerFactory.getLogger(JavaFXSpringAnnotationApplication.class);
    /***********************************************************************************************************************************************************
     * {@inheritDoc}
     **********************************************************************************************************************************************************/
    @Override @Nonnull
    protected ConfigurableApplicationContext createApplicationContext()
      {
        Beans.setApplication(this);
        // Don't pass getClass() to the ApplicationContext or it will be instantiated — for the second time, since JavaFX launch() already did it.
        // Instead, create a synthetic class carrying on all its annotations, so the needed features of Spring will be activated.
        final var componentClasses = new ArrayList<>(List.of(createSyntheticClassWithAnnotations(getClass()), Beans.class));
        if (getClass().isAnnotationPresent(EnableMessageBus.class))
          {
            log.info("Detected @{}, enabling message bus", EnableMessageBus.class.getSimpleName());
            componentClasses.add(MessageBusBeans.class);
          }
        return new AnnotationConfigApplicationContext(componentClasses.toArray(Class<?>[]::new));
      }
    /***********************************************************************************************************************************************************
     *
     **********************************************************************************************************************************************************/
    @Nonnull
    public static Class<?> createSyntheticClassWithAnnotations (@Nonnull final Class<?> clazz)
      {
        var fluent = new ByteBuddy().subclass(Object.class).name(clazz.getPackageName() + ".AnnotationCarrier");
        for (final var annotation : clazz.getAnnotations())
          {
            fluent = fluent.annotateType(AnnotationDescription.ForLoadedAnnotation.of(annotation));
          }
        return fluent.make().load(clazz.getClassLoader()).getLoaded();
      }
  }
