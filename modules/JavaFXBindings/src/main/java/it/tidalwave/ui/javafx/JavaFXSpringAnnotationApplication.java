/*
 * *********************************************************************************************************************
 *
 * SteelBlue: DCI User Interfaces
 * http://tidalwave.it/projects/steelblue
 *
 * Copyright (C) 2015 - 2024 by Tidalwave s.a.s. (http://tidalwave.it)
 *
 * *********************************************************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * *********************************************************************************************************************
 *
 * git clone https://bitbucket.org/tidalwave/steelblue-src
 * git clone https://github.com/tidalwave-it/steelblue-src
 *
 * *********************************************************************************************************************
 */
package it.tidalwave.ui.javafx;

import javax.annotation.Nonnull;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import it.tidalwave.util.PreferencesHandler;
import it.tidalwave.role.impl.DefaultContextManager;
import it.tidalwave.role.spring.spi.AnnotationSpringSystemRoleFactory;
import it.tidalwave.role.ui.javafx.StackPaneSelector;

/***********************************************************************************************************************
 *
 * A base class for JavaFX applications with use Spring annotation scanning.
 *
 * @author  Fabrizio Giudici
 * @since   1.1-ALPHA-4
 *
 **********************************************************************************************************************/
@Configuration
public class JavaFXSpringAnnotationApplication extends AbstractJavaFXSpringApplication
  {
    // Don't use Slf4j and its static logger - give Main a chance to initialize things
    private final Logger log = LoggerFactory.getLogger(JavaFXSpringApplication.class);

    @Override @Nonnull
    protected ConfigurableApplicationContext createApplicationContext()
      {
        final var mainClass = getClass();
        log.info("Scanning beans from {}", mainClass);
        return new AnnotationConfigApplicationContext(mainClass);
      }

    @Bean(name = "javafxBinderExecutor")
    public ThreadPoolTaskExecutor getJavaFXSafeProxyCreator()
      {
        return JavaFXSafeProxyCreator.getExecutor();
      }

    @Bean(name = "stackPaneSelector")
    public StackPaneSelector getStackPaneSelector()
      {
        return new StackPaneSelector();
      }

    @Bean(name = "preferencesHandler")
    public PreferencesHandler getPreferencesHandler()
      {
        return PreferencesHandler.getInstance();
      }

    @Bean(name = "roleManager")
    public AnnotationSpringSystemRoleFactory getAnnotationSpringSystemRoleFactory()
      {
        return new AnnotationSpringSystemRoleFactory();
      }

    @Bean(name = "contextManager")
    public DefaultContextManager getDefaultContextManager()
      {
        return new DefaultContextManager();
      }
  }
