/*
 * *********************************************************************************************************************
 *
 * SteelBlue: DCI User Interfaces
 * http://tidalwave.it/projects/steelblue
 *
 * Copyright (C) 2015 - 2021 by Tidalwave s.a.s. (http://tidalwave.it)
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
package it.tidalwave.role.ui.javafx.example.large.mainscreen;

import javax.annotation.Nonnull;
import java.nio.file.Path;
import it.tidalwave.util.ui.UserNotificationWithFeedback;
import it.tidalwave.role.ui.BoundProperty;
import it.tidalwave.role.ui.PresentationModel;
import it.tidalwave.role.ui.UserAction;
import lombok.Builder;

/***********************************************************************************************************************
 *
 * This interface describes all the interactions with the presentation object.
 *
 * @stereotype  Presentation
 *
 * @author  Fabrizio Giudici (Fabrizio.Giudici@tidalwave.it)
 *
 **********************************************************************************************************************/
public interface MainScreenPresentation
  {
    /*******************************************************************************************************************
     *
     * If the presentation contains form fields, it's a good practice to group them together within a single class
     * which exposes {@link BoundProperty} instances.
     *
     ******************************************************************************************************************/
    @Builder
    public static class Bindings
      {
        @Nonnull
        public final UserAction buttonAction;

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

    /*******************************************************************************************************************
     *
     * A presentation always exposes a {@code bind()} method which is invoked by the control and binds callbacks
     * and form fields. There is no requirement on the name and signature of the method.
     *
     ******************************************************************************************************************/
    public void bind (@Nonnull Bindings bindings);

    /*******************************************************************************************************************
     *
     * A presentation typically exposes a {@code showUp()} method which brings the object into a visible state.
     * There is no requirement on the name and signature of the method.
     *
     ******************************************************************************************************************/
    public void showUp();

    /*******************************************************************************************************************
     *
     * A presentation also exposes some {@link populateXYZ()} methods which are used to fill various parts of the UI
     * with data. Data structures are modelled by {@link PresentationModel}s. There is no requirement on the name of the
     * method.
     *
     ******************************************************************************************************************/
    public void populate (@Nonnull PresentationModel pm1,
                          @Nonnull PresentationModel pm2,
                          @Nonnull PresentationModel pm3);

    /*******************************************************************************************************************
     *
     * When the control requires an interaction with the user in form of a dialog box with feedback (such as Ok/Cancel)
     * a method must be exposed which accepts a {@link UserNotificationWithFeedback}. There is no requirement on the
     * name of the method.
     *
     ******************************************************************************************************************/
    public void notify (@Nonnull UserNotificationWithFeedback notification);

    /*******************************************************************************************************************
     *
     * Simple message notifications that appear in the presentation (for instance in a {@link Label} can be passed as
     * simple strings of specific methods.
     *
     ******************************************************************************************************************/
    public void notify (@Nonnull String message);

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    public void pickFile (@Nonnull BoundProperty<Path> selectedFile,
                          @Nonnull UserNotificationWithFeedback notification);

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    public void pickDirectory (@Nonnull BoundProperty<Path> selectedFolder,
                               @Nonnull UserNotificationWithFeedback notification);
  }
