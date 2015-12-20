/*
 * #%L
 * *********************************************************************************************************************
 *
 * SteelBlue
 * http://steelblue.tidalwave.it - git clone git@bitbucket.org:tidalwave/steelblue-src.git
 * %%
 * Copyright (C) 2015 - 2015 Tidalwave s.a.s. (http://tidalwave.it)
 * %%
 *
 * *********************************************************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * *********************************************************************************************************************
 *
 * $Id$
 *
 * *********************************************************************************************************************
 * #L%
 */
package it.tidalwave.ui.javafx;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import it.tidalwave.role.ui.javafx.JavaFXBinder;
import java.util.ArrayList;
import java.util.List;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class JavaFXSpringApplication extends JavaFXApplicationWithSplash
  {
    // Don't use Slf4j and its static logger - give Main a chance to initialize things
    private final Logger log = LoggerFactory.getLogger(JavaFXSpringApplication.class);
            
    private ClassPathXmlApplicationContext applicationContext;

    private final List<String> springConfigLocations = new ArrayList<>();
    
    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @Override @Nonnull
    protected Parent createParent()
      throws IOException
      {
        return FXMLLoader.load(getClass().getResource("Application.fxml"));
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @Override
    protected void initializeInBackground()
      {
        try
          {
            logProperties();
            // TODO: workaround for NWRCA-41
            System.setProperty("it.tidalwave.util.spring.ClassScanner.basePackages", "it");
            
            springConfigLocations.add("classpath*:/META-INF/*AutoBeans.xml");
            final String osName = System.getProperty("os.name", "").toLowerCase();

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
            applicationContext = new ClassPathXmlApplicationContext(springConfigLocations.toArray(new String[0]));
            applicationContext.registerShutdownHook(); // this actually seems not working, onClosing() does
          }
        catch (Throwable t)
          {
            log.error("", t);
          }
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @Override
    protected void onStageCreated (@Nonnull Stage stage)
      {
        applicationContext.getBean(JavaFXBinder.class).setMainWindow(stage);
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @Override
    protected void onClosing()
      {
        applicationContext.close();
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    private void logProperties()
      {
        final SortedMap<Object, Object> map = new TreeMap<>(System.getProperties());

        for (final Entry<Object, Object> e : map.entrySet())
          {
            log.debug("{}: {}", e.getKey(), e.getValue());
          }
      }
  }