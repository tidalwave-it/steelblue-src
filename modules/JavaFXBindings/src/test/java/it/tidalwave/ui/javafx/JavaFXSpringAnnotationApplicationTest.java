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

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import it.tidalwave.util.PreferencesHandler;
import it.tidalwave.ui.core.MenuBarModel;
import it.tidalwave.ui.core.ToolBarModel;
import it.tidalwave.ui.core.annotation.EnableMessageBus;
import it.tidalwave.messagebus.MessageBus;
import org.testng.annotations.Test;
import static it.tidalwave.ui.javafx.JavaFXSpringAnnotationApplication.APPLICATION_MESSAGE_BUS_BEAN_NAME;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

/***************************************************************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **************************************************************************************************************************************************************/
public class JavaFXSpringAnnotationApplicationTest
  {
    static class UnderTest extends JavaFXSpringAnnotationApplication
      {
      }

    @EnableMessageBus
    static class UnderTestWithEnableMessageBus extends UnderTest
      {
      }

    /**********************************************************************************************************************************************************/
    @Test
    public void test_ApplicationContext()
      {
        // given
        final var underTest = new UnderTest();
        PreferencesHandler.setAppName("test");
        // when
        final var applicationContext = underTest.createApplicationContext();
        // then
        assertThat(applicationContext.getBean("javafxBinderExecutor", ThreadPoolTaskExecutor.class), notNullValue());
        assertThat(applicationContext.getBean("stackPaneSelector", StackPaneSelector.class), notNullValue());
        assertThat(applicationContext.getBean("preferencesHandler", PreferencesHandler.class), notNullValue());
        assertThat(applicationContext.getBean("toolBarModel", ToolBarModel.class), notNullValue());
        assertThat(applicationContext.getBean("menuBarModel", MenuBarModel.class), notNullValue());
        assertThat(applicationContext.containsBean(APPLICATION_MESSAGE_BUS_BEAN_NAME), is(false));
      }

    /**********************************************************************************************************************************************************/
    @Test
    public void test_ApplicationContext_with_EnableMessageBus()
      {
        // given
        final var underTest = new UnderTestWithEnableMessageBus();
        PreferencesHandler.setAppName("test");
        // when
        final var applicationContext = underTest.createApplicationContext();
        // then
        assertThat(applicationContext.getBean("javafxBinderExecutor", ThreadPoolTaskExecutor.class), notNullValue());
        assertThat(applicationContext.getBean("stackPaneSelector", StackPaneSelector.class), notNullValue());
        assertThat(applicationContext.getBean("preferencesHandler", PreferencesHandler.class), notNullValue());
        assertThat(applicationContext.getBean("toolBarModel", ToolBarModel.class), notNullValue());
        assertThat(applicationContext.getBean("menuBarModel", MenuBarModel.class), notNullValue());
        assertThat(applicationContext.getBean(APPLICATION_MESSAGE_BUS_BEAN_NAME, MessageBus.class), notNullValue());
      }
  }
