/*
 * *********************************************************************************************************************
 *
 * SteelBlue: DCI User Interfaces
 * http://tidalwave.it/projects/steelblue
 *
 * Copyright (C) 2015 - 2021 by Tidalwave s.a.s. (http://tidalwave.it)
 *
 * *********************************************************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * *********************************************************************************************************************
 *
 * git clone https://bitbucket.org/tidalwave/steelblue-src
 * git clone https://github.com/tidalwave-it/steelblue-src
 *
 * *********************************************************************************************************************
 */
package it.tidalwave.role.ui.javafx.impl.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import static java.util.stream.Collectors.toList;
import static it.tidalwave.role.spi.impl.LogUtil.*;

/***********************************************************************************************************************
 *
 * Just slightly adapted from http://www.artima.com/weblogs/viewpost.jsp?thread=208860
 *
 * TODO: Consider incorporating this in TFT.
 *
 * @author Fabrizio Giudici
 * @author based on code of Ian Robertson
 *
 **********************************************************************************************************************/
@Slf4j @UtilityClass
public class ReflectionUtils
  {
    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    public static void injectDependencies (@Nonnull final Object object, @Nonnull final Map<Class<?>, Object> beans)
      {
        for (final Field field : object.getClass().getDeclaredFields())
          {
            if (field.getAnnotation(Inject.class) != null)
              {
                field.setAccessible(true);
                final Class<?> type = field.getType();
                final Object dependency = beans.get(type);

                if (dependency == null)
                  {
                    throw new RuntimeException("Can't inject " + object + "." + field.getName());
                  }

                try
                  {
                    field.set(object, dependency);
                  }
                catch (IllegalArgumentException | IllegalAccessException e)
                  {
                    throw new RuntimeException(e);
                  }
              }
          }
      }

    /*******************************************************************************************************************
     *
     * Instantiates an object of the given class performing dependency injections through the constructor.
     *
     * @param <T>     the generic type of the object to instantiate
     * @param type    the dynamic type of the object to instantiate; it is expected to have a single constructor
     * @param beans   the pool of objects to instantiate
     * @return        the new instance
     * @throws        RuntimeException if something fails
     *
     ******************************************************************************************************************/
    public static <T> T instantiateWithDependencies (@Nonnull final Class<T> type,
                                                     @Nonnull final Map<Class<?>, Object> beans)
      {
        try
          {
            log.debug("instantiateWithDependencies({}, {})", shortName(type), shortIds(beans.values()));
            final Constructor<?>[] constructors = type.getConstructors();

            if (constructors.length > 1)
              {
                throw new RuntimeException("Multiple constructors in " + type);
              }

            final List<Object> parameters =
                    Arrays.stream(constructors[0].getParameterTypes()).map(beans::get).collect(toList());

            log.trace(">>>> ctor arguments: {}", shortIds(parameters));
            return type.cast(constructors[0].newInstance(parameters.toArray()));
          }
        catch (InstantiationException | IllegalAccessException | InvocationTargetException e)
          {
            throw new RuntimeException(e);
          }
      }

    /*******************************************************************************************************************
     *
     * Get the actual type arguments a child class has used to extend a generic base class.
     *
     * @param baseClass the base class
     * @param childClass the child class
     * @return a list of the raw classes for the actual type arguments.
     *
     ******************************************************************************************************************/
    public static <T> List<Class<?>> getTypeArguments (@Nonnull final Class<T> baseClass,
                                                       @Nonnull final Class<? extends T> childClass)
      {
        final Map<Type, Type> resolvedTypes = new HashMap<>();
        Type type = childClass;

        // start walking up the inheritance hierarchy until we hit baseClass
        while (!getClass(type).equals(baseClass))
          {
            if (type instanceof Class<?>)
              {
                // there is no useful information for us in raw types, so just keep going.
                type = ((Class<?>)type).getGenericSuperclass();
              }
            else
              {
                final ParameterizedType parameterizedType = (ParameterizedType) type;
                final Class<?> rawType = (Class) parameterizedType.getRawType();
                final Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                final TypeVariable<?>[] typeParameters = rawType.getTypeParameters();

                for (int i = 0; i < actualTypeArguments.length; i++)
                  {
                    resolvedTypes.put(typeParameters[i], actualTypeArguments[i]);
                  }

                if (!rawType.equals(baseClass))
                  {
                    type = rawType.getGenericSuperclass();
                  }
              }
          }

        // finally, for each actual type argument provided to baseClass, determine (if possible)
        // the raw class for that type argument.
        final Type[] actualTypeArguments;

        if (type instanceof Class)
          {
            actualTypeArguments = ((Class)type).getTypeParameters();
          }
        else
          {
            actualTypeArguments = ((ParameterizedType)type).getActualTypeArguments();
          }

        final List<Class<?>> typeArgumentsAsClasses = new ArrayList<>();
        // resolve types by chasing down type variables.
        for (Type baseType : actualTypeArguments)
          {
            while (resolvedTypes.containsKey(baseType))
              {
                baseType = resolvedTypes.get(baseType);
              }

            typeArgumentsAsClasses.add(getClass(baseType));
          }

        return typeArgumentsAsClasses;
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @CheckForNull
    public static Class<?> getClass (@Nonnull final Type type)
      {
        if (type instanceof Class<?>)
          {
            return (Class<?>)type;
          }
        else if (type instanceof ParameterizedType)
          {
            return getClass(((ParameterizedType)type).getRawType());
          }
        else if (type instanceof GenericArrayType)
          {
            final Type componentType = ((GenericArrayType)type).getGenericComponentType();
            final Class<?> componentClass = getClass(componentType);

            if (componentClass == null)
              {
                return null;
              }

            return Array.newInstance(componentClass, 0).getClass();
          }
        else
          {
            return null;
          }
      }
  }
