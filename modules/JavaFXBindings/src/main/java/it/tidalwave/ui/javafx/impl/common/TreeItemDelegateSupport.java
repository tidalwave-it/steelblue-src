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

import javax.annotation.Nonnull;
import java.beans.PropertyChangeListener;
import java.util.concurrent.Executor;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.scene.control.TreeItem;
import javafx.application.Platform;
import it.tidalwave.util.annotation.VisibleForTesting;
import it.tidalwave.role.ui.PresentationModel;
import it.tidalwave.role.ui.Visible;
import it.tidalwave.ui.javafx.impl.tree.ObsoletePresentationModelDisposer;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import static java.util.stream.Collectors.*;
import static it.tidalwave.role.ui.Visible._Visible_;
import static it.tidalwave.ui.javafx.impl.common.JavaFXWorker.childrenPm;

/***************************************************************************************************************************************************************
 *
 * Some common stuff for both {@code TreeView} and {@code TreeTableView}.
 *
 * @author  Fabrizio Giudici
 *
 **************************************************************************************************************************************************************/
@Slf4j
public class TreeItemDelegateSupport extends DelegateSupport
  {
    @VisibleForTesting @Getter()
    private final ChangeListenerSelectableAdapter selectionListener = new ChangeListenerSelectableAdapter(executor);

    private final ObsoletePresentationModelDisposer presentationModelDisposer = new ObsoletePresentationModelDisposer();

    /***********************************************************************************************************************************************************
     *
     **********************************************************************************************************************************************************/
    public TreeItemDelegateSupport (@Nonnull final Executor executor)
      {
        super(executor);
      }

    /***********************************************************************************************************************************************************
     * Sets the children for a {@link TreeItem}.
     *
     * @param   parentItem  the {@code TreeItem}
     * @param   depth       the depth level (used only for logging)
     **********************************************************************************************************************************************************/
    protected void setChildren (@Nonnull final TreeItem<PresentationModel> parentItem, final int depth)
      {
        JavaFXWorker.run(executor,
                         () -> childrenPm(parentItem.getValue(), depth),
                         items -> parentItem.getChildren().setAll(items.stream()
                                                                       .map(childPm -> createTreeItem(childPm, depth))
                                                                       .collect(toList())));
      }

    /***********************************************************************************************************************************************************
     * Creates a single {@link TreeItem} for the given the {@link PresentationModel}. When the {@code PresentationModel}
     * fires the {@link PresentationModel#PROPERTY_CHILDREN} property change event, children are recreated.
     *
     * @param   pm        the {@code PresentationModel}
     * @param   depth     the depth level (used only for logging)
     * @return            the
     **********************************************************************************************************************************************************/
    @Nonnull
    protected TreeItem<PresentationModel> createTreeItem (@Nonnull final PresentationModel pm, final int depth)
      {
        assertIsFxApplicationThread();
        final TreeItem<PresentationModel> item = new PresentationModelTreeItem(pm);

        final PropertyChangeListener recreateChildrenOnUpdateListener = __ ->
                Platform.runLater(() ->
          {
            log.debug("On recreateChildrenOnUpdateListener");
            setChildren(item, depth + 1);
            item.setExpanded(true);
          });

        pm.addPropertyChangeListener(PresentationModel.PROPERTY_CHILDREN, recreateChildrenOnUpdateListener);

        item.expandedProperty().addListener((observable, oldValue, newValue) ->
          {
            if (newValue)
              {
                setChildren(item, depth + 1);
              }
          });

        return item;
      }

    /***********************************************************************************************************************************************************
     *
     **********************************************************************************************************************************************************/
    protected void setRootProperty (@Nonnull final PresentationModel pm,
                                    @Nonnull final ObjectProperty<TreeItem<PresentationModel>> rootProperty)
      {
        rootProperty.removeListener(presentationModelDisposer);
        rootProperty.addListener(presentationModelDisposer);
        rootProperty.set(createTreeItem(pm, 0));
      }

    /***********************************************************************************************************************************************************
     *
     **********************************************************************************************************************************************************/
    protected void bindSelectionListener (@Nonnull final ReadOnlyObjectProperty<TreeItem<PresentationModel>> selectedItemProperty)
      {
        selectedItemProperty.removeListener(selectionListener.asTreeItemChangeListener());
        selectedItemProperty.addListener(selectionListener.asTreeItemChangeListener());
      }

    /***********************************************************************************************************************************************************
     *
     **********************************************************************************************************************************************************/
    protected static Boolean shouldShowRoot (@Nonnull final PresentationModel pm)
      {
        return pm.maybeAs(_Visible_).map(Visible::isVisible).orElse(true);
      }
  }
