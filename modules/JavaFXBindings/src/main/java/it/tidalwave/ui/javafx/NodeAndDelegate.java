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
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.application.Platform;
import it.tidalwave.util.ReflectionUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import static lombok.AccessLevel.PRIVATE;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
@RequiredArgsConstructor(access = PRIVATE) @Getter @Slf4j
public final class NodeAndDelegate<T>
  {
    @Nonnull
    private final Node node;

    @Nonnull
    private final T delegate;

    @Nonnull
    public static <T> NodeAndDelegate<T> load (@Nonnull final Class<T> clazz, @Nonnull final String resource)
            throws IOException
      {
        log.debug("NodeAndDelegate({}, {})", clazz, resource);
        assert Platform.isFxApplicationThread() : "Not in JavaFX UI Thread";
        final var loader = new FXMLLoader(clazz.getResource(resource), null, null,
                                          type -> ReflectionUtils.instantiateWithDependencies(type, JavaFXSafeProxyCreator.BEANS));
        final Node node = loader.load();
        final T jfxController = loader.getController();
        ReflectionUtils.injectDependencies(jfxController, JavaFXSafeProxyCreator.BEANS);
        final var interfaces = jfxController.getClass().getInterfaces();

        if (interfaces.length == 0)
          {
            log.warn("{} has no interface: not creating safe proxy", jfxController.getClass());
            log.debug(">>>> load({}, {}) completed", clazz, resource);
            return new NodeAndDelegate<>(node, jfxController);
          }
        else
          {
            final var interfaceClass = (Class<T>)interfaces[0]; // FIXME
            final var safeDelegate = JavaFXSafeProxyCreator.createSafeProxy(jfxController, interfaceClass);
            log.debug(">>>> load({}, {}) completed", clazz, resource);
            return new NodeAndDelegate<>(node, safeDelegate);
          }
      }
  }
