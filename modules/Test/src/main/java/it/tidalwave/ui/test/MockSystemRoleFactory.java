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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import org.apiguardian.api.API;
import it.tidalwave.util.Pair;
import it.tidalwave.role.spi.SystemRoleFactory;
import lombok.RequiredArgsConstructor;
import static org.apiguardian.api.API.Status.EXPERIMENTAL;
import static java.util.stream.Collectors.*;
import static lombok.AccessLevel.PRIVATE;

/***************************************************************************************************************************************************************
 *
 * A mock implementation of {@link SystemRoleFactory} for tests. It should be created by passing triples that associate a class type, a role type and a
 * factory function:
 * <pre>
 * {@code
 * final var rf = MockSystemRoleFactory.newInstance()
 *                                     .with(Datum.class, Role1.class, o -> List.of(new MockRole1a(o), new MockRole2(o)))
 *                                     .with(Datum.class, Role2.class, o -> List.of(new MockRole3(o)));
 * }
 * </pre>
 *
 * The function argument is the owner of the roles.
 * This class must be installed before each test run (e.g. in a method annotated with {@code BeforeMethod}):
 *
 * <pre>
 * {@code
 * SystemRoleFactory.reset();
 * SystemRoleFactory.set(MockSystemRoleFactory.newInstance().with(...));
 * }
 * </pre>
 * @author  Fabrizio Giudici
 *
 **************************************************************************************************************************************************************/
@API(status = EXPERIMENTAL)
@RequiredArgsConstructor(staticName = "of", access = PRIVATE)
public final class MockSystemRoleFactory implements SystemRoleFactory
  {
    @Nonnull
    private final Map<Pair<Class<?>, Class<?>>, Function<Object, List<?>>> rolesByType;

    /***********************************************************************************************************************************************************
     * {@return a new instance of the factory}.
     **********************************************************************************************************************************************************/
    @Nonnull
    public static MockSystemRoleFactory newInstance()
      {
        return of(Map.of());
      }

    /***********************************************************************************************************************************************************
     * {@inheritDoc}
     **********************************************************************************************************************************************************/
    @Override @Nonnull @SuppressWarnings("unchecked")
    public <R> List<R> findRoles (@Nonnull final Object owner, @Nonnull final Class<? extends R> roleType)
      {
        return (List<R>)rolesByType.entrySet().stream()
                                   .filter(e -> e.getKey().a.isAssignableFrom(owner.getClass()))
                                   .filter(e -> e.getKey().b.isAssignableFrom(roleType))
                                   .flatMap(e -> e.getValue().apply(owner).stream())
                                   .collect(toList());
      }

    /***********************************************************************************************************************************************************
     * {@return a new factory that supports the given owner and role type}.
     * @param   <T>             the static type of the owner
     * @param   <R>             the static type of the role
     * @param   ownerType       the type of the owner
     * @param   roleType        the type of the role
     * @param   roleFactory     a factory function that instantiates the roles
     **********************************************************************************************************************************************************/
    @Nonnull
    public <T, R> MockSystemRoleFactory with (@Nonnull final Class<T> ownerType,
                                              @Nonnull final Class<R> roleType,
                                              @Nonnull final Function<T, List<R>> roleFactory)
      {
        final var map = new HashMap<>(rolesByType);
        map.put(Pair.of(ownerType, roleType), (Function<Object, List<?>>)(Function)roleFactory);
        return of(map);
      }
  }
