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
package it.tidalwave.ui.javafx.spi;

import javax.annotation.Nonnull;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;
import it.tidalwave.message.PowerOffEvent;
import it.tidalwave.message.PowerOnEvent;
import it.tidalwave.ui.javafx.NodeAndDelegate;
import it.tidalwave.messagebus.MessageBus;
import lombok.RequiredArgsConstructor;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static it.tidalwave.ui.javafx.JavaFXSpringAnnotationApplication.APPLICATION_MESSAGE_BUS_BEAN_NAME;
import static org.mockito.Mockito.*;

/***************************************************************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **************************************************************************************************************************************************************/
public class AbstractJavaFXSpringApplicationTest
  {
    @RequiredArgsConstructor
    static class UnderTest extends AbstractJavaFXSpringApplication
      {
        @Nonnull
        private final ConfigurableApplicationContext mockApplicationContext;

        @Override @Nonnull
        protected ConfigurableApplicationContext createApplicationContext()
          {
            return mockApplicationContext;
          }
      }

    static class MockPresentationDelegate
      {
      }

    private UnderTest underTest;

    private ConfigurableApplicationContext applicationContext;

    private ConfigurableListableBeanFactory beanFactory;

    private MessageBus messageBus;

    private NodeAndDelegate<MockPresentationDelegate> nad;

    private ExecutorService executorService;

    /**********************************************************************************************************************************************************/
    @BeforeMethod
    public void setup()
      {
        beanFactory = mock(ConfigurableListableBeanFactory.class);
        applicationContext = mock(ConfigurableApplicationContext.class);
        when(applicationContext.getBeanFactory()).thenReturn(beanFactory);
        messageBus = mock(MessageBus.class);
        when(beanFactory.containsBean(eq(APPLICATION_MESSAGE_BUS_BEAN_NAME))).thenReturn(false);
        when(beanFactory.getBean(eq(APPLICATION_MESSAGE_BUS_BEAN_NAME), eq(MessageBus.class))).thenThrow(new NoSuchBeanDefinitionException(MessageBus.class));
        nad = mock(NodeAndDelegate.class);
        when(nad.getDelegate()).thenReturn(new MockPresentationDelegate());
        underTest = new UnderTest(applicationContext);
        executorService = underTest.getExecutorService();
      }

    /**********************************************************************************************************************************************************/
    @Test
    public void must_not_fire_PowerOnEvent_when_MessageBus_not_present()
            throws InterruptedException
      {
        // given
        underTest.initializeInBackground();
        // when
        underTest.onStageCreated2(nad);
        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.SECONDS);
        // then
        verifyNoInteractions(messageBus);
      }

    /**********************************************************************************************************************************************************/
    @Test
    public void must_fire_PowerOnEvent_when_MessageBus_present()
            throws InterruptedException
      {
        // given
        when(beanFactory.containsBean(eq(APPLICATION_MESSAGE_BUS_BEAN_NAME))).thenReturn(true);
        when(beanFactory.getBean(eq(APPLICATION_MESSAGE_BUS_BEAN_NAME), eq(MessageBus.class))).thenReturn(messageBus);
        underTest.initializeInBackground();
        // when
        underTest.onStageCreated2(nad);
        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.SECONDS);
        // then
        verify(messageBus).publish(any(PowerOnEvent.class));
        verifyNoMoreInteractions(messageBus);
      }

    /**********************************************************************************************************************************************************/
    @Test
    public void must_not_fire_PowerOffEvent_when_MessageBus_not_present()
      {
        // given
        underTest.initializeInBackground();
        // when
        underTest.onClosing();
        // then
        verifyNoInteractions(messageBus);
      }

    /**********************************************************************************************************************************************************/
    @Test
    public void must_fire_PowerOffEvent_when_MessageBus_present()
      {
        // given
        when(beanFactory.containsBean(eq(APPLICATION_MESSAGE_BUS_BEAN_NAME))).thenReturn(true);
        when(beanFactory.getBean(eq(APPLICATION_MESSAGE_BUS_BEAN_NAME), eq(MessageBus.class))).thenReturn(messageBus);
        underTest.initializeInBackground();
        // when
        underTest.onClosing();
        // then
        verify(messageBus).publish(any(PowerOffEvent.class));
        verifyNoMoreInteractions(messageBus);
      }
  }
