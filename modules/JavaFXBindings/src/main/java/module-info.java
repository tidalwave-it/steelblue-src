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
module it.tidalwave.ui.javafx
  {
    requires static lombok;
    requires static jsr305;
    requires static org.slf4j;
    requires static com.github.spotbugs.annotations;
    requires transitive it.tidalwave.ui.core;
    requires it.tidalwave.messagebus; // not transitive, since it's optional
    requires transitive jakarta.annotation;
    requires transitive jakarta.inject;
    requires transitive javafx.base;
    requires transitive javafx.controls;
    requires transitive javafx.graphics;
    requires transitive javafx.fxml;
    requires transitive javafx.web;
    requires transitive org.jfxtras.styles.jmetro;
    requires spring.beans;
    requires spring.context;
    exports it.tidalwave.ui.javafx;
    exports it.tidalwave.ui.javafx.role;
    opens it.tidalwave.ui.javafx to spring.core, javafx.graphics, javafx.fxml;
    exports it.tidalwave.ui.javafx.impl;
    opens it.tidalwave.ui.javafx.impl to javafx.fxml, javafx.graphics, spring.core;
  }