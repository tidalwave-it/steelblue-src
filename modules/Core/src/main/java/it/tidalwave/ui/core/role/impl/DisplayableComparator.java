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
package it.tidalwave.ui.core.role.impl;

import javax.annotation.Nonnull;
import java.text.Collator;
import java.util.Comparator;
import it.tidalwave.ui.core.role.Displayable;

/***************************************************************************************************************************************************************
 *
 * A {@link Comparator} for classes implementing the {@link Displayable} role.
 * FIXME: don't use it, it requires the objects to statically implement Displayable
 *
 * @author  Fabrizio Giudici
 * @it.tidalwave.javadoc.draft Will be removed (FIXME)
 *
 **************************************************************************************************************************************************************/
public final class DisplayableComparator implements Comparator<Displayable>
  {
    private static final DisplayableComparator INSTANCE = new DisplayableComparator();

    @Nonnull
    public static DisplayableComparator getInstance()
      {
        return INSTANCE;
      }

    @Override
    public int compare (@Nonnull final Displayable d1, @Nonnull final Displayable d2)
      {
        return Collator.getInstance().compare(d1.getDisplayName(), d2.getDisplayName());
      }
  }
