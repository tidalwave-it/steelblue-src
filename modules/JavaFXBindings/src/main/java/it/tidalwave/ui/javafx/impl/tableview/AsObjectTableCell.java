/*
 * *************************************************************************************************************************************************************
 *
 * SteelBlue: DCI User Interfaces
 * http://tidalwave.it/projects/steelblue
 *
 * Copyright (C) 2015 - 2025 by Tidalwave s.a.s. (http://tidalwave.it)
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
package it.tidalwave.ui.javafx.impl.tableview;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import javafx.scene.control.cell.TextFieldTableCell;
import it.tidalwave.ui.javafx.impl.common.AsObjectStringConverter;
import it.tidalwave.ui.javafx.impl.common.CellBinder;
import it.tidalwave.util.As;

/***************************************************************************************************************************************************************
 *
 * A specialisation of {@link TextFieldTableCell} which binds to an {@link As}-capable item.
 *
 * @author  Fabrizio Giudici
 *
 **************************************************************************************************************************************************************/
public class AsObjectTableCell<T extends As> extends TextFieldTableCell<T, T>
  {
    @Nonnull
    private final CellBinder cellBinder;

    public AsObjectTableCell (@Nonnull final CellBinder cellBinder)
      {
        super(AsObjectStringConverter.getInstance()); // Required for STB-123
        this.cellBinder = cellBinder;
      }

    @Override
    public void updateItem (@Nullable final T item, final boolean empty)
      {
        super.updateItem(item, empty);
        cellBinder.bind(this, item, empty);
      }
  }
