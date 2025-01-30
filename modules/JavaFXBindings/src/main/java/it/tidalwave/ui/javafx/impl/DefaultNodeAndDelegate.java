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
import java.io.IOException;
import java.io.UncheckedIOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import it.tidalwave.ui.javafx.NodeAndDelegate;
import it.tidalwave.ui.javafx.impl.util.JavaFXSafeProxy;
import it.tidalwave.ui.javafx.spi.AbstractJavaFXSpringApplication;
import org.slf4j.LoggerFactory;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import static it.tidalwave.ui.javafx.impl.DefaultJavaFXBinder.enforceFxApplicationThread;
import static it.tidalwave.ui.javafx.impl.util.JavaFXSafeRunner.runSafelyAndWait;
import static it.tidalwave.util.ReflectionUtils.*;

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
    @Nonnull
    private final Node node;

    @Nonnull
    private final T delegate;

    /***********************************************************************************************************************************************************
     * Creates a {@link NodeAndDelegate} for the given presentation class. The FXML resource name is inferred by default, For instance, is the class is named
     * {@code JavaFXFooBarPresentation}, the resource name is {@code FooBar.fxml} and searched in the same packages as the class.
     * @param   presentationClass   the class of the presentation for which the resources must be created.
     * @since   1.0-ALPHA-13
     * @see                         #of(java.lang.Class, java.lang.String)
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
     * @param   presentationClass   the class of the presentation for which the resources must be created.
     * @param   fxmlResourcePath    the path of the FXML resource
     **********************************************************************************************************************************************************/
    @Nonnull
    public static <T> NodeAndDelegate<T> of (@Nonnull final Class<T> presentationClass, @Nonnull final String fxmlResourcePath)
      {
        try
          {
            log.debug("of({}, {})", presentationClass, fxmlResourcePath);
            return runSafelyAndWait(() -> load(presentationClass, fxmlResourcePath));
          }
        catch (RuntimeException e)
          {
            throw e;
          }
        catch (Exception e)
          {
            throw new RuntimeException(e);
          }
      }

    /***********************************************************************************************************************************************************
     *
     **********************************************************************************************************************************************************/
    @Nonnull
    private static <T> NodeAndDelegate<T> load (@Nonnull final Class<T> clazz, @Nonnull final String resource)
      {
        try
          {
            enforceFxApplicationThread();
            final var log = LoggerFactory.getLogger(NodeAndDelegate.class);
            log.debug("NodeAndDelegate({}, {})", clazz, resource);
            final var loader = new FXMLLoader(clazz.getResource(resource), null, null, type -> instantiateWithDependencies(type, AbstractJavaFXSpringApplication.getBeans()));
            final Node node = loader.load();
            final T jfxController = loader.getController();
            injectDependencies(jfxController, AbstractJavaFXSpringApplication.getBeans());
            final var interfaces = jfxController.getClass().getInterfaces();

            if (interfaces.length == 0)
              {
                log.warn("{} has no interface: not creating safe proxy", jfxController.getClass());
              }

            final T delegate = (interfaces.length > 0) ? JavaFXSafeProxy.of(jfxController, interfaces) : jfxController;
            log.debug(">>>> load({}, {}) completed", clazz, resource);
            return new DefaultNodeAndDelegate<>(node, delegate);
          }
        catch (IOException e)
          {
            throw new UncheckedIOException(e);
          }
        catch (IllegalStateException e)
          {
            final var message = String.format("ERROR: Cannot find resource: %s/%s", clazz.getPackageName().replace('.','/'), resource);
            log.error("ERROR: Cannot find resource: {}", message);
            throw new IllegalStateException(message, e);
          }
      }
  }
