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
package it.tidalwave.ui.core;
import jakarta.annotation.Nonnull;
import org.apiguardian.api.API;
import static org.apiguardian.api.API.Status.EXPERIMENTAL;
/***************************************************************************************************************************************************************
 *
 * A service that provides a piece of he User Interface together with the definition of the group in the main window where it should be made visible (left,
 * right, center or bottom). Instances of this class are looked up and managed by {@link PanelGroupControl}.
 *
 * @see         PanelGroupControl
 * @since       2.0-ALPHA-3
 * @author      Fabrizio Giudici
 *
 **************************************************************************************************************************************************************/
@API(status = EXPERIMENTAL)
public interface PanelGroupProvider<T>
  {
    /***********************************************************************************************************************************************************
     * {@return the label associated to this User Interface piece}.
     **********************************************************************************************************************************************************/
    @Nonnull
    public String getLabel();
    /***********************************************************************************************************************************************************
     * {@return the presentation associated to this provider}.
     * @since       2.0-ALPHA-5
     **********************************************************************************************************************************************************/
    @Nonnull
    public Object getPresentation();
    /***********************************************************************************************************************************************************
     * {@return the concrete UI component associated to this provider}.
     **********************************************************************************************************************************************************/
    @Nonnull
    public T getComponent();
    /***********************************************************************************************************************************************************
     * {@return the placement of this piece of the User Interface}.
     **********************************************************************************************************************************************************/
    @Nonnull
    public PanelGroupControl.Group getGroup();
  }
