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
import org.springframework.context.ApplicationContext;

/***************************************************************************************************************************************************************
 *
 * This interface can be implemented by the application main presentation. In this case, it will called back with the
 * related method to be able to programmatically assemble parts of the UI — typically a toolbar and a main panel.
 *
 * @since   1.1-ALPHA-4
 * @author  Fabrizio Giudici
 *
 **************************************************************************************************************************************************************/
public interface PresentationAssembler
  {
    /***********************************************************************************************************************************************************
     * Assemble the application presentation. This method is called in the JavaFX thread.
     *
     * @param   applicationContext    the application context
     **********************************************************************************************************************************************************/
    public void assemble (@Nonnull ApplicationContext applicationContext);
  }
