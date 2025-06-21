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
module it.tidalwave.ui.core
  {
    requires static lombok;
    requires static jsr305;
    requires static org.slf4j;
    requires static com.github.spotbugs.annotations;
    requires transitive jakarta.annotation;
    requires transitive org.apiguardian.api;
    requires transitive java.desktop;
    requires spring.core;
    requires spring.beans;
    requires transitive it.tidalwave.util;
    requires transitive it.tidalwave.role;
    requires it.tidalwave.messagebus;        // not transitive, since it's optional
    requires it.tidalwave.messagebus.spring; // not transitive, since it's optional
    exports it.tidalwave.ui.core;
    exports it.tidalwave.ui.core.annotation;
    exports it.tidalwave.ui.core.function;
    exports it.tidalwave.ui.core.message;
    exports it.tidalwave.ui.core.role;
    exports it.tidalwave.ui.core.role.spi;
    exports it.tidalwave.ui.core.spi;
    exports it.tidalwave.ui.util;
    exports it.tidalwave.ui.util.impl; // FIXME - required only to it.tidalwave.ui.javafx
    opens it.tidalwave.ui.core.spi to spring.core;
    opens it.tidalwave.ui.util.impl to it.tidalwave.messagebus.spring, spring.core;
  }