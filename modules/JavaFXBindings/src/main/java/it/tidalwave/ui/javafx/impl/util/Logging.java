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
package it.tidalwave.ui.javafx.impl.util;

import javax.annotation.Nonnull;
import java.util.Collection;
import it.tidalwave.util.As;
import it.tidalwave.role.Aggregate;
import it.tidalwave.role.Composite;
import it.tidalwave.role.ui.Displayable;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import static java.util.stream.Collectors.*;
import static it.tidalwave.util.ShortNames.shortName;

/***************************************************************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **************************************************************************************************************************************************************/
@Slf4j @UtilityClass
public class Logging
  {
    public static final String INDENT = " ".repeat(100);

    public static final String P_LOG_CHILDREN = Logging.class.getName() + ".logCompositeChildren";

    public static final boolean logChildren = Boolean.getBoolean(P_LOG_CHILDREN);

    /***********************************************************************************************************************************************************
     *
     **********************************************************************************************************************************************************/
    public static void logObjects (@Nonnull final String prefix, @Nonnull final Collection<?> objects)
      {
        if (!log.isDebugEnabled())
          {
            return;
          }

        if (objects.isEmpty())
          {
            log.debug(">>>>{} <empty>", prefix);
          }
        else
          {
            for (final Object object : objects)
              {
                logObject(prefix, object);
              }
          }
      }

    /***********************************************************************************************************************************************************
     *
     **********************************************************************************************************************************************************/
    public static void logObject (@Nonnull final String indent, @Nonnull final Object object)
      {
        if (!log.isDebugEnabled())
          {
            return;
          }

        if (object instanceof Displayable)
          {
            var displayableName = "";

            try
              {
                displayableName = ((Displayable)object).getDisplayName();
              }
            catch (RuntimeException e)
              {
                log.error("", e);
                displayableName = "<ERROR>";
              }

            log.debug(">>>>     {}{}: {}", indent, shortName(object.getClass()), displayableName);
          }
        else
          {
            log.debug(">>>>     {}{}", indent, object);
          }

        if (object instanceof Aggregate)
          {
            final var aggregate = (Aggregate<?>)object;
            aggregate.getNames().forEach(name ->
                       logObject(indent + "    " + name + ": ", aggregate.getByName(name).get()));
          }

        if (object instanceof Composite)
          {
            final var finder = ((Composite<?, ?>)object).findChildren();

            // this is optional because it would jeopardize incremental loading of tress and probably cause troubles
            if (!logChildren)
              {
                log.debug(">>>>     {}    to see children, set system property to true: " + P_LOG_CHILDREN, indent);
              }
            else
              {
                logObjects(indent + "    ", finder.results().stream().filter(o -> o != object).collect(toList()));
              }
          }

        if (object instanceof As)
          {
            logObjects(indent + "    Role: ",
                       ((As)object).asMany(Object.class).stream().filter(o -> o != object).collect(toList()));
          }
      }
  }