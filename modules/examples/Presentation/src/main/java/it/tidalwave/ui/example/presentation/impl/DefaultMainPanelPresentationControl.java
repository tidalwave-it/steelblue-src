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
package it.tidalwave.ui.example.presentation.impl;

import javax.annotation.Nonnull;
import javax.annotation.PostConstruct;
import java.util.List;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.stereotype.Component;
import it.tidalwave.message.PowerOnEvent;
import it.tidalwave.util.annotation.VisibleForTesting;
import it.tidalwave.role.Aggregate;
import it.tidalwave.role.ui.BoundProperty;
import it.tidalwave.role.ui.Displayable;
import it.tidalwave.ui.core.MenuBarModel.MenuPlacement;
import it.tidalwave.role.ui.PresentationModel;
import it.tidalwave.role.ui.PresentationModelAggregate;
import it.tidalwave.role.ui.Selectable;
import it.tidalwave.role.ui.UserAction;
import it.tidalwave.role.ui.UserActionProvider;
import it.tidalwave.role.ui.Visible;
import it.tidalwave.ui.example.model.Dao;
import it.tidalwave.ui.example.model.SimpleDciEntity;
import it.tidalwave.ui.example.model.SimpleEntity;
import it.tidalwave.ui.example.presentation.MainPanelPresentation;
import it.tidalwave.ui.example.presentation.MainPanelPresentation.Bindings;
import it.tidalwave.ui.example.presentation.MainPanelPresentationControl;
import it.tidalwave.messagebus.annotation.ListensTo;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import static it.tidalwave.util.Parameters.r;
import static it.tidalwave.util.ui.UserNotificationWithFeedback.*;
import static it.tidalwave.role.ui.Presentable._Presentable_;
import static it.tidalwave.role.ui.spi.PresentationModelCollectors.toCompositePresentationModel;

/***************************************************************************************************************************************************************
 *
 * @stereotype  Control
 *
 * @author  Fabrizio Giudici
 *
 **************************************************************************************************************************************************************/
