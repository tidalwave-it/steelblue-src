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
package it.tidalwave.ui.javafx.impl.list;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javafx.scene.control.cell.TextFieldListCell;
import it.tidalwave.util.As;
import it.tidalwave.ui.javafx.impl.common.CellBinder;
import lombok.AllArgsConstructor;

/***************************************************************************************************************************************************************
 *
 * A specialisation of {@link TextFieldListCell} which binds to an {@link As}-capable item.
 *
 * @author  Fabrizio Giudici
 *
 **************************************************************************************************************************************************************/
@AllArgsConstructor
public class AsObjectListCell<T extends As> extends TextFieldListCell<T>
  {
    @Nonnull
    private final CellBinder cellBinder;

    @Override
    public void updateItem (@CheckForNull final T item, final boolean empty)
      {
        super.updateItem(item, empty);
        cellBinder.bind(this, item, empty);
      }
  }
