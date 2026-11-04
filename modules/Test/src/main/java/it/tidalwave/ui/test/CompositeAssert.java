/*
 * *************************************************************************************************************************************************************
 *
 * SteelBlue: DCI User Interfaces
 * http://tidalwave.it/projects/steelblue
 *
 * Copyright (C) 2015 - 2026 by Tidalwave s.a.s. (http://tidalwave.it)
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
package it.tidalwave.ui.test;
import jakarta.annotation.Nonnull;
import org.apiguardian.api.API;
import it.tidalwave.util.As;
import it.tidalwave.role.SimpleComposite;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import static org.assertj.core.api.Assertions.assertThat;
import static lombok.AccessLevel.PROTECTED;
/***************************************************************************************************************************************************************
 *
 * Assertions for the {@link it.tidalwave.role.Composite} role.
 * @since   3.0-ALPHA-3
 * @author  Fabrizio Giudici
 *
 **************************************************************************************************************************************************************/
@API(status = API.Status.EXPERIMENTAL)
@RequiredArgsConstructor(staticName = "of", access = PROTECTED) @ToString
public class CompositeAssert<T extends As>
  {
    /** The type of the composite. */
    @Nonnull
    protected final Class<T> compositeType;
    /** The Composite under inspection. */
    @Nonnull
    protected final SimpleComposite<T> composite;
    /***********************************************************************************************************************************************************
     * Asserts that the object under inspection has the given number of items.
     * @param   count           the expected count of children
     * @return                  an object for fluent further assertions
     **********************************************************************************************************************************************************/
    @Nonnull
    public ItemsAssert<T> withItemCount (final int count)
      {
        final var children = composite.findChildren().results();
        assertThat(children).hasSize(count);
        return ItemsAssert.of(compositeType, children);
      }
  }
