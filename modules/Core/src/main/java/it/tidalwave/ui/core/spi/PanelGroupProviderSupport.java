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
package it.tidalwave.ui.core.spi;
import jakarta.annotation.Nonnull;
import java.util.function.Supplier;
import it.tidalwave.ui.core.PanelGroupControl.Group;
import it.tidalwave.ui.core.PanelGroupProvider;
import org.apiguardian.api.API;
import it.tidalwave.util.BundleUtilities;
import it.tidalwave.util.LazySupplier;
import lombok.Builder;
import lombok.Getter;
import static it.tidalwave.ui.core.PanelGroupControl.DefaultGroups.LEFT;
import static org.apiguardian.api.API.Status.EXPERIMENTAL;
/***************************************************************************************************************************************************************
 *
 * A support class for {@link PanelGroupProvider}. Most implementation will just need to subclass and provide the proper parameters to the constructor.
 *
 * @param <T>   the concrete type of the UI control
 * @since       2.0-ALPHA-3
 * @author      Fabrizio Giudici
 *
 **************************************************************************************************************************************************************/
@API(status = EXPERIMENTAL)
public abstract class PanelGroupProviderSupport<T> implements PanelGroupProvider<T>
  {
    /***********************************************************************************************************************************************************
     * Constructor parameters for {@link PanelGroupProviderSupport}.
     * @param <T>   the concrete type of the UI control
     * @since       3.0-ALPHA-5
     **********************************************************************************************************************************************************/
    @Builder @Getter
    public static class Parameters<T>
      {
        /** The group. */
        @Nonnull @Builder.Default
        private final Group group = LEFT;
        /** The label. */
        @Nonnull @Builder.Default
        private final String label = "";
        /** The presentation object. */
        @Nonnull @Builder.Default
        private final Object presentation = new Object();
        /** The supplier of the UI control */
        @Nonnull @Builder.Default
        private final Supplier<T> supplier = (Supplier<T>)(Object)DUMMY_SUPPLIER;
        /*******************************************************************************************************************************************************
         * Customisation of the Lombok-generated builder.
         ******************************************************************************************************************************************************/
        public static class ParametersBuilder<T>
          {
            /***************************************************************************************************************************************************
             * Sets a localised label.
             * @param   clazz     the owner of the bundle
             * @param   key       the key of the resource inside the bundle
             * @param   params    the parameters (used if the string in the bundle is a {@code String} format)
             **************************************************************************************************************************************************/
            @Nonnull
            public ParametersBuilder<T> labelFromBundle (@Nonnull final Class<?> clazz, @Nonnull final String key, @Nonnull final Object... params)
              {
                return label(BundleUtilities.getMessage(clazz, key, params));
              }
          }
      }
    private static final Supplier<?> DUMMY_SUPPLIER = Object::new;
    @Getter @Nonnull
    private final Group group;
    @Getter @Nonnull
    private final String label;
    @Getter @Nonnull
    private final Object presentation;
    @Nonnull
    private final LazySupplier<T> lazySupplier;
    /***********************************************************************************************************************************************************
     * {@return a new parameter builder}
     * @param <T>   the concrete type of the UI control
     * @see         Parameters
     * @since       3.0-ALPHA-5
     **********************************************************************************************************************************************************/
    @Nonnull
    public static <T> Parameters.ParametersBuilder<T> parameters()
      {
        return PanelGroupProviderSupport.Parameters.builder();
      }
    /***********************************************************************************************************************************************************
     * Creates a new instance with the given parameters.
     * @param       parametersBuilder    the parameters' builder
     * @since       3.0-ALPHA-5
     **********************************************************************************************************************************************************/
    protected PanelGroupProviderSupport (@Nonnull final Parameters.ParametersBuilder<T> parametersBuilder)
      {
        final var parameters = parametersBuilder.build();
        if (parameters.getSupplier() == DUMMY_SUPPLIER)
          {
            throw new IllegalArgumentException("You did not specify a supplier");
          }
        this.group = parameters.getGroup();
        this.label = parameters.getLabel();
        this.presentation = parameters.getPresentation();
        this.lazySupplier = LazySupplier.of(parameters.getSupplier());
      }
    /***********************************************************************************************************************************************************
     * {@inheritDoc}
     **********************************************************************************************************************************************************/
    @Override @Nonnull
    public T getComponent ()
      {
        return lazySupplier.get();
      }
  }
