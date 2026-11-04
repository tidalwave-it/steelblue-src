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
package it.tidalwave.ui.test;
import jakarta.annotation.Nonnull;
import java.util.stream.Stream;
import org.apiguardian.api.API;
import it.tidalwave.util.As;
import it.tidalwave.role.Aggregate;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import static org.assertj.core.api.Assertions.assertThat;
import static java.util.stream.Collectors.*;
import static lombok.AccessLevel.PROTECTED;
/***************************************************************************************************************************************************************
 *
 * Assertions for the {@link Aggregate} role.
 * @since   3.0-ALPHA-3
 * @author  Fabrizio Giudici
 *
 **************************************************************************************************************************************************************/
@API(status = API.Status.EXPERIMENTAL)
@RequiredArgsConstructor(staticName = "of", access = PROTECTED) @ToString
public class AggregateAssert<T extends As>
  {
    /** The type of the aggregate. */
    @Nonnull
    protected final Class<T> aggregateType;
    /** The Aggregate under inspection. */
    @Nonnull
    protected final Aggregate<T> aggregate;
    /***********************************************************************************************************************************************************
     * Asserts that the {@link Aggregate} under test contains all the items whose names are provided.
     * @param    names           the names
     * @return                   an object for fluent assertions
     **********************************************************************************************************************************************************/
    @Nonnull
    public ItemsAssert<T> withExactItemNames (@Nonnull final String... names)
      {
        assertThat(aggregate.getNames()).containsExactlyInAnyOrder(names);
        return ItemsAssert.of(aggregateType, Stream.of(names).map(n -> aggregate.getByName(n).orElseThrow()).collect(toList()));
      }
  }
