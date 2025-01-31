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

import java.lang.reflect.Array;
import jakarta.annotation.Nonnull;
import java.util.List;
import java.util.function.Consumer;
import org.apiguardian.api.API;
import it.tidalwave.util.As;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.mockito.ThrowingConsumer;
import static it.tidalwave.ui.test.AsAssert.assertThat;
import static lombok.AccessLevel.PRIVATE;

/***************************************************************************************************************************************************************
 *
 * Assertions for items of {@link it.tidalwave.role.Composite} and {@link it.tidalwave.role.Aggregate}.
 * @since   3.0-ALPHA-3
 * @author  Fabrizio Giudici
 *
 **************************************************************************************************************************************************************/
@API(status = API.Status.EXPERIMENTAL)
@RequiredArgsConstructor(staticName = "of", access = PRIVATE) @ToString
public class ItemsAssert<T extends As>
  {
    /** The type of items. */
    @Nonnull
    protected final Class<T> itemType;

    /** The items. */
    @Nonnull
    protected final List<T> items;

    /** Whether {@link #withNextItemSatisfying(ThrowingConsumer)} has been already called in the current chain. */
    protected final boolean nextItemSatisfyingCalled;

    /***********************************************************************************************************************************************************
     *
     **********************************************************************************************************************************************************/
    @Nonnull
    protected static <U extends As> ItemsAssert<U> of (@Nonnull final Class<U> itemType, @Nonnull final List<U> items)
      {
        return of(itemType, items, false);
      }

    /***********************************************************************************************************************************************************
     * Enumerates all children of the current object so assertions can be enforced on them.
     * This method can be used in a chain that includes one or multiple calls to {@link #withNextItemSatisfying(ThrowingConsumer)}, but only before them.
     * @param   consumer        the consumer of children
     * @return                  an object for fluent further assertions
     **********************************************************************************************************************************************************/
    @Nonnull
    public ItemsAssert<T> withEachItem (@Nonnull final ThrowingConsumer<T> consumer)
      {
        enforceBeforeWithNextItem("withEachChild");
        items.forEach(consumer);
        return new ItemsAssert<>(itemType, items, nextItemSatisfyingCalled);
      }

    /***********************************************************************************************************************************************************
     * Enumerates all children of the current object so assertions can be enforced on them.
     * This method can be used in a chain that includes one or multiple calls to {@link #withNextItemSatisfying(ThrowingConsumer)}, but only before them.
     * @param   consumer        the consumer of children
     * @return                  an object for fluent further assertions
     **********************************************************************************************************************************************************/
    @Nonnull
    public ItemsAssert<T> withEachItemSatisfying (@Nonnull final ThrowingConsumer<AsAssert<?>> consumer)
      {
        enforceBeforeWithNextItem("withEachItemSatisfying");
        items.forEach(i -> consumer.accept(assertThat(i)));
        return new ItemsAssert<>(itemType, items, nextItemSatisfyingCalled);
      }

    /***********************************************************************************************************************************************************
     * Provides all children of the current object so assertions can be enforced on them.
     * This method can be used in a chain that includes one or multiple calls to {@link #withNextItemSatisfying(ThrowingConsumer)}, but only before them.
     * @param   consumer        the consumer of children
     * @return                  an object for fluent further assertions
     **********************************************************************************************************************************************************/
    @Nonnull
    @SuppressWarnings("unchecked")
    public ItemsAssert<T> withAll (@Nonnull final Consumer<T[]> consumer)
      {
        enforceBeforeWithNextItem("withAll");
        consumer.accept(items.toArray((T[])Array.newInstance(itemType, items.size())));
        return new ItemsAssert<>(itemType, items, nextItemSatisfyingCalled);
      }

    /***********************************************************************************************************************************************************
     * Provides the next child of this object so assertions can be enforced on them. Further chained calls of this method will provide further children.
     * @param   consumer        the consumer of the child
     * @return                  an object for fluent further assertions
     **********************************************************************************************************************************************************/
    @Nonnull
    public ItemsAssert<T> withNextItemSatisfying (@Nonnull final ThrowingConsumer<AsAssert<?>> consumer)
      {
        consumer.accept(assertThat(items.get(0)));
        return new ItemsAssert<>(itemType, items.subList(1, items.size()), true);
      }

    /***********************************************************************************************************************************************************
     *
     **********************************************************************************************************************************************************/
    private void enforceBeforeWithNextItem (@Nonnull final String methodName)
      {
        if (nextItemSatisfyingCalled)
          {
            throw new IllegalStateException(String.format("Move %s() before calls to withNextItemSatisfying() because items have been consumed", methodName));
          }
      }
  }
