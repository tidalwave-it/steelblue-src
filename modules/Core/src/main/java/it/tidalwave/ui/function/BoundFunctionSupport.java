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
package it.tidalwave.ui.function;

import java.beans.PropertyChangeSupport;
import lombok.experimental.Delegate;

/***************************************************************************************************************************************************************
 *
 * @since   2.0-ALPHA-1
 * @author  Fabrizio Giudici
 *
 **************************************************************************************************************************************************************/
public abstract class BoundFunctionSupport<DOMAIN_TYPE, CODOMAIN_TYPE>
                  implements BoundFunction<DOMAIN_TYPE, CODOMAIN_TYPE>
  {
    @Delegate // FIXME: weak
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    /***********************************************************************************************************************************************************
     * {@inheritDoc}
     **********************************************************************************************************************************************************/
    @Override
    public void unbindAll()
      {
        for (final var listener : pcs.getPropertyChangeListeners().clone())
          {
            pcs.removePropertyChangeListener(listener);
          }
      }

    protected void fireValueChanged (final CODOMAIN_TYPE oldValue, final CODOMAIN_TYPE newValue)
      {
        pcs.firePropertyChange("value", oldValue, newValue);
      }

    protected void fireValueChanged (final boolean oldValue, final boolean newValue)
      {
        pcs.firePropertyChange("value", oldValue, newValue);
      }
  }
