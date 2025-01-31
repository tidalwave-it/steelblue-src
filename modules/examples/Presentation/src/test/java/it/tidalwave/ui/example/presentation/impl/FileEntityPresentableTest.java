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
package it.tidalwave.ui.example.presentation.impl;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;
import it.tidalwave.ui.core.role.Selectable;
import it.tidalwave.ui.example.model.FileEntity;
import org.testng.annotations.Test;
import static it.tidalwave.ui.core.role.PresentationModel.*;
import static it.tidalwave.ui.core.role.Selectable._Selectable_;
import static it.tidalwave.ui.test.AsAssert.assertThat;
import static it.tidalwave.util.LocalizedDateTimeFormatters.getDateTimeFormatterFor;
import static org.mockito.Mockito.*;

/***************************************************************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **************************************************************************************************************************************************************/
public class FileEntityPresentableTest
  {
    private static final DateTimeFormatter FORMATTER = getDateTimeFormatterFor(FormatStyle.SHORT, Locale.getDefault());

    @Test
    public void test()
      throws Exception
      {
        // given
        final var creationDateTime = ZonedDateTime.of(2025, 10, 28, 20, 34, 26, 0, ZoneId.systemDefault());
        final var lastModifiedDateTime = ZonedDateTime.of(2025, 12, 8, 16, 52, 37, 0, ZoneId.systemDefault());
        final var fileEntity = mock(FileEntity.class);
        when(fileEntity.getDisplayName()).thenReturn("file.txt");
        when(fileEntity.getSize()).thenReturn(12345678L);
        when(fileEntity.getCreationDateTime()).thenReturn(creationDateTime);
        when(fileEntity.getLastModifiedDateTime()).thenReturn(lastModifiedDateTime);
        final var extraRole = mock(Selectable.class);
        final var underTest = new FileEntityPresentable(fileEntity);
        // when
        final var pm = underTest.createPresentationModel(extraRole);
        // then
        assertThat(pm).hasRole(_Selectable_).isSameAs(extraRole);
        assertThat(pm).hasAggregateOf(_PresentationModel_)
                      .withExactItemNames("name", "size", "latestModificationDate", "creationDate")
                      .withNextItemSatisfying(apm -> apm.hasDisplayName("file.txt"))
                      .withNextItemSatisfying(apm -> apm.hasDisplayName("12345678")
                                                                    .hasStyleable().withExactStyles("right-aligned"))
                      .withNextItemSatisfying(apm -> apm.hasDisplayName(FORMATTER.format(lastModifiedDateTime)))
                      .withNextItemSatisfying(apm -> apm.hasDisplayName(FORMATTER.format(creationDateTime)));
      }
  }
