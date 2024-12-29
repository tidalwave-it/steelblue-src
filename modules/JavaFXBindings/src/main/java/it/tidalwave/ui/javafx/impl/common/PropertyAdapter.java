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
package it.tidalwave.ui.javafx.impl.common;

import javax.annotation.Nonnull;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;
import javafx.beans.InvalidationListener;
import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.application.Platform;
import it.tidalwave.ui.core.BoundProperty;
import lombok.extern.slf4j.Slf4j;

/***************************************************************************************************************************************************************
 *
 * Adapts a {@link BoundProperty} to a JavaFX {@link Property}. It also takes care of threaring issues, making sure that
 * the JavaFX {@code Property} is updated in the JavaFX UI thread. Conversely, updates on the JavaFX 
 * {@code BoundProperty} are executed in a separated thread provided by an {@link Executor}.
 *
 * TODO: javafx.beans.binding.BooleanExpression.booleanExpression(source)? Does it do threading?
 * 
 * @author  Fabrizio Giudici
 *
 **************************************************************************************************************************************************************/
@Slf4j
public class PropertyAdapter<T> implements Property<T>
  {
    @Nonnull
    private final Executor executor;

    @Nonnull
    private final BoundProperty<T> delegate;

    // FIXME: WEAK LISTENER!!
    private final List<ChangeListener<? super T>> changeListeners = new ArrayList<>();
    
    // FIXME: WEAK LISTENER!!
    private final List<InvalidationListener> invalidationListeners = new ArrayList<>();

    private T boundValue;

    private final PropertyChangeListener propertyChangeListener = (event) -> 
      {
        log.trace("propertyChange({}) - bound value: {}", event, boundValue);
        
        if (!Objects.equals(boundValue, event.getNewValue()))
          {
            boundValue = (T)event.getNewValue();
            Platform.runLater(() ->
              {
                new ArrayList<>(invalidationListeners)
                        .forEach(listener -> listener.invalidated(this));
                new ArrayList<>(changeListeners)
                        .forEach(listener -> listener.changed(this,
                                (T)event.getOldValue(), (T)event.getNewValue()));
              });
          }
    };

    public PropertyAdapter (@Nonnull final Executor executor, @Nonnull final BoundProperty<T> delegate)
      {
        this.executor = executor;
        this.delegate = delegate;
        this.boundValue = delegate.get();
        delegate.addPropertyChangeListener(propertyChangeListener);
      }

    @Override
    public T getValue()
      {
        return delegate.get();
      }

    @Override
    public void setValue (final T value)
      {
        log.debug("setValue({})", value);
        boundValue = value;

        if (!Objects.equals(value, delegate.get()))
          {
            executor.execute(() -> delegate.set(value));
          }
      }

    @Override
    public void addListener (@Nonnull final ChangeListener<? super T> listener)
      {
        changeListeners.add(listener);
      }

    @Override
    public void removeListener (@Nonnull final ChangeListener<? super T> listener)
      {
        changeListeners.remove(listener);
      }

    @Override
    public void addListener (@Nonnull final InvalidationListener listener)
      {
        invalidationListeners.add(listener);
      }

    @Override
    public void removeListener (@Nonnull final InvalidationListener listener)
      {
        invalidationListeners.remove(listener);
      }

    @Override
    public void bind (final ObservableValue<? extends T> observable)
      {
        log.warn("bind({})", observable);
        throw new UnsupportedOperationException("Not supported yet.");
      }

    @Override
    public void unbind()
      {
        log.warn("unbind()");
        throw new UnsupportedOperationException("Not supported yet.");
      }

    @Override
    public boolean isBound()
      {
        log.warn("isBound()");
        throw new UnsupportedOperationException("Not supported yet.");
      }

    @Override
    public void bindBidirectional (final Property<T> other)
      {
        log.warn("bindBidirectional({})", other);
        throw new UnsupportedOperationException("Not supported yet.");
      }

    @Override
    public void unbindBidirectional (final Property<T> other)
      {
        log.warn("unbindBidirectional({})", other);
        throw new UnsupportedOperationException("Not supported yet.");
      }

    @Override
    public Object getBean()
      {
        log.warn("getBean()");
        throw new UnsupportedOperationException("Not supported yet.");
      }

    @Override
    public String getName()
      {
        log.warn("getName()");
        throw new UnsupportedOperationException("Not supported yet.");
      }
  }
