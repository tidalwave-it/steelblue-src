/*
 * *************************************************************************************************************************************************************
 *
 * blueMarine III: Semantic DAM
 * http://tidalwave.it/projects/bluemarine3
 *
 * Copyright (C) 2024 - 2025 by Tidalwave s.a.s. (http://tidalwave.it)
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
 * git clone https://bitbucket.org/tidalwave/bluemarine3-src
 * git clone https://github.com/tidalwave-it/bluemarine3-src
 *
 * *************************************************************************************************************************************************************
 */
package it.tidalwave.ui.javafx.impl.common;
import jakarta.annotation.Nonnull;
import javafx.util.StringConverter;
import it.tidalwave.ui.core.role.Displayable;
import it.tidalwave.util.As;
import lombok.NoArgsConstructor;
import static it.tidalwave.ui.core.role.Displayable._Displayable_;
import static lombok.AccessLevel.PRIVATE;
/***************************************************************************************************************************************************************
 *
 * @author Fabrizio Giudici
 *
 **************************************************************************************************************************************************************/
@NoArgsConstructor(access = PRIVATE)
public class AsObjectStringConverter extends StringConverter<As>
  {
    private static final AsObjectStringConverter INSTANCE = new AsObjectStringConverter();
    @Nonnull @SuppressWarnings("unchecked")
    public static <T extends As> StringConverter<T> getInstance()
      {
        return (StringConverter<T>)INSTANCE;
      }
    @Override
    public String toString (final As object)
      {
        return object.maybeAs(_Displayable_).map(Displayable::getDisplayName).orElse("");
      }
    @Override
    public As fromString (final String string)
      {
        throw new UnsupportedOperationException();
      }
  }
