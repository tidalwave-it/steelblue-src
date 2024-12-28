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
package it.tidalwave.ui.example.model;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.time.ZoneId;
import java.time.ZonedDateTime;
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
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.*;

/***************************************************************************************************************************************************************
 *
 * A class that models a file with its attributes and children.
 *
 * @author  Fabrizio Giudici
 *
 **************************************************************************************************************************************************************/
@Immutable @EqualsAndHashCode @ToString @Slf4j
public class FileEntity implements As, Displayable
  {
    @RequiredArgsConstructor
    private static class FileEntityFinder extends SimpleFinderSupport<FileEntity>
      {
        private static final long serialVersionUID = 5780394869213L;

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

    /** The path of the file.*/
    @Nonnull
    private final Path path;

    /** Support object for implementing {@link As} functions.*/
    @Delegate
    private final As delegate;

    /***********************************************************************************************************************************************************
     * Creates a new instance related to the given path.
     * @param   path    the path
     **********************************************************************************************************************************************************/
    // START SNIPPET: constructor
    private FileEntity (@Nonnull final Path path)
      {
        this.path = path;
        delegate = As.forObject(this, Files.isDirectory(path) ? List.of(SimpleComposite.of(new FileEntityFinder(path))) : emptyList());
      }
    // END SNIPPET: constructor

    /***********************************************************************************************************************************************************
     * {@return a new instance} related to the given path.
     * @param   path    the path
     **********************************************************************************************************************************************************/
    // START SNIPPET: constructor
    @Nonnull
    public static FileEntity of (@Nonnull final Path path)
      {
        return new FileEntity(path);
      }
    // END SNIPPET: constructor

    /***********************************************************************************************************************************************************
     * {@return the display name}. It's a static implementation of the {@link Displayable} role.
     **********************************************************************************************************************************************************/
    @Override @Nonnull
    public String getDisplayName()
      {
        return Optional.ofNullable(path.getFileName()).map(Path::toString).orElse("/");
      }

    /***********************************************************************************************************************************************************
     * {@return the creation date-time} of the file.
     * @throws  IOException if the file does not exist or cannot be accessed
     **********************************************************************************************************************************************************/
    @Nonnull
    public ZonedDateTime getCreationDateTime()
            throws IOException
      {
        return toZoneDateTime(getBasicFileAttributes().creationTime());
      }

    /***********************************************************************************************************************************************************
     * {@return the last access date-time} of the file.
     * @throws  IOException if the file does not exist or cannot be accessed
     **********************************************************************************************************************************************************/
    @Nonnull
    public ZonedDateTime getLastAccessDateTime()
            throws IOException
      {
        return toZoneDateTime(getBasicFileAttributes().lastAccessTime());
      }

    /***********************************************************************************************************************************************************
     * {@return the last modified date-time} of the file.
     * @throws  IOException if the file does not exist or cannot be accessed
     **********************************************************************************************************************************************************/
    @Nonnull
    public ZonedDateTime getLastModifiedDateTime()
            throws IOException
      {
        return toZoneDateTime(getBasicFileAttributes().lastModifiedTime());
      }

    /***********************************************************************************************************************************************************
     * {@return the size} of the file.
     * @throws  IOException if the file does not exist or cannot be accessed
     **********************************************************************************************************************************************************/
    @Nonnegative
    public long getSize()
            throws IOException
      {
        return Files.size(path);
      }

    /***********************************************************************************************************************************************************
     * {@return the basic attributes} of the file.
     * @throws  IOException if the file does not exist or cannot be accessed
     **********************************************************************************************************************************************************/
    @Nonnull
    private BasicFileAttributes getBasicFileAttributes()
            throws IOException
      {
        return Files.getFileAttributeView(path, BasicFileAttributeView.class).readAttributes();
      }

    /***********************************************************************************************************************************************************
     *
     **********************************************************************************************************************************************************/
    @Nonnull
    private static ZonedDateTime toZoneDateTime (@Nonnull final FileTime dateTime)
      {
        return ZonedDateTime.ofInstant(dateTime.toInstant(), ZoneId.systemDefault());
      }
  }
