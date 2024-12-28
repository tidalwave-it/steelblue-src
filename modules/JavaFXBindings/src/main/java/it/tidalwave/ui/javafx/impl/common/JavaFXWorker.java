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
package it.tidalwave.ui.javafx.impl.common;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javafx.collections.ObservableList;
import javafx.application.Platform;
import it.tidalwave.util.annotation.VisibleForTesting;
import it.tidalwave.role.ui.PresentationModel;
import it.tidalwave.ui.javafx.impl.util.Logging;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import static java.util.Collections.emptyList;
import static javafx.collections.FXCollections.*;
import static it.tidalwave.role.SimpleComposite._SimpleComposite_;
import static it.tidalwave.ui.javafx.impl.util.Logging.INDENT;

/***************************************************************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **************************************************************************************************************************************************************/
@UtilityClass @Slf4j
public class JavaFXWorker
  {
    public static <T> void run (@Nonnull final Executor executor,
                                @Nonnull Supplier<T> backgroundSupplier,
                                @Nonnull Consumer<T> javaFxFinalizer)
      {
        try
          {
            executor.execute(() ->
              {
                final var value = backgroundSupplier.get();
                Platform.runLater(() -> javaFxFinalizer.accept(value));
              });
          }
        catch (RejectedExecutionException e)
          {
            log.error("Background task failed: {}", e.getMessage());
          }
      }

    @Nonnull
    public static ObservableList<PresentationModel> childrenPm (@Nonnull final PresentationModel pm)
      {
        return childrenPm(pm, 0);
      }

    @Nonnull
    public static ObservableList<PresentationModel> childrenPm (@Nonnull final PresentationModel pm,
                                                                @Nonnegative int depth)
      {
        final var indent = INDENT.substring(0, depth * 8);
        final var composite = pm.maybeAs(_SimpleComposite_);
        composite.ifPresent(c -> Logging.logObject(indent, composite));
        final List<PresentationModel> items = composite.map(c -> c.findChildren().results()).orElse(emptyList());
        final var badItems = extractBadItems(items);

        if (!badItems.isEmpty()) // defensive
          {
            log.error("Child object are not PresentationModel: (only 10 are shown)");
            log.error("This happens when the PresentationModel doesn't have its own Composite role that decorates the" +
                      " owner entity Composite - see SimpleCompositePresentable for instance.");
            badItems.stream().limit(10).forEach(item -> log.error("    {}", item));
            return emptyObservableList();
          }

        Logging.logObjects(indent, items);
        return observableArrayList(items);
      }

    @Nonnull
    @VisibleForTesting static List<Object> extractBadItems (@Nonnull final List<PresentationModel> items)
      {
        final List<Object> badItems = new ArrayList<>(items);
        badItems.removeIf(item -> item instanceof PresentationModel);
        return badItems;
//        return items.stream()
//                    .map(item -> (Object)item)
//                    .filter(item -> !(item instanceof PresentationModel))
//                    .peek(item -> log.error(">>>> {}", item))
//                    .collect(toList());
      }
  }

