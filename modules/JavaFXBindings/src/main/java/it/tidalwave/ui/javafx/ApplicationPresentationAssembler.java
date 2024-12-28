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
package it.tidalwave.ui.javafx;

import javax.annotation.Nonnull;

/***************************************************************************************************************************************************************
 *
 * Implementation of this interface are called after Spring and JavaFX have been initialised. The purpose of those
 * implementations is to assemble the parts of the application presentation that might have been separately created.
 *
 * @param <DELEGATE>
 *
 * @since   1.0-ALPHA-13
 * @author  Fabrizio Giudici
 *
 **************************************************************************************************************************************************************/
public interface ApplicationPresentationAssembler<DELEGATE>
  {
    /***********************************************************************************************************************************************************
     * Assemble the application presentation. This method is called in the JavaFX thread.
     *
     * @param   delegate    the JavaFX delegate of the main UI
     **********************************************************************************************************************************************************/
    public void assemble (@Nonnull DELEGATE delegate);
  }