/*
 * *********************************************************************************************************************
 *
 * SteelBlue: DCI User Interfaces
 * http://tidalwave.it/projects/steelblue
 *
 * Copyright (C) 2015 - 2024 by Tidalwave s.a.s. (http://tidalwave.it)
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
package it.tidalwave.role.ui.example.presentation.impl;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;
import it.tidalwave.role.Aggregate;
import it.tidalwave.role.ui.PresentationModel;
import it.tidalwave.role.ui.Selectable;
import it.tidalwave.role.ui.example.model.FileEntity;
import org.testng.annotations.Test;
import static it.tidalwave.role.Aggregate._Aggregate_;
import static it.tidalwave.role.ui.Displayable._Displayable_;
import static it.tidalwave.role.ui.Selectable._Selectable_;
import static it.tidalwave.role.ui.Styleable._Styleable_;
import static org.mockito.Mockito.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
public class FileEntityPresentableTest
  {
    @Test
    public void test()
      throws Exception
      {
        // given
        final var creationDateTime = ZonedDateTime.of(2024, 10, 28, 20, 34, 26, 0, ZoneId.systemDefault());
        final var lastModifiedDateTime = ZonedDateTime.of(2024, 12, 8, 16, 52, 37, 0, ZoneId.systemDefault());
        final var fileEntity = mock(FileEntity.class);
        when(fileEntity.getDisplayName()).thenReturn("file.txt");
        when(fileEntity.getSize()).thenReturn(12345678L);
        when(fileEntity.getCreationDateTime()).thenReturn(creationDateTime);
        when(fileEntity.getLastModifiedDateTime()).thenReturn(lastModifiedDateTime);
        final var extraRole = mock(Selectable.class);
        // when
        final var underTest = new FileEntityPresentable(fileEntity);
        // then
        final var pm = underTest.createPresentationModel(extraRole);
        final Aggregate<PresentationModel> aggregate = pm.as(_Aggregate_);
        assertThat(aggregate.getNames(), is(Set.of("name", "size", "latestModificationDate", "creationDate")));
        final var pmName = aggregate.getByName("name").orElseThrow();
        assertThat(pmName.as(_Displayable_).getDisplayName(), is("file.txt"));
        final var pmSize = aggregate.getByName("size").orElseThrow();
        assertThat(pmSize.as(_Displayable_).getDisplayName(), is("12345678"));
        assertThat(pmSize.as(_Styleable_).getStyles(), is(List.of("right-aligned")));
        final var pmCDT = aggregate.getByName("creationDate").orElseThrow();
        assertThat(pmCDT.as(_Displayable_).getDisplayName(), is("10/28/24 8:34 pm"));
        final var pmLMDT = aggregate.getByName("latestModificationDate").orElseThrow();
        assertThat(pmLMDT.as(_Displayable_).getDisplayName(), is("12/8/24 4:52 pm"));
        assertThat(pm.as(_Selectable_), is(extraRole));
      }
  }
