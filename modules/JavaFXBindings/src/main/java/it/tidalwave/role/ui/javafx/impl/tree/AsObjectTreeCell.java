/*
 * #%L
 * *********************************************************************************************************************
 *
 * SteelBlue
 * http://steelblue.tidalwave.it - git clone git@bitbucket.org:tidalwave/steelblue-src.git
 * %%
 * Copyright (C) 2015 - 2016 Tidalwave s.a.s. (http://tidalwave.it)
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
 * $Id$
 *
 * *********************************************************************************************************************
 * #L%
 */
package it.tidalwave.role.ui.javafx.impl.tree;

import javax.annotation.CheckForNull;
import javax.inject.Inject;
import javafx.scene.control.cell.TextFieldTreeCell;
import org.springframework.beans.factory.annotation.Configurable;
import it.tidalwave.util.As;
import it.tidalwave.util.VisibleForTesting;
import it.tidalwave.role.ui.javafx.impl.CellBinder;

/***********************************************************************************************************************
 *
 * A specialisation of {@link TextFieldTreeCell} which binds to an {@link As}-capable item.
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@Configurable
public class AsObjectTreeCell<T extends As> extends TextFieldTreeCell<T>
  {
    @Inject
    @VisibleForTesting CellBinder cellBinder;

    @Override
    public void updateItem (final @CheckForNull T item, final boolean empty)
      {
        super.updateItem(item, empty);
        cellBinder.bind(this, item, empty);
      }
  }
