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
package it.tidalwave.ui.core.role;
import jakarta.annotation.Nonnull;
import java.util.Comparator;
import java.util.Locale;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import it.tidalwave.ui.core.role.impl.AsDisplayableComparator;
import it.tidalwave.ui.core.role.impl.DefaultDisplayable;
import it.tidalwave.ui.core.role.impl.DisplayableComparator;
import it.tidalwave.util.As;
import static it.tidalwave.util.BundleUtilities.getMessage;
/***************************************************************************************************************************************************************
 *
 * The role of an object which can provide its own display name.
 *
 * @stereotype Role
 *
 * @since   2.0-ALPHA-1
 * @author  Fabrizio Giudici
 * @it.tidalwave.javadoc.stable
 *
 **************************************************************************************************************************************************************/
@FunctionalInterface
public interface Displayable
  {
    /** Shortcut for {@link it.tidalwave.util.As}. */
    public static final Class<Displayable> _Displayable_ = Displayable.class;
    /***********************************************************************************************************************************************************
     * A default {@code Displayable} with an empty display name.
     **********************************************************************************************************************************************************/
    public static final Displayable DEFAULT = new DefaultDisplayable("", "DEFAULT");
    /***********************************************************************************************************************************************************
     * Returns the display name in the current {@link java.util.Locale}.
     *
     * @return  the display name
     **********************************************************************************************************************************************************/
    @Nonnull
    public String getDisplayName();
    /***********************************************************************************************************************************************************
     * Returns the display name in the given {@link Locale}.
     * @param    locale       the {@code Locale}
     * @since                 2.0-ALPHA-2
     * @return                the display name
     **********************************************************************************************************************************************************/
    @Nonnull
    public default String getDisplayName (@Nonnull final Locale locale)
      {
        return getDisplayName();
      }
    /***********************************************************************************************************************************************************
     * Returns all the display names in {@link Map} where they are indexed by {@code Locale}.
     * @since                 2.0-ALPHA-2
     * @return                the display names
     **********************************************************************************************************************************************************/
    @Nonnull
    public default Map<Locale, String> getDisplayNames()
      {
        return Map.of();
      }
    /***********************************************************************************************************************************************************
     * Returns the supported {@code Locale}s.
     * @since                 2.0-ALPHA-2
     * @return                the available {@code Locale}s
     **********************************************************************************************************************************************************/
    @Nonnull
    public default SortedSet<Locale> getLocales()
      {
        return new TreeSet<>();
      }
    /***********************************************************************************************************************************************************
     * Sends the display name in the current {@link java.util.Locale} to a given customer.
     *
     * @param     consumer    the {@code Consumer}
     * @since     3.2-ALPHA-15
     **********************************************************************************************************************************************************/
    @SuppressWarnings("BoundedWildcard")
    public default void display (@Nonnull final Consumer<String> consumer)
      {
        consumer.accept(getDisplayName());
      }
    /***********************************************************************************************************************************************************
     * Creates an instance with a given display name.
     *
     * @param  displayName    the display name
     * @return                the new instance
     * @since                 3.2-ALPHA-1 (was {@code DefaultDisplayable}
     **********************************************************************************************************************************************************/
    @Nonnull
    public static Displayable of (@Nonnull final String displayName)
      {
        return of(displayName, "???");
      }
    /***********************************************************************************************************************************************************
     * Creates an instance with a given display name iand an explicit label for  {@code toString()}.
     *
     * @param  displayName    the display name
     * @param  toStringName   the name to be rendered when {@code toString()} is called
     * @return                the new instance
     * @since                 3.2-ALPHA-1 (was {@code DefaultDisplayable}
     **********************************************************************************************************************************************************/
    @Nonnull
    public static Displayable of (@Nonnull final String displayName, @Nonnull final String toStringName)
      {
        return new DefaultDisplayable(displayName, toStringName);
      }
    /***********************************************************************************************************************************************************
     * Creates an instance from a {@link Supplier}{@code <String>}. The supplier is invoked each time
     * {@link #getDisplayName()} is called.
     *
     * @param   supplier      the {@code Supplier}
     * @return                the new instance
     * @since                 3.2-ALPHA-3
     * @it.tidalwave.javadoc.experimental
     **********************************************************************************************************************************************************/
    @Nonnull
    public static Displayable of (@Nonnull final Supplier<String> supplier)
      {
        return supplier::get;
      }
    /***********************************************************************************************************************************************************
     * Creates an instance from a {@link Function}{@code <T, String>} and a generic object that the function is applied
     * to. The function is invoked each time {@link #getDisplayName()} is called.
     *
     * @param   <T>           the type of the object
     * @param   function      the {@code Function}
     * @param   object        the object
     * @return                the new instance
     * @since                 3.2-ALPHA-3
     * @it.tidalwave.javadoc.experimental
     **********************************************************************************************************************************************************/
    @Nonnull
    public static <T> Displayable of (@Nonnull final Function<T, String> function, @Nonnull final T object)
      {
        return () -> function.apply(object);
      }
    /***********************************************************************************************************************************************************
     * Creates a {@link Displayable} from a resource bundle. The bundle resource file is named {@code Bundle.properties} and it should be placed in the same
     * package as the owner class.
     * @param   ownerClass    the class that owns the bundle
     * @param   key           the resource key
     * @since                 2.0-ALPHA-2
     * @return                the new instance
     **********************************************************************************************************************************************************/
    @Nonnull
    public static Displayable fromBundle (@Nonnull final Class<?> ownerClass, @Nonnull final String key)
      {
        return new DefaultDisplayable(getMessage(ownerClass, key));
      }
    /***********************************************************************************************************************************************************
     * Returns a {@link Comparator} for comparing two instances of {@code Displayable}.
     *
     * @return  the {@code Comparator}
     * @since   3.2-ALPHA-6
     **********************************************************************************************************************************************************/
    @Nonnull
    public static Comparator<Displayable> comparing()
      {
        return DisplayableComparator.getInstance();
      }
    /***********************************************************************************************************************************************************
     * Returns a {@link Comparator} for comparing two instances of objects implementing {@code As} that contain the
     * {@code Displayable} role.
     *
     * @return  the {@code Comparator}
     * @since   3.2-ALPHA-6
     **********************************************************************************************************************************************************/
    @Nonnull
    public static Comparator<As> asComparing()
      {
        return AsDisplayableComparator.getInstance();
      }
    /***********************************************************************************************************************************************************
     * Renders the attached object into a {@link String}. The method accepts optional parameters that can be used to control the format of the rendering; they
     * are usually specific of the object attached to this role.
     * @param  args  optional rendering parameters
     * @return       the string
     **********************************************************************************************************************************************************/
    @Nonnull
    public default String render (@Nonnull final Object... args)
      {
        return getDisplayName();
      }
    /***********************************************************************************************************************************************************
     * Renders the attached object providing the string tu a {@link Consumer}. The method accepts optional parameters that can be used to control the format of
     * the rendering; they are usually specific of the object attached to this role.
     * @param  consumer        the {@code Consumer} to append to
     * @param  args            optional rendering parameters
     **********************************************************************************************************************************************************/
    public default void renderTo (@Nonnull final Consumer<? super String> consumer, @Nonnull final Object... args)
      {
        consumer.accept(render(args));
      }
  }
