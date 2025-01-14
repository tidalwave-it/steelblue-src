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
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import it.tidalwave.ui.javafx.impl.DefaultJavaFXPanelGroupControl;
import it.tidalwave.ui.javafx.impl.util.JavaFXSafeComponentBuilder;
import it.tidalwave.ui.javafx.spi.AbstractJavaFXSpringApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import it.tidalwave.util.PreferencesHandler;
import it.tidalwave.ui.core.annotation.EnableMessageBus;
import it.tidalwave.messagebus.MessageBus;
import it.tidalwave.messagebus.spi.SimpleMessageBus;

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
    static class Beans
      {
        @Bean
        public ThreadPoolTaskExecutor javafxBinderExecutor()
          {
            return JavaFXSafeComponentBuilder.getExecutor();
          }

        @Bean
        public PreferencesHandler preferencesHandler()
          {
            return PreferencesHandler.getInstance();
          }

        @Bean
        public JavaFXToolBarControl toolBarControl()
          {
            return JavaFXSafeComponentBuilder.getToolBarControl();
          }

        @Bean
        public JavaFXMenuBarControl menuBarControl()
          {
            return JavaFXSafeComponentBuilder.getMenuBarControl();
          }

        @Bean
        public JavaFXPanelGroupControl panelGroupControl (@Nonnull final BeanFactory beanFactory)
          {
            return new DefaultJavaFXPanelGroupControl(beanFactory);
          }
      }

    static class MessageBusBeans
      {
        @Bean(name = APPLICATION_MESSAGE_BUS_BEAN_NAME)
        public MessageBus applicationMessageBus()
          {
            final var executor = new ThreadPoolTaskExecutor();
            executor.setWaitForTasksToCompleteOnShutdown(false);
            executor.setThreadNamePrefix("messageBus-");
            executor.setCorePoolSize(10);
            executor.setMaxPoolSize(10);
            executor.setQueueCapacity(10);
            executor.afterPropertiesSet();
            return new SimpleMessageBus(executor);
          }
      }

    public static final String APPLICATION_MESSAGE_BUS_BEAN_NAME = "applicationMessageBus";

    // Don't use Slf4j and its static logger - give Main a chance to initialize things
    private final Logger log = LoggerFactory.getLogger(JavaFXSpringAnnotationApplication.class);

    @Override @Nonnull
    protected ConfigurableApplicationContext createApplicationContext()
      {
        final var mainClass = getClass();
        final var componentClasses = new ArrayList<>(List.of(mainClass, Beans.class));

        if (getClass().isAnnotationPresent(EnableMessageBus.class))
          {
            log.info("Detected @{}, enabling message bus", EnableMessageBus.class.getSimpleName());
            componentClasses.add(MessageBusBeans.class);
          }

        log.info("Scanning beans from {}", mainClass);
        return new AnnotationConfigApplicationContext(componentClasses.toArray(new Class<?>[0]));
      }
  }
