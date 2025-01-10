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
package it.tidalwave.ui.javafx.impl.common;

import jakarta.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import javafx.application.Platform;
import it.tidalwave.util.As;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import it.tidalwave.ui.core.role.UserAction;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import static it.tidalwave.ui.core.role.UserActionProvider._UserActionProvider_;
import static java.util.stream.Collectors.*;
import static it.tidalwave.util.ShortNames.*;

/***************************************************************************************************************************************************************
 *
 * This class collects a bag of roles from a source. It is used to preload roles in a generic thread before using them in a JavaFX thread.
 *
 * @author  Fabrizio Giudici
 *
 **************************************************************************************************************************************************************/
@Slf4j
public class RoleCollector
  {
    private final Map<Class<?>, List<Object>> roleMapByTipe = new HashMap<>();

    /** The default user action, which is the first action of the first {@link it.tidalwave.ui.core.role.UserActionProvider}. */
    @Getter @Nonnull
    private final Optional<UserAction> defaultUserAction;

    /***********************************************************************************************************************************************************
     *
     **********************************************************************************************************************************************************/
    public RoleCollector()
      {
        defaultUserAction = Optional.empty();
      }

    /***********************************************************************************************************************************************************
     * Creates a new instances for the given source and role types.
     * @param   source        the source
     * @param   roleTypes     the types of roles to collect
     **********************************************************************************************************************************************************/
    @SuppressFBWarnings("CT_CONSTRUCTOR_THROW")
    public RoleCollector (@Nonnull final As source, @Nonnull final Collection<Class<?>> roleTypes)
      {
        if (Platform.isFxApplicationThread())
          {
            throw new IllegalStateException("RoleCollector must not be instantiated in the JavaFX thread");
          }

        if (log.isTraceEnabled())
          {
            log.trace("ctor({}, {})", source, shortNames(roleTypes));
          }

        roleTypes.forEach(roleType -> copyRoles(source, roleType));
        // TODO: perhaps it could be associated to a dummy key, instead of being returned by a getter?
        defaultUserAction = getMany(_UserActionProvider_).stream().flatMap(a -> a.getOptionalDefaultAction().stream()).findFirst();
      }

    /***********************************************************************************************************************************************************
     * Adds a role. This method is useful for tests.
     * @param   roleType      the type of the role
     * @param   role          the role
     **********************************************************************************************************************************************************/
    public <R> void put (@Nonnull final Class<R> roleType, @Nonnull final R role)
      {
        putMany(roleType, List.of(role));
      }

    /***********************************************************************************************************************************************************
     * Adds roles. This method is useful for tests.
     * @param   roleType      the type of the role
     * @param   roles         the roles
     **********************************************************************************************************************************************************/
    public <R> void putMany (@Nonnull final Class<R> roleType, @Nonnull final Collection<? extends R> roles)
      {
        roleMapByTipe.put(roleType, new ArrayList<>(roles));
      }

    /***********************************************************************************************************************************************************
     * {@return a role of thw given type, if present}. If multiple roles for the same type are present, the first is returned.
     * @param   roleType      the type of the role
     **********************************************************************************************************************************************************/
    @Nonnull
    public <R> Optional<R> get (@Nonnull final Class<R> roleType)
      {
        return getMany(roleType).stream().findFirst();
      }

    /***********************************************************************************************************************************************************
     * {@return multiple roles of thw given type}.
     * @param   roleType      the type of the role
     **********************************************************************************************************************************************************/
    @Nonnull @SuppressWarnings("unchecked")
    public <R> List<R> getMany (@Nonnull final Class<R> roleType)
      {
        return new CopyOnWriteArrayList<>((List<R>)roleMapByTipe.getOrDefault(roleType, List.of()));
      }

    /***********************************************************************************************************************************************************
     * {@inheritDoc}
     **********************************************************************************************************************************************************/
    @Nonnull
    public String toString()
      {
        return roleMapByTipe.entrySet().stream().map(this::toString).collect(joining(", ", "RoleCollector(", ")"));
      }

    /***********************************************************************************************************************************************************
     *
     **********************************************************************************************************************************************************/
    @Nonnull
    private String toString (@Nonnull final Map.Entry<Class<?>, List<Object>> entry)
      {
        return shortName(entry.getKey()) + ": " + shortIds(entry.getValue());
      }

    /***********************************************************************************************************************************************************
     * Copy roles of the given type.
     * @param   source        the source
     * @param   roleType      the type of roles to collect
     **********************************************************************************************************************************************************/
    private <R> void copyRoles (@Nonnull final As source, @Nonnull final Class<R> roleType)
      {
        putMany(roleType, source.asMany(roleType));
      }
  }
