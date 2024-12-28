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
import java.util.Collection;
import java.util.Optional;
import java.nio.file.Path;
import javafx.beans.property.Property;
import javafx.scene.Node;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.TreeView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Window;
import it.tidalwave.ui.javafx.role.CustomGraphicProvider;
import it.tidalwave.util.ui.UserNotification;
import it.tidalwave.util.ui.UserNotificationWithFeedback;
import it.tidalwave.role.ui.BoundProperty;
import it.tidalwave.role.ui.PresentationModel;
import it.tidalwave.role.ui.UserAction;

/***************************************************************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **************************************************************************************************************************************************************/
public interface JavaFXBinder
  {
    /***********************************************************************************************************************************************************
     * Sets the main window. This operation must be performed before any other method is called. This operation is
     * automatically performed by the SteelBlue runtime.
     *
     * @param   window      the main window
     **********************************************************************************************************************************************************/
    public void setMainWindow (@Nonnull Window window);

    /***********************************************************************************************************************************************************
     * Binds a button to a {@link UserAction}. The following roles o the action are used:
     *
     * <ul>
     * <li>Displayable: to set the label of the button</li>
     * </ul>
     *
     * The action is used as a callback when the button is pressed; invoked in a thread provided by the binder executor.
     * The {@code enabled} property of the {@code UserAction} is bound, negated, to the {@code disabled} property of the
     * button.
     *
     * @param   button      the button
     * @param   action      the action
     **********************************************************************************************************************************************************/
    public void bind (@Nonnull ButtonBase button, @Nonnull UserAction action);

    /***********************************************************************************************************************************************************
     * Binds a menu item to a {@link UserAction}. The following roles o the action are used:
     *
     * <ul>
     * <li>Displayable: to set the label of the menu item</li>
     * </ul>
     *
     * The action is used as a callback when the button is pressed; invoked in a thread provided by the binder executor.
     * The {@code enabled} property of the {@code UserAction} is bound, negated, to the {@code disabled} property of the
     * menu item.
     *
     * @param   menuItem    the menu item
     * @param   action      the action
     **********************************************************************************************************************************************************/
    public void bind (@Nonnull MenuItem menuItem, @Nonnull UserAction action);

    /***********************************************************************************************************************************************************
     * Binds a {@link TableView} to a {@link PresentationModel} and an optional callback.
     *
     * The {@code PresentationModel} is used to populate the table. The following roles are used:
     *
     * <ul>
     * <li>A {@link it.tidalwave.role.SimpleComposite} provides children {@code PresentationModel}s for each row.</li>
     * <li>In each row, an {@link it.tidalwave.role.Aggregate<PresentationModel>} is used to provide the {@code PresentationModel}s for
     *     each column.</li>
     * <li>A {@link it.tidalwave.role.ui.Displayable} (optional) is used to provide the text to render for each item.</li>
     * <li>A {@link CustomGraphicProvider} (optional) is used to provide the graphics to render for each item.</li>
     * <li>A {@link it.tidalwave.role.ui.Styleable} (optional) is used to provide the rendering style for each item.</li>
     * <li>A {@link it.tidalwave.role.ui.UserActionProvider} (optional) is used to provide the actions for creating a context menu;
     *     the default action is also bound to the double click or SPACE gesture.</li>
     * </ul>
     *
     * The process of populating data is performed in background threads, so this method quickly returns also in case
     * of large amount of data.
     * The initialization callback is called in the JavaFX thread when data population has been completed.
     *
     * @since   1.0-ALPHA-13
     * @param   tableView       the {@code TablewView}
     * @param   pm              the {@code PresentationModel}
     * @param   initCallback    the callback
     **********************************************************************************************************************************************************/
    public void bind (@Nonnull TableView<PresentationModel> tableView,
                      @Nonnull PresentationModel pm,
                      @Nonnull Optional<Runnable> initCallback);

    /***********************************************************************************************************************************************************
     * Binds a {@link TableView} to a {@link PresentationModel} and a callback.
     * See {@link #bind(javafx.scene.control.TableView, it.tidalwave.role.ui.PresentationModel, java.util.Optional)}.
     *
     * The process of populating data is performed in background threads, so this method quickly returns also in case
     * of large amount of data.
     * The initialization callback is called in the JavaFX thread when data population has been completed.
     *
     * @param   tableView       the {@code TablewView}
     * @param   pm              the {@code PresentationModel}
     * @param   initCallback    the callback
     **********************************************************************************************************************************************************/
    public default void bind (@Nonnull final TableView<PresentationModel> tableView,
                              @Nonnull final PresentationModel pm,
                              @Nonnull final Runnable initCallback)
      {
        bind(tableView, pm, Optional.of(initCallback));
      }

    /***********************************************************************************************************************************************************
     * Binds a {@link TableView} to a {@link PresentationModel}.
     * See {@link #bind(javafx.scene.control.TableView, it.tidalwave.role.ui.PresentationModel, java.util.Optional)}.
     *
     * @since   1.0-ALPHA-13
     * @param   tableView       the {@code TablewView}
     * @param   pm              the {@code PresentationModel}
     **********************************************************************************************************************************************************/
    public default void bind (@Nonnull final TableView<PresentationModel> tableView,
                              @Nonnull final PresentationModel pm)
      {
        bind(tableView, pm, Optional.empty());
      }

    /***********************************************************************************************************************************************************
     * Binds a {@link TreeView} to a {@link PresentationModel} and a callback.
     *
     * The {@code PresentationModel} is used to populate the table. The following roles are used:
     *
     * <ul>
     * <li>A {@link it.tidalwave.role.SimpleComposite} provides children {@code PresentationModel}s for each row.</li>
     * <li>In each row, an {@link it.tidalwave.role.Aggregate<PresentationModel>} is used to provide the {@code PresentationModel}s for
     *     each column.</li>
     * <li>A {@link it.tidalwave.role.ui.Displayable} (optional) is used to provide the text to render for each item.</li>
     * <li>A {@link CustomGraphicProvider} (optional) is used to provide the graphics to render for each item.</li>
     * <li>A {@link it.tidalwave.role.ui.Styleable} (optional) is used to provide the rendering style for each item.</li>
     * <li>A {@link it.tidalwave.role.ui.UserActionProvider} (optional) is used to provide the actions for creating a context menu;
     *     the default action is also bound to the double click or SPACE gesture.</li>
     * <li>A {@link it.tidalwave.role.ui.Visible} (optional) is used to decide whether the root node should be visible or not.</li>
     * </ul>
     *
     * The process of populating data is performed in background threads, so this method quickly returns also in case
     * of large amount of data.
     * The initialization callback is called in the JavaFX thread when data population has been completed.
     *
     * @param   treeView        the {@code TreeView}
     * @param   pm              the {@code PresentationModel}
     * @param   initCallback    the callback
     **********************************************************************************************************************************************************/
    public void bind (@Nonnull TreeView<PresentationModel> treeView,
                      @Nonnull PresentationModel pm,
                      @Nonnull Optional<Runnable> initCallback);

    /***********************************************************************************************************************************************************
     * Binds a {@link TableView} to a {@link PresentationModel} and a callback.
     * See {@link #bind(javafx.scene.control.TreeView, it.tidalwave.role.ui.PresentationModel, java.util.Optional)}.
     *
     * @since   1.0-ALPHA-13
     * @param   treeView        the {@code TreeView}
     * @param   pm              the {@code PresentationModel}
     * @param   initCallback    the callback
     **********************************************************************************************************************************************************/
    public default void bind (@Nonnull final TreeView<PresentationModel> treeView,
                              @Nonnull final PresentationModel pm,
                              @Nonnull final Runnable initCallback)
      {
        bind(treeView, pm, Optional.of(initCallback));
      }

    /***********************************************************************************************************************************************************
     * Binds a {@link TableView} to a {@link PresentationModel}.
     * See {@link #bind(javafx.scene.control.TableView, it.tidalwave.role.ui.PresentationModel, java.util.Optional)}
     *
     * @since   1.0-ALPHA-13
     * @param   treeView        the {@code TreeView}
     * @param   pm              the {@code PresentationModel}
     **********************************************************************************************************************************************************/
    public default void bind (@Nonnull final TreeView<PresentationModel> treeView,
                              @Nonnull final PresentationModel pm)
      {
        bind(treeView, pm, Optional.empty());
      }

    /***********************************************************************************************************************************************************
     * Binds a {@link TreeTableView} to a {@link PresentationModel} and a callback.
     *
     * The {@code PresentationModel} is used to populate the table. The following roles are used:
     *
     * <ul>
     * <li>A {@link it.tidalwave.role.SimpleComposite} provides children {@code PresentationModel}s for each row.</li>
     * <li>In each row, an {@link it.tidalwave.role.Aggregate<PresentationModel>} is used to provide the {@code PresentationModel}s for
     *     each column.</li>
     * <li>A {@link it.tidalwave.role.ui.Displayable} (optional) is used to provide the text to render for each item.</li>
     * <li>A {@link CustomGraphicProvider} (optional) is used to provide the graphics to render for each item.</li>
     * <li>A {@link it.tidalwave.role.ui.Styleable} (optional) is used to provide the rendering style for each item.</li>
     * <li>A {@link it.tidalwave.role.ui.UserActionProvider} (optional) is used to provide the actions for creating a context menu;
     *     the default action is also bound to the double click or SPACE gesture.</li>
     * <li>A {@link it.tidalwave.role.ui.Visible} (optional) is used to decide whether the root node should be visible or not.</li>
     * </ul>
     *
     * The process of populating data is performed in background threads, so this method quickly returns also in case
     * of large amount of data.
     * The initialization callback is called in the JavaFX thread when data population has been completed.
     *
     * @param   treeTableView   the {@code TreeTableView}
     * @param   pm              the {@code PresentationModel}
     * @param   initCallback    the callback
     **********************************************************************************************************************************************************/
    public void bind (@Nonnull TreeTableView<PresentationModel> treeTableView,
                      @Nonnull PresentationModel pm,
                      @Nonnull Optional<Runnable> initCallback);

    /***********************************************************************************************************************************************************
     * Binds a {@link TreeTableView} to a {@link PresentationModel} and a callback.
     * See {@link #bind(javafx.scene.control.TreeTableView, it.tidalwave.role.ui.PresentationModel, java.util.Optional)}.
     *
     * @since   1.0-ALPHA-13
     * @param   treeTableView   the {@code TreeTableView}
     * @param   pm              the {@code PresentationModel}
     * @param   initCallback    the callback
     **********************************************************************************************************************************************************/
    public default void bind (@Nonnull final TreeTableView<PresentationModel> treeTableView,
                              @Nonnull final PresentationModel pm,
                              @Nonnull final Runnable initCallback)
      {
        bind(treeTableView, pm, Optional.of(initCallback));
      }

    /***********************************************************************************************************************************************************
     * Binds a {@link TreeTableView} to a {@link PresentationModel}.
     * See {@link #bind(javafx.scene.control.TreeTableView, it.tidalwave.role.ui.PresentationModel, java.util.Optional)}.
     *
     * @since   1.0-ALPHA-13
     * @param   treeTableView   the {@code TreeTableView}
     * @param   pm              the {@code PresentationModel}
     **********************************************************************************************************************************************************/
    public default void bind (@Nonnull final TreeTableView<PresentationModel> treeTableView,
                              @Nonnull final PresentationModel pm)
      {
        bind(treeTableView, pm, Optional.empty());
      }

    /***********************************************************************************************************************************************************
     * Binds a {@link ListView} to a {@link PresentationModel} and an optional callback.
     *
     * The {@code PresentationModel} is used to populate the table. The following roles are used:
     *
     * <ul>
     * <li>A {@link it.tidalwave.role.SimpleComposite} provides children {@code PresentationModel}s for each row.</li>
     * <li>In each row, an {@link it.tidalwave.role.Aggregate<PresentationModel>} is used to provide the {@code PresentationModel}s for
     *     each column.</li>
     * <li>A {@link it.tidalwave.role.ui.Displayable} (optional) is used to provide the text to render for each item.</li>
     * <li>A {@link CustomGraphicProvider} (optional) is used to provide the graphics to render for each item.</li>
     * <li>A {@link it.tidalwave.role.ui.Styleable} (optional) is used to provide the rendering style for each item.</li>
     * <li>A {@link it.tidalwave.role.ui.UserActionProvider} (optional) is used to provide the actions for creating a context menu;
     *     the default action is also bound to the double click or SPACE gesture.</li>
     * </ul>
     *
     * The process of populating data is performed in background threads, so this method quickly returns also in case
     * of large amount of data.
     * The initialization callback is called in the JavaFX thread when data population has been completed.
     *
     * @param   listView        the {@code ListView}
     * @param   pm              the {@code PresentationModel}
     * @param   initCallback    the callback
     **********************************************************************************************************************************************************/
    public void bind (@Nonnull ListView<PresentationModel> listView,
                      @Nonnull PresentationModel pm,
                      @Nonnull Optional<Runnable> initCallback);

    /***********************************************************************************************************************************************************
     * Binds a {@link ListView} to a {@link PresentationModel} and a callback.
     * See {@link #bind(javafx.scene.control.ListView, it.tidalwave.role.ui.PresentationModel, java.util.Optional)}.
     *
     * @since   1.0-ALPHA-13
     * @param   listView        the {@code ListView}
     * @param   pm              the {@code PresentationModel}
     * @param   initCallback    the callback
     **********************************************************************************************************************************************************/
    public default void bind (@Nonnull final ListView<PresentationModel> listView,
                              @Nonnull final PresentationModel pm,
                              @Nonnull final Runnable initCallback)
      {
        bind(listView, pm, Optional.of(initCallback));
      }

    /***********************************************************************************************************************************************************
     * Binds a {@link ComboBox} to a {@link PresentationModel}.
     * See {@link #bind(javafx.scene.control.ListView, it.tidalwave.role.ui.PresentationModel, java.util.Optional)}.
     *
     * @since   1.0-ALPHA-13
     * @param   listView        the {@code ListView}
     * @param   pm              the {@code PresentationModel}
     **********************************************************************************************************************************************************/
    public default void bind (@Nonnull final ListView<PresentationModel> listView,
                              @Nonnull final PresentationModel pm)
      {
        bind(listView, pm, Optional.empty());
      }

    /***********************************************************************************************************************************************************
     * Binds a {@link ComboBox} to a {@link PresentationModel} and an optional callback.
     *
     * The {@code PresentationModel} is used to populate the table. The following roles are used:
     *
     * <ul>
     * <li>A {@link it.tidalwave.role.SimpleComposite} provides children {@code PresentationModel}s for each row.</li>
     * <li>In each row, an {@link it.tidalwave.role.Aggregate<PresentationModel>} is used to provide the {@code PresentationModel}s for
     *     each column.</li>
     * <li>A {@link it.tidalwave.role.ui.Displayable} (optional) is used to provide the text to render for each item.</li>
     * <li>A {@link CustomGraphicProvider} (optional) is used to provide the graphics to render for each item.</li>
     * <li>A {@link it.tidalwave.role.ui.Styleable} (optional) is used to provide the rendering style for each item.</li>
     * <li>A {@link it.tidalwave.role.ui.UserActionProvider} (optional) is used to provide the actions for creating a context menu;
     *     the default action is also bound to the double click or SPACE gesture.</li>
     * </ul>
     *
     * The process of populating data is performed in background threads, so this method quickly returns also in case
     * of large amount of data.
     * The initialization callback is called in the JavaFX thread when data population has been completed.
     *
     * @param   comboBox        the {@code ComboBox}
     * @param   pm              the {@code PresentationModel}
     * @param   initCallback    the callback
     **********************************************************************************************************************************************************/
    public void bind (@Nonnull ComboBox<PresentationModel> comboBox,
                      @Nonnull PresentationModel pm,
                      @Nonnull Optional<Runnable> initCallback);

    /***********************************************************************************************************************************************************
     * Binds a {@link ComboBox} to a {@link PresentationModel} and a callback.
     * See {@link #bind(javafx.scene.control.ComboBox, it.tidalwave.role.ui.PresentationModel, java.util.Optional)}.
     *
     * @since   1.0-ALPHA-13
     * @param   comboBox        the {@code ComboBox}
     * @param   pm              the {@code PresentationModel}
     * @param   initCallback    the callback
     **********************************************************************************************************************************************************/
    public default void bind (@Nonnull final ComboBox<PresentationModel> comboBox,
                              @Nonnull final PresentationModel pm,
                              @Nonnull final Runnable initCallback)
      {
        bind(comboBox, pm, Optional.of(initCallback));
      }

    /***********************************************************************************************************************************************************
     * Binds a {@link ComboBox} to a {@link PresentationModel}.
     * See {@link #bind(javafx.scene.control.ComboBox, it.tidalwave.role.ui.PresentationModel, java.util.Optional)}.
     *
     * @since   1.0-ALPHA-13
     * @param   comboBox        the {@code ComboBox}
     * @param   pm              the {@code PresentationModel}
     **********************************************************************************************************************************************************/
    public default void bind (@Nonnull final ComboBox<PresentationModel> comboBox,
                              @Nonnull final PresentationModel pm)
      {
        bind(comboBox, pm, Optional.empty());
      }

    /***********************************************************************************************************************************************************
     * Given a {@link PresentationModel} that contains a {@link it.tidalwave.role.Composite}, populate the pane with
     * {@link ToggleButton}s associated to the elements of the {@link it.tidalwave.role.Composite}. Each element is searched for the
     * following roles:
     *
     * <ul>
     * <li>{@link it.tidalwave.role.ui.UserActionProvider} (mandatory) to provide a callback for the button</li>
     * <li>{@link it.tidalwave.role.ui.Displayable} to provide a text for the button</li>
     * <li>{@link it.tidalwave.role.ui.Styleable} to provide a CSS style for the button</li>
     * </ul>
     *
     * The pane must be pre-populated with at least one button, which will be queried for the CSS style.
     *
     * @param   pane        the {@code Pane}
     * @param   pm          the {@code PresentationModel}
     **********************************************************************************************************************************************************/
    public void bindToggleButtons (@Nonnull Pane pane, @Nonnull PresentationModel pm);

    /***********************************************************************************************************************************************************
     * Deprecated. Merge to bindToggleButtons, passing some arguments for choosing toggle or normal buttons.
     *
     * @deprecated
     **********************************************************************************************************************************************************/
    @Deprecated
    public void bindButtonsInPane (@Nonnull GridPane gridPane, @Nonnull Collection<UserAction> actions);

    /***********************************************************************************************************************************************************
     * Bidirectionally binds two properties.
     *
     * @param   <T>         the property type
     * @param   property1   the former property
     * @param   property2   the latter property
     **********************************************************************************************************************************************************/
    public <T> void bindBidirectionally (@Nonnull Property<T> property1, @Nonnull BoundProperty<T> property2);

    /***********************************************************************************************************************************************************
     *
     **********************************************************************************************************************************************************/
    public <T> void bindBidirectionally (@Nonnull TextField textField,
                                         @Nonnull BoundProperty<String> textProperty,
                                         @Nonnull BoundProperty<Boolean> validProperty);

    /***********************************************************************************************************************************************************
     * Shows a modal dialog with the given content and provides feedback by means of the given notification.
     *
     * @param   notification  the object notifying whether the operation is confirmed or cancelled
     * @since   1.1-ALPHA-6
     **********************************************************************************************************************************************************/
    public default void showInModalDialog (@Nonnull final UserNotification notification)
      {
        showInModalDialog(UserNotificationWithFeedback.notificationWithFeedback()
                                                      .withCaption(notification.getCaption())
                                                      .withText(notification.getText()));
      }

    /***********************************************************************************************************************************************************
     * Shows a modal dialog with the given content and provides feedback by means of the given notification.
     *
     * @param  node          the dialog content
     * @param  notification  the object notifying whether the operation is confirmed or cancelled
     **********************************************************************************************************************************************************/
    public void showInModalDialog (@Nonnull UserNotificationWithFeedback notification,
                                   @Nonnull Optional<Node> node);

    // FIXME: use a Builder, merge with the above
    public default void showInModalDialog (@Nonnull final Node node,
                                           @Nonnull final UserNotificationWithFeedback notification,
                                           @Nonnull final BoundProperty<Boolean> valid)
      {
        showInModalDialog(notification, Optional.of(node));
      }

    @Deprecated
    public default void showInModalDialog (@Nonnull final Node node,
                                           @Nonnull final UserNotificationWithFeedback notification)
      {
        showInModalDialog(notification, Optional.of(node));
      }

    public default void showInModalDialog (@Nonnull final UserNotificationWithFeedback notification)
      {
        showInModalDialog(notification, Optional.empty());
      }

    /***********************************************************************************************************************************************************
     * Opens the FileChooser for selecting a file. The outcome of the operation (confirmed or cancelled) will be
     * notified to the given notification object. The selected file will be set to the given bound property, which can
     * be also used to set the default value rendered on the FileChooser.
     *
     * @param  notification  the object notifying whether the operation is confirmed or cancelled
     * @param  selectedFile  the property containing the selected file
     **********************************************************************************************************************************************************/
    public void openFileChooserFor (@Nonnull UserNotificationWithFeedback notification,
                                    @Nonnull BoundProperty<Path> selectedFile);

    /***********************************************************************************************************************************************************
     * Opens the FileChooser for selecting a folder. The outcome of the operation (confirmed or cancelled) will be
     * notified to the given notification object. The selected folder will be set to the given bound property, which can
     * be also used to set the default value rendered on the FileChooser.
     *
     * @param  notification    the object notifying whether the operation is confirmed or cancelled
     * @param  selectedFolder  the property containing the selected folder
     **********************************************************************************************************************************************************/
    public void openDirectoryChooserFor (@Nonnull UserNotificationWithFeedback notification,
                                         @Nonnull BoundProperty<Path> selectedFolder);
  }
