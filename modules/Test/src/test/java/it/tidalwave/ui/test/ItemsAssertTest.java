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
import java.util.List;
import java.util.function.Consumer;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ThrowingConsumer;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
/***************************************************************************************************************************************************************
 *
 * Assertions for children elements.
 * @since   3.0-ALPHA-3
 * @author  Fabrizio Giudici
 *
 **************************************************************************************************************************************************************/
public class ItemsAssertTest
  {
    private Mock mock1;
    private Mock mock2;
    private Mock mock3;
    private List<Mock> items;
    /**********************************************************************************************************************************************************/
    @BeforeMethod
    public void setup()
      {
        mock1 = Mock.newInstance("mock1");
        mock2 = Mock.newInstance("mock2");
        mock3 = Mock.newInstance("mock3");
        items = List.of(mock1, mock2, mock3);
      }
    /**********************************************************************************************************************************************************/
    @Test @SuppressWarnings("unchecked")
    public void test_withEachItem()
      {
        // given
        final var underTest = ItemsAssert.of(Mock.class, items);
        final var consumer = mock(ThrowingConsumer.class);
        // when
        final var fluent = underTest.withEachItem(consumer);
        // then
        final var inOrder = inOrder(consumer);
        inOrder.verify(consumer).accept(same(mock1));
        inOrder.verify(consumer).accept(same(mock2));
        inOrder.verify(consumer).accept(same(mock3));
        assertThat(fluent.itemType).isEqualTo(Mock.class);
        assertThat(fluent.items).containsExactly(mock1, mock2, mock3);
        assertThat(fluent.nextItemSatisfyingCalled).isFalse();
      }
    /**********************************************************************************************************************************************************/
    @Test @SuppressWarnings("unchecked")
    public void test_withEachItemSatisfying()
      {
        // given
        final var underTest = ItemsAssert.of(Mock.class, items);
        final var consumer = mock(ThrowingConsumer.class);
        // when
        final var fluent = underTest.withEachItemSatisfying(consumer);
        // then
        final var captor = ArgumentCaptor.forClass(AsAssert.class);
        verify(consumer, times(3)).accept(captor.capture());
        final var values = captor.getAllValues().toArray(AsAssert[]::new);
        assertThat(values[0].object).isSameAs(mock1);
        assertThat(values[1].object).isSameAs(mock2);
        assertThat(values[2].object).isSameAs(mock3);
        assertThat(fluent.itemType).isEqualTo(Mock.class);
        assertThat(fluent.items).containsExactly(mock1, mock2, mock3);
        assertThat(fluent.nextItemSatisfyingCalled).isFalse();
      }
    /**********************************************************************************************************************************************************/
    @Test @SuppressWarnings("unchecked")
    public void test_withAll()
      {
        // given
        final var underTest = ItemsAssert.of(Mock.class, items);
        final var consumer = mock(Consumer.class);
        // when
        final var fluent = underTest.withAll(consumer);
        // then
        verify(consumer).accept(new Mock[] { mock1, mock2, mock3 });
        assertThat(fluent.itemType).isEqualTo(Mock.class);
        assertThat(fluent.items).containsExactly(mock1, mock2, mock3);
        assertThat(fluent.nextItemSatisfyingCalled).isFalse();
      }
    /**********************************************************************************************************************************************************/
    @Test @SuppressWarnings("unchecked")
    public void test_withNextItemSatisfying()
      {
        // given
        final var underTest = ItemsAssert.of(Mock.class, items);
        final var consumer = mock(ThrowingConsumer.class);
        // when
        final var fluent = underTest.withNextItemSatisfying(consumer);
        // then
        final var captor = ArgumentCaptor.forClass(AsAssert.class);
        verify(consumer).accept(captor.capture());
        assertThat(captor.getValue().object).isSameAs(mock1);
        assertThat(fluent.itemType).isEqualTo(Mock.class);
        assertThat(fluent.items).containsExactly(mock2, mock3);
        assertThat(fluent.nextItemSatisfyingCalled).isTrue();
      }
    /**********************************************************************************************************************************************************/
    @Test @SuppressWarnings("unchecked")
    public void test_proper_invocation_order()
      {
        // given
        final var underTest = ItemsAssert.of(Mock.class, items);
        final var consumer = mock(ThrowingConsumer.class);
        // when
        underTest.withEachItemSatisfying(consumer)
                 .withNextItemSatisfying(consumer);
        // then
      }
    /**********************************************************************************************************************************************************/
    @Test(expectedExceptions = IllegalStateException.class,
            expectedExceptionsMessageRegExp = "Move withEachItemSatisfying\\(\\) before calls to withNextItemSatisfying\\(\\) because items have been consumed")
    @SuppressWarnings("unchecked")
    public void test_wrong_invocation_order()
      {
        // given
        final var underTest = ItemsAssert.of(Mock.class, items);
        final var consumer = mock(ThrowingConsumer.class);
        // when
        underTest.withNextItemSatisfying(consumer)
                 .withEachItemSatisfying(consumer);
        // then
      }
  }
