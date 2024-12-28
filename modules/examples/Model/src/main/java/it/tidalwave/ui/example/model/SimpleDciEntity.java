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
package it.tidalwave.ui.example.model;

import javax.annotation.Nonnull;
import it.tidalwave.util.As;
import it.tidalwave.role.ui.Displayable;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Delegate;

/***************************************************************************************************************************************************************
 *
 * A simple datum class with a DCI role: {@link Displayable}, which contains the entity display name,
 *
 * @stereotype  Datum
 *
 * @author  Fabrizio Giudici
 *
 **************************************************************************************************************************************************************/
// START SNIPPET: entity
@Getter @ToString(exclude = "asDelegate")
public class SimpleDciEntity implements As
  {
    @Delegate
    private final As asDelegate;

    @Nonnull
    private final String name;

    private final int attribute1;

    private final int attribute2;

    public SimpleDciEntity (@Nonnull final String id, final int attribute1, final int attribute2)
      {
        this.name = id;
        this.attribute1 = attribute1;
        this.attribute2 = attribute2;
        asDelegate = As.forObject(this, Displayable.of(name));
      }
  }
// END SNIPPET: entity
