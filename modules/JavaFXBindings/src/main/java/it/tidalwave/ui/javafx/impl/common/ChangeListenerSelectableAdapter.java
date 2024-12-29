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

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.Executor;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TreeItem;
import it.tidalwave.ui.core.role.PresentationModel;
import it.tidalwave.ui.core.role.Selectable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import static it.tidalwave.ui.core.role.Selectable._Selectable_;

/***************************************************************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **************************************************************************************************************************************************************/
@RequiredArgsConstructor @Slf4j
public class ChangeListenerSelectableAdapter implements ChangeListener<PresentationModel>
  {
    @Nonnull
    protected final Executor executor;

    private final ChangeListener<TreeItem<PresentationModel>> treeItemChangeListener =
            // FIXME: test null oldValue, newValue?
            (observable, oldValue, newValue) -> changed(null, safe(oldValue), safe(newValue));

    @Override
    public void changed (@Nonnull final ObservableValue<? extends PresentationModel> ov,
                         @Nonnull final PresentationModel oldPm,
                         @CheckForNull final PresentationModel newPm)
      {
        if (newPm != null) // no selection
          {
            executor.execute(() -> newPm.maybeAs(_Selectable_).ifPresent(Selectable::select));
          }
      }

    @Nonnull
    public ChangeListener<TreeItem<PresentationModel>> asTreeItemChangeListener()
      {
        return treeItemChangeListener;
      }

    @Nullable
    private static PresentationModel safe (@Nullable final TreeItem<PresentationModel> value)
      {
        return (value != null) ? value.getValue() : null;
      }
  }
