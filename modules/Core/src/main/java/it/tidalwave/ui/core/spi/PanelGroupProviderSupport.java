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
import it.tidalwave.util.LazySupplier;
import lombok.Getter;
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
    @Getter @Nonnull
    private final Group group;

    @Getter @Nonnull
    private final String label;

    @Nonnull
    private final LazySupplier<T> lazySupplier;

    /***********************************************************************************************************************************************************
     * Creates a new instance with the given parameter.
     * @param   group       the group
     * @param   label       the label of the provided object
     * @param   supplier    the supplier of the UI control
     **********************************************************************************************************************************************************/
    protected PanelGroupProviderSupport (@Nonnull final Group group, @Nonnull final String label, @Nonnull final Supplier<T> supplier)
      {
        this.group = group;
        this.label = label;
        this.lazySupplier = LazySupplier.of(supplier);
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
