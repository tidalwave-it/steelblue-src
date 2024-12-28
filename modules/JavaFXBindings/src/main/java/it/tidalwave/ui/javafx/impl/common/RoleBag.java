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
package it.tidalwave.ui.javafx.impl.common;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import it.tidalwave.util.As;
import it.tidalwave.role.ui.UserAction;
import lombok.Getter;
import lombok.ToString;
import static java.util.Collections.*;
import static it.tidalwave.role.ui.UserActionProvider._UserActionProvider_;

/***************************************************************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **************************************************************************************************************************************************************/
@ToString
public class RoleBag
  {
    private final Map<Class<?>, List<Object>> map = new HashMap<>();

    /** The default user action, which is the first action of the first {@link it.tidalwave.role.ui.UserActionProvider}. */
    @Getter
    private final Optional<UserAction> defaultUserAction;

    public RoleBag()
      {
        defaultUserAction = Optional.empty();
      }

    public RoleBag (@Nonnull final As source, @Nonnull final Collection<Class<?>> roleTypes)
      {
        // TODO: assert not FX thread
        roleTypes.forEach(roleType -> copyRoles(source, roleType));
        // computed NOW because we are in the background thread
        // TODO: perhaps it could be associated to a dummy key, instead of being returned by a getter?
        defaultUserAction = getMany(_UserActionProvider_).stream().flatMap(a -> a.getOptionalDefaultAction().stream()).findFirst();
      }

    public <ROLE_TYPE> void put (@Nonnull final Class<ROLE_TYPE> roleClass, @Nonnull final ROLE_TYPE role)
      {
        putMany(roleClass, singletonList(role));
      }

    public <ROLE_TYPE> void putMany (@Nonnull final Class<ROLE_TYPE> roleClass,
                                     @Nonnull final Collection<? extends ROLE_TYPE> roles)
      {
        map.put(roleClass, new ArrayList<>(roles));
      }

    @Nonnull
    public <ROLE_TYPE> Optional<ROLE_TYPE> get (@Nonnull final Class<ROLE_TYPE> roleClass)
      {
        return getMany(roleClass).stream().findFirst();
      }

    @Nonnull
    public <ROLE_TYPE> List<ROLE_TYPE> getMany (@Nonnull final Class<ROLE_TYPE> roleClass)
      {
        return unmodifiableList((List<ROLE_TYPE>)map.getOrDefault(roleClass, emptyList()));
      }


    private <ROLE_TYPE> void copyRoles (@Nonnull final As item, @Nonnull final Class<ROLE_TYPE> roleClass)
      {
        putMany(roleClass, item.asMany(roleClass));
      }
  }
