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

import javax.annotation.Nonnull;
import it.tidalwave.ui.core.role.Changeable;
import it.tidalwave.ui.core.ChangingSource;
import lombok.extern.slf4j.Slf4j;

/***************************************************************************************************************************************************************
 *
 * @since   2.0-ALPHA-1
 * Changes the destination only at a certain condition in function of the target.
 *
 * @author  Fabrizio Giudici
 *
 **************************************************************************************************************************************************************/
@Slf4j
public abstract class WeakCopyFunctionSupport<T> extends UnaryBoundFunctionSupport<T, T> implements Changeable<T>
  {
    @Nonnull
    protected T targetValue;

    public WeakCopyFunctionSupport (@Nonnull final ChangingSource<T> source)
      {
        super(source);
      }

    @Override
    protected void onSourceChange (@Nonnull final T oldSourceValue, @Nonnull final T newSourceValue)
      {
        final var oldValue = function(oldSourceValue);
        final var newValue = function(newSourceValue);

        if (shouldChange(oldValue, newValue))
          {
            value = newValue;
            fireValueChanged(oldValue, newValue);
          }
      }

    @Override
    public void set (final T value)
      {
        this.targetValue = value;
      }

    protected abstract boolean shouldChange (T oldValue, T newValue);

    @Override @Nonnull
    protected T function (@Nonnull final T value)
      {
        return value;
      }
  }
