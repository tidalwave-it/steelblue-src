/*
 * *************************************************************************************************************************************************************
 *
 * SteelBlue: DCI User Interfaces
 * http://tidalwave.it/projects/steelblue
 *
 * Copyright (C) 2015 - 2026 by Tidalwave s.a.s. (http://tidalwave.it)
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
module it.tidalwave.ui.example.presentation.javafx
  {
    requires static lombok;
    requires jakarta.annotation;
    requires org.slf4j;
    requires spring.aspects;
    requires spring.context;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires it.tidalwave.ui.javafx;
    requires it.tidalwave.ui.example.presentation;
    opens it.tidalwave.ui.example.presentation.javafx to spring.core, spring.beans, spring.context, javafx.graphics, javafx.fxml, it.tidalwave.ui.javafx;
    opens it.tidalwave.ui.example.presentation.impl.javafx to spring.core, spring.beans, javafx.graphics, spring.context, javafx.fxml, it.tidalwave.util,
            it.tidalwave.ui.javafx;
  }
