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
package it.tidalwave.role.ui.javafx.impl.tree;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.beans.PropertyChangeListener;
import javafx.application.Platform;
import it.tidalwave.role.ui.Visible;
import it.tidalwave.role.ui.javafx.impl.common.ChangeListenerSelectableAdapter;
import it.tidalwave.role.ui.javafx.impl.common.PresentationModelTreeItem;
import javafx.util.Callback;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import it.tidalwave.util.annotation.VisibleForTesting;
import it.tidalwave.role.ui.PresentationModel;
import it.tidalwave.role.ui.javafx.impl.common.CellBinder;
import it.tidalwave.role.ui.javafx.impl.common.DelegateSupport;
import it.tidalwave.role.ui.javafx.impl.common.JavaFXWorker;
import lombok.extern.slf4j.Slf4j;
import static it.tidalwave.role.ui.Visible._Visible_;
import static java.util.stream.Collectors.toList;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
@Slf4j
public class TreeViewBindings extends DelegateSupport
  {
    @VisibleForTesting final Callback<TreeView<PresentationModel>, TreeCell<PresentationModel>> treeCellFactory;

    private final ObsoletePresentationModelDisposer presentationModelDisposer = new ObsoletePresentationModelDisposer();

    @VisibleForTesting final ChangeListenerSelectableAdapter changeListener = new ChangeListenerSelectableAdapter(executor);

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    public TreeViewBindings (@Nonnull final Executor executor, @Nonnull final CellBinder cellBinder)
      {
        super(executor);
        treeCellFactory = treeView -> new AsObjectTreeCell<>(cellBinder);
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    public void bind (@Nonnull final TreeView<PresentationModel> treeView,
                      @Nonnull final PresentationModel pm,
                      @Nonnull final Optional<Runnable> callback)
      {
        assertIsFxApplicationThread();
        log.debug("bind({}, {}, {})", treeView, pm, callback);

        final ObjectProperty<TreeItem<PresentationModel>> rootProperty = treeView.rootProperty();
        rootProperty.removeListener(presentationModelDisposer);
        rootProperty.addListener(presentationModelDisposer);
        rootProperty.set(createTreeItem(pm, 0));
        callback.ifPresent(Runnable::run);

        treeView.setCellFactory(treeCellFactory);
        treeView.setShowRoot(pm.maybeAs(_Visible_).map(Visible::isVisible).orElse(true));

        final ReadOnlyObjectProperty<TreeItem<PresentationModel>> selectionProperty =
                treeView.getSelectionModel().selectedItemProperty();
        selectionProperty.removeListener(changeListener.asTreeItemChangeListener());
        selectionProperty.addListener(changeListener.asTreeItemChangeListener());
     }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    private TreeItem<PresentationModel> createTreeItem (@Nonnull final PresentationModel pm, final int depth)
      {
        assertIsFxApplicationThread();
        final TreeItem<PresentationModel> item = new PresentationModelTreeItem(pm);

        final PropertyChangeListener recreateChildrenOnUpdateListener = __ ->
          Platform.runLater(() ->
            {
              log.debug("On recreateChildrenOnUpdateListener");
              setChildren(item, pm, depth + 1);
              item.setExpanded(true);
            });

        pm.addPropertyChangeListener(PresentationModel.PROPERTY_CHILDREN, recreateChildrenOnUpdateListener);

        item.expandedProperty().addListener(((observable, oldValue, newValue) ->
          {
            if (newValue)
              {
                setChildren(item, pm, depth + 1);
              }
          }));

        return item;
      }

    /*******************************************************************************************************************
     *
     * Sets the children for a {@link TreeItem}.
     *
     * @param   parentItem  the {@code TreeItem}
     * @param   depth       the depth level (used only for logging)
     *
     ******************************************************************************************************************/
    private void setChildren (@Nonnull final TreeItem<PresentationModel> parentItem,
                              @Nonnull final PresentationModel pm,
                              final int depth)
      {
        assertIsFxApplicationThread();
        JavaFXWorker.run(executor,
                         () -> JavaFXWorker.childrenPm(pm, depth),
                         items -> parentItem.getChildren().setAll(items.stream()
                                                                       .map(childPm -> createTreeItem(childPm, depth))
                                                                       .collect(toList())));
      }
  }
