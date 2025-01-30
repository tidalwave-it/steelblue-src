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
import jakarta.annotation.Nullable;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Skin;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import it.tidalwave.ui.core.role.Displayable;
import it.tidalwave.ui.core.role.PresentationModel;
import it.tidalwave.ui.core.role.Selectable;
import it.tidalwave.ui.javafx.impl.common.DefaultCellBinder;
import it.tidalwave.ui.javafx.impl.list.ListViewBindingsTest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.BeforeMethod;
import static it.tidalwave.ui.core.role.spi.PresentationModelCollectors.toCompositePresentationModel;
import static java.util.stream.Collectors.*;
import static org.mockito.Mockito.*;

/***************************************************************************************************************************************************************
 *
 * @author Fabrizio Giudici
 *
 **************************************************************************************************************************************************************/
@RequiredArgsConstructor @Slf4j
public abstract class BindingTestSupport<T extends Node> extends JavaFXTestSupport
  {
    /***********************************************************************************************************************************************************
     *
     **********************************************************************************************************************************************************/
    @RequiredArgsConstructor
    protected static class Datum
      {
        @Nonnull
        private final String name;

        @Override @Nonnull
        public String toString()
          {
            return name;
          }
      }

    @Nonnull
    private final Supplier<T> supplier;

    protected T control;

    protected ExecutorService executor;

    protected Runnable callBack;

    protected PresentationModel pm;

    private StackPane stackPane;

    /***********************************************************************************************************************************************************
     * Prepares the {@link Stage for the test}.
     **********************************************************************************************************************************************************/
    @Override
    public void start (@Nonnull final Stage stage)
      {
        control = supplier.get();
        stage.setScene(new Scene(stackPane = new StackPane(), 400.0, 400));
        stage.show();
        executor = Executors.newSingleThreadExecutor();
        runSafelyAndWait("start", executor, () -> {}); // wait for tasks completed
        callBack = mock(Runnable.class);
      }

    /***********************************************************************************************************************************************************
     *
     **********************************************************************************************************************************************************/
    protected abstract void setup (@Nonnull final DefaultCellBinder cellBinder);

    /**********************************************************************************************************************************************************/
    @BeforeMethod(groups = "display")
    public void setup()
      {
        log.info("RESETTING FOR NEXT TEST...");
        pm = createAggregatePresentationModel();
        runSafelyAndWait("setup", () ->
          {
            stackPane.getChildren().setAll(control = supplier.get());
            final var cellBinder = new DefaultCellBinder(executor);
            setup(cellBinder);
          });
      }

    /***********************************************************************************************************************************************************
     *
     **********************************************************************************************************************************************************/
    @Nonnull
    protected static PresentationModel createAggregatePresentationModel()
      {
        return IntStream.rangeClosed(1, 10)
                        .mapToObj(ListViewBindingsTest::createPresentationModel)
                        .collect(toCompositePresentationModel());
      }

    /***********************************************************************************************************************************************************
     *
     **********************************************************************************************************************************************************/
    @Nonnull
    protected static PresentationModel createPresentationModel (final int i)
      {
        final var displayable = Displayable.of("Item #" + i);
        final var selectable = mock(Selectable.class);
        doAnswer(invocation -> { log.info("Invoked selectable #{}", i); return null; }).when(selectable).select();
        return PresentationModel.of(new Datum("Datum #" + i), List.of(displayable, selectable));
      }


    /***********************************************************************************************************************************************************
     * {@return a list of strings containing the texts from the cells of skin.
     * @param     skin    the skin
     **********************************************************************************************************************************************************/
    @Nonnull
    protected static List<String> findTextsFrom (@Nonnull final Node node, @Nonnull final Predicate<String> filter)
      {
        if (node instanceof Parent)
          {
            return ((Parent)node).getChildrenUnmodifiable().stream().flatMap(c -> findTextsFrom(c, filter).stream()).collect(toList());
          }

        return (node instanceof Text ? List.of(((Text)node).getText()) : List.<String>of()).stream().filter(filter).collect(toList());
//        return node.lookupAll(selector)
//                   .stream()
//                   .map(c -> (Cell<?>)c)
//                   .filter(c -> c.getItem() != null)
//                   .map(Labeled::getText)
//                   .collect(toList());
      }

    /***********************************************************************************************************************************************************
     * {@return a list of strings containing the texts from the cells of skin.
     * @param     skin    the skin
     **********************************************************************************************************************************************************/
    @Nonnull
    protected static List<String> findTextsFrom (@Nullable final Skin<?> skin, @Nonnull final Predicate<String> filter)
      {
        if (skin == null)
          {
            return List.of();
          }

        return ((SkinBase<?>)skin).getChildren()
                                  .stream()
                                  .flatMap(c -> findTextsFrom(c, filter).stream())
                                  .collect(toList());
      }
  }
