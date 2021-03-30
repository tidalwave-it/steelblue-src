/*
 * *********************************************************************************************************************
 *
 * SteelBlue: DCI User Interfaces
 * http://tidalwave.it/projects/steelblue
 *
 * Copyright (C) 2015 - 2021 by Tidalwave s.a.s. (http://tidalwave.it)
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
 * git clone https://bitbucket.org/tidalwave/steelblue-src
 * git clone https://github.com/tidalwave-it/steelblue-src
 *
 * *********************************************************************************************************************
 */
package it.tidalwave.ui.javafx;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.Executors;
import java.io.IOException;
import javafx.stage.Stage;
import javafx.application.Platform;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import it.tidalwave.role.ui.javafx.ApplicationPresentationAssembler;
import it.tidalwave.ui.javafx.JavaFXSafeProxyCreator.NodeAndDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
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
    protected NodeAndDelegate createParent()
      throws IOException
      {
        return NodeAndDelegate.load(getClass(), applicationFxml);
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @Override
    protected void initializeInBackground()
      {
        log.info("initializeInBackground()");

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
    protected void onStageCreated (@Nonnull final Stage stage,
                                   @Nonnull final NodeAndDelegate applicationNad)
      {
        assert Platform.isFxApplicationThread();
        JavaFXSafeProxyCreator.getJavaFxBinder().setMainWindow(stage);
        runApplicationAssemblers(applicationNad);
        Executors.newSingleThreadExecutor().execute(() -> onStageCreated(applicationContext));
      }

    /*******************************************************************************************************************
     *
     * Invoked when the {@link Stage} is created and the {@link ApplicationContext} has been initialized. Typically
     * the main class overrides this, retrieves a reference to the main controller and boots it.
     * This method is executed in a background thread.
     *
     * @param   applicationContext  the application context
     *
     ******************************************************************************************************************/
    protected void onStageCreated (@Nonnull final ApplicationContext applicationContext)
      {
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
    private void runApplicationAssemblers (@Nonnull final NodeAndDelegate applicationNad)
      {
        Objects.requireNonNull(applicationContext, "applicationContext is null");
        applicationContext.getBeansOfType(ApplicationPresentationAssembler.class).values()
                .forEach(a -> a.assemble(applicationNad.getDelegate()));
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