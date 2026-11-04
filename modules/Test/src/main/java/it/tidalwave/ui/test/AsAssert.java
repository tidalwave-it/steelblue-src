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
import org.springframework.util.function.ThrowingSupplier;
import it.tidalwave.ui.core.BoundProperty;
import org.apiguardian.api.API;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ObjectAssert;
import it.tidalwave.util.As;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.mockito.ArgumentCaptor;
import static it.tidalwave.ui.core.role.Displayable._Displayable_;
import static it.tidalwave.ui.core.role.Styleable._Styleable_;
import static it.tidalwave.ui.core.role.UserActionProvider._UserActionProvider_;
import static it.tidalwave.role.Aggregate._Aggregate_;
import static it.tidalwave.role.SimpleComposite._SimpleComposite_;
import static lombok.AccessLevel.PROTECTED;
/***************************************************************************************************************************************************************
 *
 * Assertions for UI core.
 * @since   3.0-ALPHA-3
 * @author  Fabrizio Giudici
 *
 **************************************************************************************************************************************************************/
@API(status = API.Status.EXPERIMENTAL)
@RequiredArgsConstructor(staticName = "of", access = PROTECTED) @ToString
public class AsAssert<T extends As>
  {
    /** The object under inspection. */
    @Nonnull
    protected final T object;
    /***********************************************************************************************************************************************************
     * Starts a sequence of assertions on the given object.
     * @param   object          the object
     * @return                  an object for fluent assertions
     **********************************************************************************************************************************************************/
    @Nonnull
    public static AsAssert<As> assertThat (@Nonnull final As object)
      {
        return new AsAssert<>(object);
      }
    /***********************************************************************************************************************************************************
     * Starts a sequence of assertions on the given object.
     * @param   captor          the object, provided as a mockito {@link ArgumentCaptor}
     * @return                  an object for fluent assertions
     **********************************************************************************************************************************************************/
    @Nonnull
    public static AsAssert<As> assertThat (@Nonnull final ArgumentCaptor<? extends As> captor)
      {
        return new AsAssert<>(captor.getValue());
      }
    /***********************************************************************************************************************************************************
     * Starts a sequence of assertions on the given property.
     * @param   <T>             the static type of the property
     * @param   property        the property
     * @return                  an object for fluent assertions
     **********************************************************************************************************************************************************/
    @Nonnull
    public static <T> ObjectAssert<T> assertThat (@Nonnull final BoundProperty<T> property)
      {
        Assertions.assertThat(property).isNotNull();
        return Assertions.assertThat(property.get());
      }
    /***********************************************************************************************************************************************************
     * Asserts that the object under inspection has a {@link it.tidalwave.ui.core.role.Displayable} role.
     * @return                  an object for fluent further assertions
     **********************************************************************************************************************************************************/
    @Nonnull
    public DisplayableAssert hasDisplayable()
      {
        return DisplayableAssert.of(getRole(As.type(_Displayable_)));
      }
    /***********************************************************************************************************************************************************
     * Asserts that the object under inspection has the given display name.
     * @param   displayName     the display name
     * @return                  an object for fluent further assertions
     **********************************************************************************************************************************************************/
    @Nonnull
    public AsAssert<T> hasDisplayName (@Nonnull final String displayName)
      {
        hasDisplayable().withDisplayName(displayName);
        return this;
      }
    /***********************************************************************************************************************************************************
     * Asserts that the object under inspection has the given display name.
     * @param   displayName     the display name
     * @return                  an object for fluent further assertions
     **********************************************************************************************************************************************************/
    @Nonnull
    public AsAssert<T> hasDisplayName (@Nonnull final ThrowingSupplier<String> displayName)
      {
        hasDisplayable().withDisplayName(displayName);
        return this;
      }
    /***********************************************************************************************************************************************************
     * Asserts that the object under inspection has a {@link it.tidalwave.ui.core.role.Styleable} role.
     * @return                  an object for fluent further assertions
     **********************************************************************************************************************************************************/
    @Nonnull
    public StyleableAssert hasStyleable()
      {
        return StyleableAssert.of(getRole(As.type(_Styleable_)));
      }
    /***********************************************************************************************************************************************************
     * Asserts that the object under inspection has a {@link it.tidalwave.role.SimpleComposite} role of the given type
     * @param   <C>             the static type of the composite
     * @param   compositeType   the type of the composite
     * @return                  an object for fluent further assertions
     **********************************************************************************************************************************************************/
    @Nonnull
    public <C extends As> CompositeAssert<C> hasCompositeOf (@Nonnull final Class<C> compositeType)
      {
        return CompositeAssert.of(compositeType, getRole(As.type(_SimpleComposite_)));
      }
    /***********************************************************************************************************************************************************
     * Asserts that the object under inspection has a {@link it.tidalwave.role.Aggregate} role of the given type
     * @param   <C>             the static type of the aggregate
     * @param   aggregateType   the type of the aggregate
     * @return                  an object for fluent further assertions
     **********************************************************************************************************************************************************/
    @Nonnull
    public <C extends As> AggregateAssert<C> hasAggregateOf (@Nonnull final Class<C> aggregateType)
      {
        return AggregateAssert.of(aggregateType, getRole(As.type(_Aggregate_)));
      }
    /***********************************************************************************************************************************************************
     * Asserts that the object under inspection has a {@link it.tidalwave.ui.core.role.UserActionProvider} role.
     * @return                  an object for fluent further assertions
     **********************************************************************************************************************************************************/
    @Nonnull
    public UserActionProviderAssert hasUserActionProvider()
      {
        return UserActionProviderAssert.of(getRole(As.type(_UserActionProvider_)));
      }
    /***********************************************************************************************************************************************************
     * Asserts that the object under inspection has the given role.
     * @param   <R>             the static type of the role
     * @param   roleType        the type of the role
     * @return                  an object for fluent further assertions
     **********************************************************************************************************************************************************/
    @Nonnull
    public <R> ObjectAssert<R> hasRole (@Nonnull final Class<R> roleType)
      {
        return hasRole(As.type(roleType));
      }
    /***********************************************************************************************************************************************************
     * Asserts that the object under inspection has the given role.
     * @param   <R>             the static type of the role
     * @param   roleType        the type of the role
     * @return                  an object for fluent further assertions
     **********************************************************************************************************************************************************/
    @Nonnull
    public <R> ObjectAssert<R> hasRole (@Nonnull final As.Type<R> roleType)
      {
        return Assertions.assertThat(getRole(roleType));
      }
    /***********************************************************************************************************************************************************
     *
     **********************************************************************************************************************************************************/
    @Nonnull
    private <R> R getRole (@Nonnull As.Type<R> roleType)
      {
        return object.maybeAs(roleType).orElseThrow(() -> new AssertionError(String.format("%s doesn't have role %s", object, roleType)));
      }
  }
