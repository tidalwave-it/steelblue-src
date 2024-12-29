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
package it.tidalwave.ui.core.role.impl;

import javax.annotation.Nonnull;
import java.util.Collection;
import it.tidalwave.util.As;
import it.tidalwave.util.Callback;
import it.tidalwave.ui.core.BoundProperty;
import it.tidalwave.ui.core.role.UserAction;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.Delegate;
import lombok.extern.slf4j.Slf4j;
import static it.tidalwave.ui.core.role.Displayable._Displayable_;

/***************************************************************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **************************************************************************************************************************************************************/
@RequiredArgsConstructor(staticName = "withCallback") @Slf4j
public class DefaultUserAction implements UserAction
  {
    @Getter @Accessors(fluent = true)
    private final BoundProperty<Boolean> enabled = new BoundProperty<>(true);

    @Delegate @Nonnull
    private final As as;

    @Nonnull
    private final Callback callback;

    /***********************************************************************************************************************************************************
     * @since 3.2-ALPHA-1 (was previously in {@code UserAction8}
     * @since  3.2-ALPHA-3 (refactored)
     **********************************************************************************************************************************************************/
    public DefaultUserAction (@Nonnull final Callback callback, @Nonnull final Collection<Object> roles)
      {
        this.callback = callback;
        this.as = As.forObject(this, roles);
      }

    /***********************************************************************************************************************************************************
     * @since 3.2-ALPHA-1 (was previously in {@code UserAction8}
     * @since  3.2-ALPHA-3 (refactored)
     **********************************************************************************************************************************************************/
    @Nonnull
    public DefaultUserAction withRoles (@Nonnull final Collection<Object> roles)
      {
        return new DefaultUserAction(callback, roles);
      }

    @Override
    public void actionPerformed() // FIXME: change with composition
      {
        try
          {
            callback.call();
          }
        catch (Throwable e)
          {
            log.error("", e);
          }
      }

    /***********************************************************************************************************************************************************
     * {@inheritDoc}
     **********************************************************************************************************************************************************/
    @Override @Nonnull
    public String toString ()
      {
        final var id = "@" + Integer.toHexString(System.identityHashCode(this));
        var details = id;

        try
          {
            details = this.maybeAs(_Displayable_)
                          .map(d -> String.format("[\"%s\"]", d.getDisplayName()))
                          .orElse(id);
          }
        catch (RuntimeException e)
          {
            details += ",broken";
            log.error("In DefaultDisplayable.toString()", e);
          }

        return String.format("DefaultUserAction%s", details);
      }
  }