@Component @RequiredArgsConstructor
// @SimpleMessageSubscriber
public class DefaultMainPanelPresentationControl implements MainPanelPresentationControl
  {
    private static final Path USER_HOME = Paths.get(System.getProperty("user.home"));

    // START SNIPPET: injections
    @Nonnull
    private final Dao dao;

    @Nonnull
    private final MainPanelPresentation presentation;
    // END SNIPPET: injections

    // For each button on the presentation that can do something, a UserAction is provided.
    // START SNIPPET: userActions
    @Getter
    private final UserAction actionButton = UserAction.of(this::onButtonPressed,
                                                          Displayable.of("Press me"));

    @Getter
    private final UserAction actionDialogOk = UserAction.of(this::onButtonDialogOkPressed,
                                                            Displayable.of("Dialog with ok"));

    @Getter
    private final UserAction actionDialogCancelOk = UserAction.of(this::onButtonDialogOkCancelPressed, List.of(
                                                                  Displayable.of("Dialog with ok/cancel"),
                                                                  MenuPlacement.under("Tools")));

    @Getter
    private final UserAction actionPickFile = UserAction.of(this::onButtonPickFilePressed, List.of(
                                                            Displayable.of("Open file..."),
                                                            MenuPlacement.under("File")));

    @Getter
    private final UserAction actionPickDirectory = UserAction.of(this::onButtonPickDirectoryPressed, List.of(
                                                            Displayable.of("Open directory..."),
                                                            MenuPlacement.under("File")));
    // END SNIPPET: userActions
    // START SNIPPET: bindings
    private final Bindings bindings = Bindings.builder()
                                              .actionButton(actionButton)
                                              .actionDialogOk(actionDialogOk)
                                              .actionDialogCancelOk(actionDialogCancelOk)
                                              .actionPickFile(actionPickFile)
                                              .actionPickDirectory(actionPickDirectory)
                                              .build();
    // END SNIPPET: bindings

    // Then there can be a set of variables that represent the internal state of the control.
    @VisibleForTesting int status = 1;

    /***********************************************************************************************************************************************************
     * At {@link PostConstruct} time the control just performs the binding to the presentation.
     **********************************************************************************************************************************************************/
    // START SNIPPET: initialization
    @PostConstruct
    @VisibleForTesting void initialize()
      {
        presentation.bind(bindings);
      }
    // END SNIPPET: initialization

    /***********************************************************************************************************************************************************
     * {@inheritDoc}
     *
     * This method demonstrates the typical idiom for populating model:
     *
     * 1. A dao is called to provide raw model - let's say in form of collections;
     * 2. Objects in the collection are transformed into PresentationModels.
     * 3. The PresentationModels are then passed to the presentation.
     **********************************************************************************************************************************************************/
    // START SNIPPET: populate
    @Override
    public void populate ()
      {
        final var entities1 = dao.getSimpleEntities();
        final var entities2 = dao.getDciEntities();
        final var files = dao.getFiles();
        final var pmEnt1 = entities1.stream().map(this::pmFor).collect(toCompositePresentationModel());
        final var pmEnt2 = entities2.stream().map(this::pmFor).collect(toCompositePresentationModel());
        final var pmFiles = files.stream()
                             .map(item -> item.as(_Presentable_).createPresentationModel())
                             .collect(toCompositePresentationModel(r(Visible.INVISIBLE)));
        presentation.populate(pmEnt1, pmEnt2, pmFiles);
      }
    // END SNIPPET: populate

    /***********************************************************************************************************************************************************
     * Alternatively to expose methods to a public interface, a pubsub facility can be used. This method is called back at application initialisation.
     * @param   event   the 'power on' event
     **********************************************************************************************************************************************************/
    // START SNIPPET: onPowerOn
    @VisibleForTesting void onPowerOn (@ListensTo final PowerOnEvent event)
      {
        presentation.bind(bindings);
        populate();
      }
    // END SNIPPET: onPowerOn

    /***********************************************************************************************************************************************************
     * Factory method for the PresentationModel of SimpleEntity instances.
     *
     * It aggregates a few extra roles into the PresentationModel that are used by the control, such as callbacks
     * for action associated to the context menu. Also a Displayable role is usually injected to control the rendering
     * of entities.
     **********************************************************************************************************************************************************/
    // START SNIPPET: pmSimpleEntity
    @Nonnull
    private PresentationModel pmFor (@Nonnull final SimpleEntity entity)
      {
        final Selectable selectable = () -> onSelected(entity);
        final var action1 = UserAction.of(() -> action1(entity), Displayable.of("Action 1"));
        final var action2 = UserAction.of(() -> action2(entity), Displayable.of("Action 2"));
        final var action3 = UserAction.of(() -> action3(entity), Displayable.of("Action 3"));
        return PresentationModel.of(entity, r(Displayable.of("Item #" + entity.getName()),
                                              selectable,
                                              UserActionProvider.of(action1, action2, action3)));
      }
    // END SNIPPET: pmSimpleEntity

    /***********************************************************************************************************************************************************
     * Factory method for the PresentationModel of SimpleDciEntity instances.
     **********************************************************************************************************************************************************/
    // START SNIPPET: pmSimpleDciEntity
    @Nonnull
    private PresentationModel pmFor (@Nonnull final SimpleDciEntity entity)
      {
        // FIXME: column names
        final Aggregate<PresentationModel> aggregate = PresentationModelAggregate.newInstance()
             .withPmOf("C1", r(Displayable.of(entity.getName())))
             .withPmOf("C2", r(Displayable.of("" + entity.getAttribute1())))
             .withPmOf("C3", r(Displayable.of("" + entity.getAttribute2())));
        final Selectable selectable = () -> onSelected(entity);
        final var action1 = UserAction.of(() -> action1(entity), Displayable.of("Action 1"));
        final var action2 = UserAction.of(() -> action2(entity), Displayable.of("Action 2"));
        final var action3 = UserAction.of(() -> action3(entity), Displayable.of("Action 3"));
        // No explicit Displayable here, as the one inside SimpleDciEntity is used.
        return PresentationModel.of(entity, r(aggregate, selectable, UserActionProvider.of(action1, action2, action3)));
      }
    // END SNIPPET: pmSimpleDciEntity

    // Below simple business methods, as per usual business.

    /***********************************************************************************************************************************************************
     *
     **********************************************************************************************************************************************************/
    // START SNIPPET: onButtonPressed
    private void onButtonPressed()
      {
        presentation.notify("Button pressed");
        status++;
        bindings.textProperty.set(Integer.toString(status));
      }
    // END SNIPPET: onButtonPressed

    /***********************************************************************************************************************************************************
     *
     **********************************************************************************************************************************************************/
    // START SNIPPET: onButtonDialogOkPressed
    private void onButtonDialogOkPressed()
      {
        presentation.notify(notificationWithFeedback()
                .withCaption("Notification")
                .withText("Now press the button")
                .withFeedback(feedback().withOnConfirm(() -> presentation.notify("Pressed ok"))));
      }
    // END SNIPPET: onButtonDialogOkPressed

    /***********************************************************************************************************************************************************
     *
     **********************************************************************************************************************************************************/
    // START SNIPPET: onButtonDialogOkCancelPressed
    private void onButtonDialogOkCancelPressed()
      {
        presentation.notify(notificationWithFeedback()
                .withCaption("Notification")
                .withText("Now press the button")
                .withFeedback(feedback().withOnConfirm(() -> presentation.notify("Pressed ok"))
                                        .withOnCancel(() -> presentation.notify("Pressed cancel"))));
      }
    // END SNIPPET: onButtonDialogOkCancelPressed

    /***********************************************************************************************************************************************************
     * This method demonstrates how to pick a file name by using the proper UI dialog.
     **********************************************************************************************************************************************************/
    // START SNIPPET: onButtonPickFilePressed
    private void onButtonPickFilePressed()
      {
        final var selectedFile = new BoundProperty<>(USER_HOME);
        presentation.pickFile(selectedFile,
            notificationWithFeedback()
                .withCaption("Pick a file")
                .withFeedback(feedback().withOnConfirm(() -> presentation.notify("Selected file: " + selectedFile.get()))
                                        .withOnCancel(() -> presentation.notify("Selection cancelled"))));
      }
    // END SNIPPET: onButtonPickFilePressed

    /***********************************************************************************************************************************************************
     * This method demonstrates how to pick a directory name by using the proper UI dialog.
     **********************************************************************************************************************************************************/
    // START SNIPPET: onButtonPickDirectoryPressed
    private void onButtonPickDirectoryPressed()
      {
        final var selectedFolder = new BoundProperty<>(USER_HOME);
        presentation.pickDirectory(selectedFolder,
            notificationWithFeedback()
                .withCaption("Pick a directory")
                .withFeedback(feedback().withOnConfirm(() -> presentation.notify("Selected directory: " + selectedFolder.get()))
                                        .withOnCancel(() -> presentation.notify("Selection cancelled"))));
      }
    // END SNIPPET: onButtonPickDirectoryPressed

    /***********************************************************************************************************************************************************
     *
     **********************************************************************************************************************************************************/
    // START SNIPPET: onSelected
    private void onSelected (@Nonnull final Object object)
      {
        presentation.notify("Selected " + object);
      }
    // END SNIPPET: onSelected

    /***********************************************************************************************************************************************************
     *
     **********************************************************************************************************************************************************/
    // START SNIPPET: action1
    private void action1 (@Nonnull final Object object)
      {
        presentation.notify("Action 1 on " + object);
      }
    // END SNIPPET: action1

    /***********************************************************************************************************************************************************
     *
     **********************************************************************************************************************************************************/
    private void action2 (@Nonnull final Object object)
      {
        presentation.notify("Action 2 on " + object);
      }

    /***********************************************************************************************************************************************************
     *
     **********************************************************************************************************************************************************/
    private void action3 (@Nonnull final Object object)
      {
        presentation.notify("Action 3 on " + object);
      }
  }
