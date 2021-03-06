/*
 * #%L
 * *********************************************************************************************************************
 *
 * SteelBlue
 * http://steelblue.tidalwave.it - git clone git@bitbucket.org:tidalwave/steelblue-src.git
 * %%
 * Copyright (C) 2015 - 2021 Tidalwave s.a.s. (http://tidalwave.it)
 * %%
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
 *
 *
 * *********************************************************************************************************************
 * #L%
 */
package it.tidalwave.role.ui.javafx.impl.common;

import javax.annotation.Nonnull;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.Effect;
import javafx.stage.Window;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
@RequiredArgsConstructor
public abstract class DelegateSupport
  {
    @Nonnull
    protected final Executor executor;

    @Setter
    protected Window mainWindow;

    /*******************************************************************************************************************
     *
     * Runs the given (@link Callable} while disabling the given {@link Window}.
     *
     * @param  window    the {@code Window} to disable
     * @param  callable  the (@code Callable} to run
     * @return           the (@code Callable} result
     *
     ******************************************************************************************************************/
    protected <T> T runWhileDisabling (@Nonnull final Window window, @Nonnull final Callable<T> callable)
      {
        final Parent root = window.getScene().getRoot();
        final Effect effect = root.getEffect();
        final boolean disabled = root.isDisable();

        try
          {
            root.setDisable(true);
            root.setEffect(createDisablingEffect());
            return callable.call();
          }
        catch (Exception e)
          {
            throw new RuntimeException(e);
          }
        finally
          {
            root.setEffect(effect);
            root.setDisable(disabled);
          }
      }

    /*******************************************************************************************************************
     *
     * TODO: delegate to a provider
     *
     ******************************************************************************************************************/
    @Nonnull
    private BoxBlur createDisablingEffect()
      {
        final BoxBlur bb = new BoxBlur();
        bb.setWidth(5);
        bb.setHeight(5);
        bb.setIterations(3);
        return bb;
      }

    /*******************************************************************************************************************
     *
     * TODO: delegate to a provider
     *
     ******************************************************************************************************************/
    public static boolean isOSX()
      {
        return System.getProperty("os.name").contains("OS X");
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    protected void assertIsFxApplicationThread()
      {
        if (!Platform.isFxApplicationThread())
          {
            throw new AssertionError("Must run in the JavaFX Application Thread");
          }
      }
  }
