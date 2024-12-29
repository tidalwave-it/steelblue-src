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
package it.tidalwave.ui.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/***************************************************************************************************************************************************************
 *
 * If a {@code Main} class is annotated with {@code EnableMessageBus}, the following things happen:
 *
 * <ul>
 * <li>a messagebus implementing {@link it.tidalwave.messagebus.MessageBus} is instantiated and made available in the Spring application context
 *   (so it can be injected);</li>
 * <li>a {@link it.tidalwave.message.PowerOnEvent} is fired when the application is ready to be initialised;</li>
 * <li>a {@link it.tidalwave.message.PowerOffEvent} is fired when the application is going to be closed.</li>
 * </ul>
 *
 * @since   1.1-ALPHA-6
 * @author  Fabrizio Giudici
 *
 **************************************************************************************************************************************************************/
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnableMessageBus
  {
  }
