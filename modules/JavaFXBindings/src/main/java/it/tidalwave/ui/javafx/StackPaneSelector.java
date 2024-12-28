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

import javax.annotation.Nonnull;
import java.util.WeakHashMap;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import lombok.extern.slf4j.Slf4j;

/***************************************************************************************************************************************************************
 *
 * A facility that is used to manage areas in the UI where multiple contents should appear in a mutually exclusive
 * way.
 *
 * @author Fabrizio Giudici
 *
 **************************************************************************************************************************************************************/
@Slf4j
public class StackPaneSelector // FIXME: rename, introduce interface
  {
    private final WeakHashMap<String, StackPane> stackPaneMapByArea = new WeakHashMap<>();

    /***********************************************************************************************************************************************************
     * Register a new area associated to a {@link StackPane}.
     *
     * @param   area        the area name
     * @param   stackPane   the {@code StackPane}
     **********************************************************************************************************************************************************/
    public void registerArea (@Nonnull final String area, @Nonnull final StackPane stackPane)
      {
        log.debug("registerArea({}, {})", area, stackPane);
        stackPaneMapByArea.put(area, stackPane);
      }

    /***********************************************************************************************************************************************************
     * Add a {@link Node} to a previously registered area.
     *
     * @param   area        the area name
     * @param   node        the {@code Node}
     **********************************************************************************************************************************************************/
    public void add (@Nonnull final String area, @Nonnull final Node node)
      {
        node.setVisible(false);
        findStackPaneFor(area).getChildren().add(node);
      }

    /***********************************************************************************************************************************************************
     * Sets the given {@link Node} as the shown one in the area where it is contained.
     *
     * @param   node        the {@code Node}
     **********************************************************************************************************************************************************/
    public void setShownNode (@Nonnull final Node node)
      {
        log.info("setShownNode({})", node);

        for (final var stackPane : stackPaneMapByArea.values())
          {
            final var children = stackPane.getChildren();

            if (children.contains(node))
              {
                children.forEach(child -> child.setVisible(false));
                node.setVisible(true); // at last
                return;
              }
          }

        throw new IllegalArgumentException("Node not in a managed StackPange: " + node);

      }

    /***********************************************************************************************************************************************************
     *
     **********************************************************************************************************************************************************/
    @Nonnull
    private StackPane findStackPaneFor (@Nonnull final String area)
      {
        final var stackPane = stackPaneMapByArea.get(area);

        if (stackPane == null)
          {
            throw new IllegalArgumentException("Area not handled: " + area);
          }

        return stackPane;
      }
 }
