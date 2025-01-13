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
package it.tidalwave.ui.javafx.spi;

import jakarta.annotation.Nonnull;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ConfigurableApplicationContext;
import it.tidalwave.ui.core.annotation.Assemble;
import it.tidalwave.ui.core.annotation.PresentationAssembler;
import it.tidalwave.ui.core.message.PowerOffEvent;
import it.tidalwave.ui.core.message.PowerOnEvent;
import it.tidalwave.ui.javafx.JavaFXBinder;
import it.tidalwave.ui.javafx.JavaFXMenuBarControl;
import it.tidalwave.ui.javafx.JavaFXPanelGroupControl;
import it.tidalwave.ui.javafx.JavaFXToolBarControl;
import it.tidalwave.ui.javafx.NodeAndDelegate;
import it.tidalwave.util.PreferencesHandler;
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
    static class Fixture extends AbstractJavaFXSpringApplication
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

    private Fixture underTest;

    private ConfigurableApplicationContext applicationContext;

    private MessageBus messageBus;

    private NodeAndDelegate<MockPresentationDelegate> nad;

    private ExecutorService executorService;

    /**********************************************************************************************************************************************************/
    @BeforeMethod @SuppressWarnings("unchecked")
    public void setup()
      {
        applicationContext = mock(ConfigurableApplicationContext.class);
        messageBus = mock(MessageBus.class);
        when(applicationContext.containsBean(APPLICATION_MESSAGE_BUS_BEAN_NAME)).thenReturn(false);
        when(applicationContext.getBean(eq(APPLICATION_MESSAGE_BUS_BEAN_NAME), eq(MessageBus.class))).thenThrow(new NoSuchBeanDefinitionException(MessageBus.class));
        nad = mock(NodeAndDelegate.class);
        when(nad.getDelegate()).thenReturn(new MockPresentationDelegate());
        underTest = new Fixture(applicationContext);
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
        when(applicationContext.containsBean(APPLICATION_MESSAGE_BUS_BEAN_NAME)).thenReturn(true);
        when(applicationContext.getBean(eq(APPLICATION_MESSAGE_BUS_BEAN_NAME), eq(MessageBus.class))).thenReturn(messageBus);
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
        when(applicationContext.containsBean(APPLICATION_MESSAGE_BUS_BEAN_NAME)).thenReturn(true);
        when(applicationContext.getBean(eq(APPLICATION_MESSAGE_BUS_BEAN_NAME), eq(MessageBus.class))).thenReturn(messageBus);
        underTest.initializeInBackground();
        // when
        underTest.onClosing();
        // then
        verify(messageBus).publish(any(PowerOffEvent.class));
        verifyNoMoreInteractions(messageBus);
      }

    /**********************************************************************************************************************************************************/
    static class Bean1 {}
    static class Bean2 {}

    @PresentationAssembler
    static class PresentationDelegateAssembler extends MockPresentationDelegate
      {
        @Assemble
        public void assemble(@Nonnull final JavaFXBinder javaFXBinder,
                             @Nonnull final JavaFXToolBarControl javaFXToolBarControl,
                             @Nonnull final JavaFXMenuBarControl menuBarControl,
                             @Nonnull final JavaFXPanelGroupControl javaFXPanelGroupControl,
                             @Nonnull final Bean1 bean1,
                             @Nonnull final Bean2 bean2)
          {
            // body not needed for tests
          }
      }

    @PresentationAssembler
    static class SpringComponentAssembler
      {
        @Assemble
        public void assemble(@Nonnull final JavaFXBinder javaFXBinder,
                             @Nonnull final JavaFXToolBarControl javaFXToolBarControl,
                             @Nonnull final JavaFXMenuBarControl menuBarControl,
                             @Nonnull final JavaFXPanelGroupControl javaFXPanelGroupControl,
                             @Nonnull final Bean1 bean1,
                             @Nonnull final Bean2 bean2)
          {
            // body not needed for tests
          }
      }

    @Test
    public void musts_call_assembler_methods()
      {
        final var presentationDelegateAssembler = spy(new PresentationDelegateAssembler());
        final var springComponentAssembler = spy(new SpringComponentAssembler());
        when(applicationContext.getBean(Bean1.class)).thenReturn(new Bean1());
        when(applicationContext.getBean(Bean2.class)).thenReturn(new Bean2());
        when(applicationContext.getBean(JavaFXPanelGroupControl.class)).thenReturn(mock(JavaFXPanelGroupControl.class));
        when(applicationContext.getBeansWithAnnotation(PresentationAssembler.class)).thenReturn(Map.of("foo", springComponentAssembler));
        when(nad.getDelegate()).thenReturn(presentationDelegateAssembler);
        PreferencesHandler.setAppName("test-" + getClass().getName());
        underTest.initializeInBackground();
        // when
        underTest.onStageCreated2(nad);
        // then
        verify(presentationDelegateAssembler).assemble(any(JavaFXBinder.class),
                                                           any(JavaFXToolBarControl.class),
                                                           any(JavaFXMenuBarControl.class),
                                                           any(JavaFXPanelGroupControl.class),
                                                           any(Bean1.class),
                                                           any(Bean2.class));
        verify(springComponentAssembler).assemble(any(JavaFXBinder.class),
                                                  any(JavaFXToolBarControl.class),
                                                  any(JavaFXMenuBarControl.class),
                                                  any(JavaFXPanelGroupControl.class),
                                                  any(Bean1.class),
                                                  any(Bean2.class));
      }
  }
