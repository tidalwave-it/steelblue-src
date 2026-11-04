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
import java.util.Collection;
import java.util.Collections;
import it.tidalwave.ui.core.Mutable;
import it.tidalwave.ui.core.role.impl.DefaultPresentationModel;
import org.apiguardian.api.API;
import it.tidalwave.util.As;
import it.tidalwave.util.NamedCallback;
import it.tidalwave.util.Parameters;
import it.tidalwave.role.Aggregate;
import it.tidalwave.role.SimpleComposite;
import static it.tidalwave.ui.core.role.Presentable._Presentable_;
import static org.apiguardian.api.API.Status.EXPERIMENTAL;
import static it.tidalwave.util.Parameters.r;
/***************************************************************************************************************************************************************
 *
 * TODO: As the NetBeans Node, it should allow children, have event listeners for children added/removed/changed.
 * This class so becomes the true M in MVC.
 *
 * @stereotype  Role
 * @since       2.0-ALPHA-1
 * @author      Fabrizio Giudici
 *
 **************************************************************************************************************************************************************/
public interface PresentationModel extends As, Mutable
  {
    /** Shortcut for {@link it.tidalwave.util.As}. */
    public static final Class<PresentationModel> _PresentationModel_ = PresentationModel.class;
    /** Shortcut for {@link it.tidalwave.util.As}. */
    public static final As.Type<SimpleComposite<PresentationModel>> _CompositeOfPresentationModel_ = As.type(SimpleComposite.class);
    /** Shortcut for {@link it.tidalwave.util.As}. */
    public static final As.Type<Aggregate<PresentationModel>> _AggregateOfPresentationModel_ = new As.Type<>(Aggregate.class);
    public static final String PROPERTY_CHILDREN = "children";
    /** This is an undocumented feature. If you add a {@link NamedCallback} with this name as a role in this object, it will be called back when
        {@link #dispose()} is called. */
    public static final String CALLBACK_DISPOSE = "dispose";
    public static final String P_VERBOSE_TOSTRING = PresentationModel.class.getCanonicalName() + ".verboseToString";
    public static final String PARAM_OWNER = "owner";
    public static final String PARAM_ROLE = "role";
    /***********************************************************************************************************************************************************
     * Disposes this object.
     **********************************************************************************************************************************************************/
    public void dispose();
    /***********************************************************************************************************************************************************
     * {@return a new instance given an owner and no roles}.
     * @param   owner   the owner
     * @since           3.2-ALPHA-3
     **********************************************************************************************************************************************************/
    @Nonnull
    public static PresentationModel of (@Nonnull final Object owner)
      {
        Parameters.mustNotBeArrayOrCollection(owner, PARAM_OWNER);
        return of(owner, Collections.emptyList());
      }
    /***********************************************************************************************************************************************************
     * {@return a new instance given an owner and a single role}.
     * @param   owner   the owner
     * @param   role    the role (or a {@link it.tidalwave.util.RoleFactory})
     * @since           3.2-ALPHA-3
     **********************************************************************************************************************************************************/
    @Nonnull
    public static PresentationModel of (@Nonnull final Object owner, @Nonnull final Object role)
      {
        Parameters.mustNotBeArrayOrCollection(owner, PARAM_OWNER);
        Parameters.mustNotBeArrayOrCollection(role, PARAM_ROLE);
        return of(owner, r(role));
      }
    /***********************************************************************************************************************************************************
     * {@return a new instance given an owner and multiple roles}.
     * @param   owner   the owner
     * @param   roles   roles or {@link it.tidalwave.util.RoleFactory} instances
     * @since           3.2-ALPHA-1
     * @since           3.2-ALPHA-3 (refactored)
     **********************************************************************************************************************************************************/
    @Nonnull
    public static PresentationModel of (@Nonnull final Object owner, @Nonnull final Collection<Object> roles)
      {
        Parameters.mustNotBeArrayOrCollection(owner, PARAM_OWNER);
        return new DefaultPresentationModel(owner, roles);
      }
    /***********************************************************************************************************************************************************
     * {@return an empty instance (no roles, with the exception of a dummy {@link Displayable})}.
     * @since           3.2-ALPHA-3
     **********************************************************************************************************************************************************/
    @Nonnull
    public static PresentationModel empty()
      {
        // TODO: cache a singleton, but don't do eager initialization (e.g. a final static), as it would deadlock
        return of("", Displayable.of("<empty presentation model>"));
      }
    /***********************************************************************************************************************************************************
     * {@return a new instance from an owner which might have the {@link Presentable} role}. If it is present, it is called to create the
     * {@code PresentationModel}; otherwise a default one is created. Additional roles are added.
     * @param   owner   the owner
     * @param   roles   roles or {@link it.tidalwave.util.RoleFactory} instances
     * @since           3.2-ALPHA-8
     **********************************************************************************************************************************************************/
    @API(status = EXPERIMENTAL) // TODO: perhaps it could be merged to of().
    @Nonnull
    public static PresentationModel ofMaybePresentable (@Nonnull final As owner, @Nonnull final Collection<Object> roles)
      {
        Parameters.mustNotBeArrayOrCollection(owner, PARAM_OWNER);
        return owner.maybeAs(_Presentable_)
                    .map(p -> p.createPresentationModel(roles))
                    .orElseGet(() -> of(owner, roles));
      }
    /***********************************************************************************************************************************************************
     * {@return a new instance from an owner which might have the {@link Presentable} role}. If it is present, it is called to create the
     * {@code PresentationModel}; otherwise a default one is created.
     * @param   owner   the owner
     * @since           3.2-ALPHA-8
     **********************************************************************************************************************************************************/
    @API(status = EXPERIMENTAL) // TODO: perhaps it could be merged to of().
    @Nonnull
    public static PresentationModel ofMaybePresentable (@Nonnull final As owner)
      {
        return ofMaybePresentable(owner, Collections.emptyList());
      }
    /***********************************************************************************************************************************************************
     * Sets the verbose mode for {@link java.lang.Object#toString} implementations of {@code PresentationModel}. Looking at the implementation of
     * {@link javafx.scene.control.cell.DefaultTreeCell}, the code always calls {@code toString()} during an update, even though the text value is later
     * overridden; hence, this method should be as fast as possible. By default, the shortened class name and system id of the owner object is returned;
     * set verbosity to have the owner object {@code toString()} called instead.
     * It is also possible to set system the property {@code it.tidalwave.ui.core.role.PresentationModel.verboseToString}.
     * @param           verbose   the verbosity
     * @since           2.0-ALPHA-4
     * @see             #isVerboseToString()
     **********************************************************************************************************************************************************/
    public static void setVerboseToString (final boolean verbose)
      {
        DefaultPresentationModel.setVerboseToString(verbose);
      }
    /***********************************************************************************************************************************************************
     * {@return the verbose mode for {@link java.lang.Object#toString} implementations of {@code PresentationModel}}.
     * @since           2.0-ALPHA-4
     * @see             #setVerboseToString(boolean)
     **********************************************************************************************************************************************************/
    public static boolean isVerboseToString()
      {
        return DefaultPresentationModel.isVerboseToString();
      }
  }
