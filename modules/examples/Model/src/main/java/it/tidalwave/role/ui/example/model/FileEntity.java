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
package it.tidalwave.role.ui.example.model;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import it.tidalwave.util.As;
import it.tidalwave.util.spi.SimpleFinderSupport;
import it.tidalwave.role.SimpleComposite;
import it.tidalwave.role.ui.Displayable;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.Delegate;
import lombok.extern.slf4j.Slf4j;
import static java.util.stream.Collectors.*;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
@Immutable @EqualsAndHashCode @ToString @Slf4j
public class FileEntity implements As, Displayable
  {
    @RequiredArgsConstructor
    public static class FileEntityFinder extends SimpleFinderSupport<FileEntity>
      {
        @Nonnull
        private final Path path;

        public FileEntityFinder (@Nonnull final FileEntityFinder other, @Nonnull final Object override)
          {
            super(other, override);
            final var source = getSource(FileEntityFinder.class, other, override);
            this.path = source.path;
          }

        @Nonnull @Override
        protected List<FileEntity> computeNeededResults()
          {
            try (final var stream = Files.list(path))
              {
                return stream.sorted(Comparator.comparing(Path::toString)).map(FileEntity::of).collect(toList());
              }
            catch (IOException e)
              {
                log.error("While listing directory " + path, e);
                throw new UncheckedIOException(e);
              }
          }
      }

    @Nonnull
    private final Path path;

    @Delegate
    private final As delegate;

    // START SNIPPET: constructor
    private FileEntity (@Nonnull final Path path)
      {
        this.path = path;
        delegate = As.forObject(this, Files.isDirectory(path)
                                                ? List.of(SimpleComposite.of(new FileEntityFinder(path)))
                                                : Collections.emptyList());
      }
    // END SNIPPET: constructor

    @Nonnull
    public static FileEntity of (@Nonnull final Path path)
      {
        return new FileEntity(path);
      }

    @Override @Nonnull
    public String getDisplayName()
      {
        return Optional.ofNullable(path.getFileName()).map(Path::toString).orElse("/");
      }

    @Nonnull
    public ZonedDateTime getCreationDateTime()
            throws IOException
      {
        return toZoneDateTime(getBasicFileAttributes().creationTime());
      }

    @Nonnull
    public ZonedDateTime getLastAccessDateTime()
            throws IOException
      {
        return toZoneDateTime(getBasicFileAttributes().lastAccessTime());
      }

    @Nonnull
    public ZonedDateTime getLastModifiedDateTime()
            throws IOException
      {
        return toZoneDateTime(getBasicFileAttributes().lastModifiedTime());
      }

    @Nonnegative
    public long getSize()
            throws IOException
      {
        return Files.size(path);
      }

    @Nonnull
    private BasicFileAttributes getBasicFileAttributes()
            throws IOException
      {
        return Files.getFileAttributeView(path, BasicFileAttributeView.class).readAttributes();
      }

    @Nonnull
    private static ZonedDateTime toZoneDateTime (@Nonnull final FileTime dateTime)
      {
        return ZonedDateTime.ofInstant(dateTime.toInstant(), ZoneId.systemDefault());
      }
  }
