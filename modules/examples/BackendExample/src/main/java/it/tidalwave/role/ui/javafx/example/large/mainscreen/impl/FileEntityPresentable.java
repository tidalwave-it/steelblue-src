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
package it.tidalwave.role.ui.javafx.example.large.mainscreen.impl;

import javax.annotation.Nonnull;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Collection;
import java.io.IOException;
import java.util.Locale;
import it.tidalwave.dci.annotation.DciRole;
import it.tidalwave.role.Aggregate;
import it.tidalwave.role.ui.Displayable;
import it.tidalwave.role.ui.PresentationModel;
import it.tidalwave.role.ui.PresentationModelAggregate;
import it.tidalwave.role.ui.Styleable;
import it.tidalwave.role.ui.javafx.example.large.data.impl.FileEntity;
import it.tidalwave.role.ui.spi.SimpleCompositePresentable;
import it.tidalwave.util.LocalizedDateTimeFormatters;
import lombok.extern.slf4j.Slf4j;
import static it.tidalwave.util.Parameters.r;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
@DciRole(datumType = FileEntity.class) @Slf4j
public class FileEntityPresentable extends SimpleCompositePresentable<FileEntity>
  {
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
        try
          {
            // TODO: add roles such as Removable, present only if permissions allow
            // TODO: iconprovider
            final DateTimeFormatter formatter =
                    LocalizedDateTimeFormatters.getDateTimeFormatterFor(FormatStyle.SHORT, Locale.getDefault());
            final Aggregate<PresentationModel> aggregate = PresentationModelAggregate.newInstance()
               .withPmOf("name",
                         r(Displayable.of(owner.getDisplayName())))
               .withPmOf("size",
                         r(Displayable.of("" + owner.getSize()), Styleable.of("right-aligned")))
               .withPmOf("creationDate",
                         r(Displayable.of(formatter.format(owner.getCreationDateTime()))))
               .withPmOf("latestModificationDate",
                         r(Displayable.of(formatter.format(owner.getLastModifiedDateTime()))));
            return super.createPresentationModel(r(aggregate, roles));
          }
        catch (IOException e)
          {
            log.error("While creating a PresentationModel: ", e);
            return PresentationModel.of(owner, Displayable.of(e.toString()));
          }
//        final Selectable selectable = () -> onSelected(entity);

      }
  }
