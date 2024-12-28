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
package it.tidalwave.ui.example.presentation;

import javax.annotation.Nonnull;
import java.nio.file.Path;
import it.tidalwave.util.ui.UserNotificationWithFeedback;
import it.tidalwave.role.ui.BoundProperty;
import it.tidalwave.role.ui.PresentationModel;
import it.tidalwave.role.ui.UserAction;
import lombok.Builder;

/***************************************************************************************************************************************************************
 *
 * This interface describes all the interactions with the presentation object.
 *
 * @stereotype  Presentation
 *
 * @author  Fabrizio Giudici
 *
 **************************************************************************************************************************************************************/
public interface MainPanelPresentation
  {
    /***********************************************************************************************************************************************************
     * If the presentation contains form fields, it's a good practice to group them together within a single class
     * which exposes {@link BoundProperty} instances.
     **********************************************************************************************************************************************************/
    // START SNIPPET: bindings
    @Builder
    public static class Bindings
      {
        @Nonnull
        public final UserAction actionButton;

        @Nonnull
        public final UserAction actionDialogOk;

        @Nonnull
        public final UserAction actionDialogCancelOk;

        @Nonnull
        public final UserAction actionPickFile;

        @Nonnull
        public final UserAction actionPickDirectory;

        public final BoundProperty<String> textProperty = new BoundProperty<>("1");

        public final BoundProperty<Boolean> booleanProperty = new BoundProperty<>(true);
      }
    // END SNIPPET: bindings

    /***********************************************************************************************************************************************************
     * A presentation always exposes a {@code bind()} method which is invoked by the control and binds callbacks
     * and form fields. There is no requirement on the name and signature of the method.
     *
     * @param   bindings          the container of bindings
     **********************************************************************************************************************************************************/
    public void bind (@Nonnull Bindings bindings);

    /***********************************************************************************************************************************************************
     * A presentation also exposes some {@code populateXYZ()} methods which are used to fill various parts of the UI
     * with model. Data structures are modelled by {@link PresentationModel}s. There is no requirement on the name of the
     * method.
     *
     * @param   pmSimpleEntities  the presentation model for SimpleEntity instances
     * @param   pmDciEntities     the presentation model for SimpleDciEntity instances
     * @param   pmFileEntities    the presentation model for FileEntity instances
     **********************************************************************************************************************************************************/
    public void populate (@Nonnull PresentationModel pmSimpleEntities,
                          @Nonnull PresentationModel pmDciEntities,
                          @Nonnull PresentationModel pmFileEntities);

    /***********************************************************************************************************************************************************
     * When the control requires an interaction with the user in form of a dialog box with feedback (such as Ok/Cancel)
     * a method must be exposed which accepts a {@link UserNotificationWithFeedback}. There is no requirement on the
     * name of the method.
     *
     * @param   notification      the object managing the interaction
     **********************************************************************************************************************************************************/
    public void notify (@Nonnull UserNotificationWithFeedback notification);

    /***********************************************************************************************************************************************************
     * Displays a notification in the presentation.
     *
     * @param  message            the text of the notification
     **********************************************************************************************************************************************************/
    public void notify (@Nonnull String message);

    /***********************************************************************************************************************************************************
     * Asks the user to pick a file.
     *
     * @param   selectedFile      a property containing both the file to pre-select and later the piked file
     * @param   notification      the object managing the interaction
     **********************************************************************************************************************************************************/
    public void pickFile (@Nonnull BoundProperty<Path> selectedFile,
                          @Nonnull UserNotificationWithFeedback notification);

    /***********************************************************************************************************************************************************
     * Asks the user to pick a directory.
     *
     * @param   selectedFolder    a property containing both the directory to pre-select and later the picked folder
     * @param   notification      the object managing the interaction
     **********************************************************************************************************************************************************/
    public void pickDirectory (@Nonnull BoundProperty<Path> selectedFolder,
                               @Nonnull UserNotificationWithFeedback notification);
  }
