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
package it.tidalwave.ui.example.presentation.impl;

import javax.annotation.Nonnull;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Collection;
import java.util.Locale;
import it.tidalwave.ui.core.role.Displayable;
import it.tidalwave.ui.core.role.PresentationModel;
import it.tidalwave.ui.core.role.PresentationModelAggregate;
import it.tidalwave.ui.core.role.Styleable;
import it.tidalwave.ui.example.model.FileEntity;
import it.tidalwave.ui.core.role.spi.SimpleCompositePresentable;
import it.tidalwave.dci.annotation.DciRole;
import lombok.extern.slf4j.Slf4j;
import static it.tidalwave.util.FunctionalCheckedExceptionWrappers.*;
import static it.tidalwave.util.LocalizedDateTimeFormatters.getDateTimeFormatterFor;
import static it.tidalwave.util.Parameters.r;

/***************************************************************************************************************************************************************
 *
 * A {@link it.tidalwave.ui.core.role.Presentable} implementation for {@link FileEntity}, creates a {@link PresentationModel}
 * for a tabular rendering with four columns: name, size, creationDate and latestModificationDate.
 *
 * @author  Fabrizio Giudici
 *
 **************************************************************************************************************************************************************/
// TODO: add roles such as Removable, present only if permissions allow
// TODO: iconprovider
// START SNIPPET: all
@DciRole(datumType = FileEntity.class) @Slf4j
public class FileEntityPresentable extends SimpleCompositePresentable
  {
    private static final DateTimeFormatter FORMATTER = getDateTimeFormatterFor(FormatStyle.SHORT, Locale.getDefault());

    @Nonnull
    private final FileEntity owner;

    public FileEntityPresentable (@Nonnull final FileEntity owner)
      {
        super(owner);
        this.owner = owner;
      }

    @Override @Nonnull
    public PresentationModel createPresentationModel (@Nonnull final Collection<Object> roles)
      {
        final var aggregate = PresentationModelAggregate.newInstance()
           .withPmOf("name",
                     r(owner))  // owner is a Displayable itself
           .withPmOf("size",
                     r(Displayable.of(_s(() -> "" + owner.getSize())),
                       Styleable.of("right-aligned")))
           .withPmOf("creationDate",
                     r(Displayable.of(_s(() -> FORMATTER.format(owner.getCreationDateTime()))),
                       Styleable.of("right-aligned")))
           .withPmOf("latestModificationDate",
                     r(Displayable.of(_s(() -> FORMATTER.format(owner.getLastModifiedDateTime()))),
                       Styleable.of("right-aligned")));
        return super.createPresentationModel(r(aggregate, roles));
      }
  }
// END SNIPPET: all
