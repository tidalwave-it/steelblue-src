package it.tidalwave.ui.javafx.impl;

import javafx.stage.Stage;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.application.Preloader;
import org.testfx.api.FxRobot;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationAdapter;
import org.testfx.framework.junit5.ApplicationFixture;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

abstract class TestNGApplicationTest extends FxRobot implements ApplicationFixture
  {
    public static Application launch(Class<? extends Application> appClass, String... appArgs)
            throws Exception
      {
        FxToolkit.registerPrimaryStage();
        return FxToolkit.setupApplication(appClass, appArgs);
      }

    @BeforeMethod
    public final void internalBefore()
            throws Exception
      {
        FxToolkit.registerPrimaryStage();
        FxToolkit.setupApplication(() -> new ApplicationAdapter(this));
      }

    @AfterMethod
    public final void internalAfter()
            throws Exception
      {
        FxToolkit.cleanupAfterTest(this, new ApplicationAdapter(this));
      }

    public void init()
      {
      }

    public void start (Stage stage)
      {
      }

    public void stop()
      {
      }

    /** @deprecated */
    @Deprecated
    public final HostServices getHostServices() {
      throw new UnsupportedOperationException();
    }

    /** @deprecated */
    @Deprecated
    public final Application.Parameters getParameters() {
      throw new UnsupportedOperationException();
    }

    /** @deprecated */
    @Deprecated
    public final void notifyPreloader(Preloader.PreloaderNotification notification) {
      throw new UnsupportedOperationException();
    }
  }