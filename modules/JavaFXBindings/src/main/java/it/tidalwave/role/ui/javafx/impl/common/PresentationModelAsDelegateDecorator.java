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
package it.tidalwave.role.ui.javafx.impl.common;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import it.tidalwave.role.ui.PresentationModel;
import it.tidalwave.util.As;
import it.tidalwave.util.AsException;
import lombok.AccessLevel;
import lombok.experimental.Delegate;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/***********************************************************************************************************************
 *
 * A decorator for {@link PresentationModel} that also searches for roles in a specified delegate as a fallback.
 *
 * FIME: move to TFT
 *
 * @stereotype Decorator
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
@RequiredArgsConstructor(access = AccessLevel.PRIVATE) @ToString
public class PresentationModelAsDelegateDecorator implements PresentationModel
  {
    @Delegate(excludes = As.class) @Nonnull
    private final PresentationModel pmDelegate;

    private final As asDelegate;

    @Nonnull
    public static PresentationModel decorating (@Nonnull final PresentationModel pmDelegate,
                                                @Nonnull final As asDelegate)
      {
        return new PresentationModelAsDelegateDecorator(pmDelegate, asDelegate);
      }

    @Override @Nonnull
    public <T> T as (@Nonnull final Class<T> type)
      {
        try
          {
            return pmDelegate.as(type);
          }
        catch (AsException e)
          {
            return asDelegate.as(type);
          }
      }

    @Override @Nonnull
    public <T> T as (@Nonnull final Class<T> type, @Nonnull final NotFoundBehaviour<T> notFoundBehaviour)
      {
        throw new UnsupportedOperationException("Not implemented yet");
      }

    @Override @Nonnull
    public <T> Collection<T> asMany (@Nonnull final Class<T> type)
      {
        final List<T> results = new ArrayList<>();
        results.addAll(pmDelegate.asMany(type));
        results.addAll(asDelegate.asMany(type));

        return results;
      }
  }
