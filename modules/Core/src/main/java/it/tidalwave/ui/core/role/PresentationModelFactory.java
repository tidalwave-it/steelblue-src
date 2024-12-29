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
package it.tidalwave.ui.core.role;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Collections;
import it.tidalwave.util.RoleFactory;

/***************************************************************************************************************************************************************
 *
 * A factory that creates a default {@link PresentationModel}.
 *
 * @since   2.0-ALPHA-1
 * @author  Fabrizio Giudici
 *
 **************************************************************************************************************************************************************/
@FunctionalInterface
public interface PresentationModelFactory
  {
    public static final Class<PresentationModelFactory> _PresentationModelFactory_ = PresentationModelFactory.class;

    /***********************************************************************************************************************************************************
     * Creates a new instance of {@link PresentationModel} with some roles or role factories.
     *
     * @param  datum          the related datum
     * @param  roles          roles or {@link RoleFactory} instances to put in the presentation model
     * @return                the new instance
     * @since                 3.2-ALPHA-3 (refactored)
     **********************************************************************************************************************************************************/
    @Nonnull
    public PresentationModel createPresentationModel (@Nonnull Object datum, @Nonnull Collection<Object> roles);

    /***********************************************************************************************************************************************************
     * Creates a new instance of {@link PresentationModel}.
     *
     * @param  datum          the related datum
     * @return                the new instance
     * @since                 3.2-ALPHA-3
     **********************************************************************************************************************************************************/
    @Nonnull
    public default PresentationModel createPresentationModel (@Nonnull final Object datum)
      {
        return createPresentationModel(datum, Collections.emptyList());
      }
  }
