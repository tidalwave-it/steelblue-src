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
package it.tidalwave.ui.javafx;
import jakarta.annotation.Nonnull;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import it.tidalwave.ui.javafx.impl.DefaultNodeAndDelegate;
/***************************************************************************************************************************************************************
 *
 * This facility class create a thread-safe proxy for the JavaFX delegate (controller). Thread-safe means that it can
 * be called by any thread and the JavaFX UI related stuff will be safely invoked in the JavaFX UI Thread.
 * It is usually used in this way:
 *
 * <pre>
 * // This is a Spring bean
 * public class JavaFxFooBarPresentation implements FooBarPresentation
 *   {
 *     private static final String FXML_URL = "/my/package/javafx/FooBar.fxml";
 *
 *     {@literal @}Inject
 *     private FlowController flowController;
 *
 *     private final NodeAndDelegate nad = createNodeAndDelegate(getClass(), FXML_URL);
 *
 *     private final FooBarPresentation delegate = nad.getDelegate();
 *
 *     public void showUp()
 *       {
 *         flowController.doSomething(nad.getNode());
 *       }
 *
 *     public void showData (final String data)
 *       {
 *         delegate.showData(data);
 *       }
 *   }
 * </pre>
 *
 * The method {@link #of(java.lang.Class, java.lang.String)} safely invokes the {@link FXMLLoader}
 * and returns a {@link NodeAndDelegate} that contains both the visual {@link Node} and its delegate (controller).
 *
 * The latter is wrapped by a safe proxy that makes sure that any method invocation (such as {@code showData()} in the
 * example is again executed in the JavaFX UI Thread. This means that the Presentation object methods can be invoked
 * in any thread.
 *
 * For method returning {@code void}, the method invocation is asynchronous; that is, the caller is not blocked waiting
 * for the method execution completion. If a return value is provided, the invocation is synchronous, and the caller
 * will correctly wait the completion of the execution in order to get the result value.
 *
 * A typical JavaFX delegate (controller) looks like:
 *
 * <pre>
 * // This is not a Spring bean - created by the FXMLLoader
 * public class JavaFxFooBarPresentationDelegate implements FooBarPresentation
 *   {
 *     {@literal @}FXML
 *     private Label label;
 *
 *     {@literal @}FXML
 *     private Button button;
 *
 *     {@literal @}Inject // the only thing that can be injected, by means of JavaFXSafeProxyCreator
 *     private JavaFxBinder binder;
 *
 *     {@literal @}Override
 *     public void bind (final UserAction action)
 *       {
 *         binder.bind(button, action);
 *       }
 *
 *     {@literal @}Override
 *     public void showData (final String data)
 *       {
 *         label.setText(data);
 *       }
 *  }
 * </pre>
 *
 * Not only all the methods invoked on the delegate are guaranteed to run in the JavaFX UI thread, but also its
 * constructor, as per JavaFX requirements.
 *
 * A Presentation Delegate must not try to have dependency injection from Spring (for instance, by means of AOP),
 * otherwise a deadlock could be triggered. Injection in constructors is safe.
 *
 * @author  Fabrizio Giudici
 *
 **************************************************************************************************************************************************************/
public interface NodeAndDelegate<T>
  {
    /***********************************************************************************************************************************************************
     * Creates a {@link NodeAndDelegate} for the given presentation class. The FXML resource name is inferred by
     * default, For instance, is the class is named {@code JavaFXFooBarPresentation}, the resource name is
     * {@code FooBar.fxml} and searched in the same packages as the class.
     * @param     presentationClass   the class of the presentation for which the resources must be created.
     * @since     1.0-ALPHA-13
     * @see       #of(java.lang.Class, java.lang.String)
     **********************************************************************************************************************************************************/
    @Nonnull
    public static <T> NodeAndDelegate<T> of (@Nonnull final Class<T> presentationClass)
      {
        return DefaultNodeAndDelegate.of(presentationClass);
      }
    /***********************************************************************************************************************************************************
     * Creates a {@link NodeAndDelegate} for the given presentation class.
     * @param   presentationClass   the class of the presentation for which the resources must be created.
     * @param   fxmlResourcePath    the path of the FXML resource
     **********************************************************************************************************************************************************/
    @Nonnull
    public static <T> NodeAndDelegate<T> of (@Nonnull final Class<T> presentationClass, @Nonnull final String fxmlResourcePath)
      {
        return DefaultNodeAndDelegate.of(presentationClass, fxmlResourcePath);
      }
    @Nonnull
    public Node getNode();
    @Nonnull
    public T getDelegate();
  }
