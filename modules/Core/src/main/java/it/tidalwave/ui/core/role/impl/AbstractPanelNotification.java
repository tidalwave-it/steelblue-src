/*
 * *************************************************************************************************************************************************************
 *
 * blueMarine III: Semantic DAM
 * http://tidalwave.it/projects/bluemarine3
 *
 * Copyright (C) 2024 - 2025 by Tidalwave s.a.s. (http://tidalwave.it)
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
 * git clone https://bitbucket.org/tidalwave/bluemarine3-src
 * git clone https://github.com/tidalwave-it/bluemarine3-src
 *
 * *************************************************************************************************************************************************************
 */
package it.tidalwave.ui.core.role.impl;
import jakarta.annotation.Nonnull;
import it.tidalwave.ui.core.PanelGroupControl;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import static it.tidalwave.util.ShortNames.shortId;
/***************************************************************************************************************************************************************
 *
 * A message notifying that a panel has been hidden.
 *
 * @since       2.0-ALPHA-5
 * @author      Fabrizio Giudici
 *
 **************************************************************************************************************************************************************/
@RequiredArgsConstructor @Getter
public abstract class AbstractPanelNotification
  {
    @Nonnull
    private final Object target;
    @Nonnull
    private final PanelGroupControl.Group group;
    @Override @Nonnull
    public String toString()
      {
        return String.format("%s(target=%s, group=%s)", getClass().getSimpleName(), shortId(target), group);
      }
  }
