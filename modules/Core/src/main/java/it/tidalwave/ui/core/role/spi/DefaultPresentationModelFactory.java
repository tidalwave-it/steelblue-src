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
package it.tidalwave.ui.core.role.spi;

import jakarta.annotation.Nonnull;
import java.util.Collection;
import it.tidalwave.ui.core.role.PresentationModel;
import it.tidalwave.ui.core.role.PresentationModelFactory;
import it.tidalwave.ui.core.role.impl.DefaultPresentationModel;

/***************************************************************************************************************************************************************
 *
 * An implementation of {@link PresentationModelFactory} that creates instances of {@link DefaultPresentationModel}.
 *
 * @since   2.0-ALPHA-1
 * @author  Fabrizio Giudici
 *
 **************************************************************************************************************************************************************/
public class DefaultPresentationModelFactory implements PresentationModelFactory
  {
    /***********************************************************************************************************************************************************
     * {@inheritDoc}
     **********************************************************************************************************************************************************/
    @Override @Nonnull
    public PresentationModel createPresentationModel (@Nonnull final Object owner,
                                                      @Nonnull final Collection<Object> localRoles)
      {
        return new DefaultPresentationModel(owner, localRoles);
      }
  }