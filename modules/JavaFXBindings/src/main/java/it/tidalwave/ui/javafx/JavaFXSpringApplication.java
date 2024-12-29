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
import java.util.ArrayList;
import java.util.Locale;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import it.tidalwave.ui.javafx.spi.AbstractJavaFXSpringApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/***************************************************************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **************************************************************************************************************************************************************/
public class JavaFXSpringApplication extends AbstractJavaFXSpringApplication
  {
    // Don't use Slf4j and its static logger - give Main a chance to initialize things
    private final Logger log = LoggerFactory.getLogger(JavaFXSpringApplication.class);

    /***********************************************************************************************************************************************************
     * Creates the application context.
     *
     * @return  the application context
     **********************************************************************************************************************************************************/
    @Nonnull
    protected ConfigurableApplicationContext createApplicationContext()
      {
        final var springConfigLocations = new ArrayList<String>();
        final var osName = System.getProperty("os.name", "").toLowerCase(Locale.getDefault());
        springConfigLocations.add("classpath*:/META-INF/*AutoBeans.xml");

        if (osName.contains("os x"))
          {
            springConfigLocations.add("classpath*:/META-INF/*AutoMacOSXBeans.xml");
          }

        if (osName.contains("linux"))
          {
            springConfigLocations.add("classpath*:/META-INF/*AutoLinuxBeans.xml");
          }

        if (osName.contains("windows"))
          {
            springConfigLocations.add("classpath*:/META-INF/*AutoWindowsBeans.xml");
          }

        log.info("Loading Spring configuration from {} ...", springConfigLocations);
        return new ClassPathXmlApplicationContext(springConfigLocations.toArray(new String[0]));
      }
  }