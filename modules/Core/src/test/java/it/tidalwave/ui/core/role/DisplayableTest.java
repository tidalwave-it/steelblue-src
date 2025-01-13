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
package it.tidalwave.ui.core.role;

import jakarta.annotation.Nonnull;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.nio.file.Path;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import static org.mockito.Mockito.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/***************************************************************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **************************************************************************************************************************************************************/
public class DisplayableTest
  {
    @Test
    public void test_of_String()
      {
        // when
        final var underTest = Displayable.of("foo bar");
        // then
        assertThat(underTest.getDisplayName(), is("foo bar"));
        assertThat(underTest.toString(), Pattern.matches("\\?\\?\\?@.*Displayable\\[\\]", underTest.toString()), is(true));
      }

    /**********************************************************************************************************************************************************/
    @Test
    public void test_of_String_with_toString_name()
      {
        // when
        final var underTest = Displayable.of("foo bar", "[toString]");
        // then
        assertThat(underTest.getDisplayName(), is("foo bar"));
        assertThat(Pattern.matches("\\[toString\\]@.*Displayable\\[\\]", underTest.toString()), is(true));
      }

    /**********************************************************************************************************************************************************/
    @Test
    public void test_of_supplier()
      {
        // when
        final var underTest = Displayable.of(() -> "foo bar");
        // then
        assertThat(underTest.getDisplayName(), is("foo bar"));
        // assertThat(underTest.toString(), is("???"));
      }

    /**********************************************************************************************************************************************************/
    @Test
    public void test_of_function()
      {
        // given
        final var path = Path.of("foo bar");
        // when
        final var underTest = Displayable.of(Path::toString, path);
        // then
        assertThat(underTest.getDisplayName(), is("foo bar"));
        // assertThat(underTest.toString(), is("???"));
      }

    /**********************************************************************************************************************************************************/
    @Test @SuppressWarnings("unchecked")
    public void test_renderTo()
      {
        // given
        final var consumer = mock(Consumer.class);
        final var underTest = Displayable.of("foo bar");
        // when
        underTest.renderTo(consumer);
        // then
        verify(consumer).accept("foo bar");
      }

    /**********************************************************************************************************************************************************/
    @Test
    public void test_render()
      {
        // given
        final var underTest = Displayable.of("foo bar");
        // when
        final var result = underTest.render();
        // then
        assertThat(result, is("foo bar"));
      }

    /**********************************************************************************************************************************************************/
    @Test(dataProvider = "i8n")
    public void must_read_the_displayName_from_the_bundle (@Nonnull final Locale locale, @Nonnull final String expected)
      {
        // given
        final var save = Locale.getDefault();
        Locale.setDefault(locale);
        // when
        final var displayable = Displayable.fromBundle(DisplayableTest.class, "key");
        // then
//        assertThat(displayable.getLocales(), is(new TreeSet<>(List.of(Locale.ENGLISH, Locale.ITALIAN, Locale.FRENCH))));
//        assertThat(displayable.getDisplayNames(), is(Map.of(Locale.ENGLISH, "English",
//                                                            Locale.ITALIAN, "Italian",
//                                                            Locale.FRENCH,  "French")));
        assertThat(displayable.getDisplayName(), is(expected));
        Locale.setDefault(save);
//        assertThat(displayable.getDisplayName(Locale.ITALIAN), is("Italian"));
//        assertThat(displayable.getDisplayName(Locale.FRENCH), is("French"));
      }

    @DataProvider
    private static Object[][] i8n()
      {
        return new Object[][]
          {
            { Locale.ENGLISH, "English"},
            { Locale.ITALIAN, "Italian"},
            { Locale.FRENCH,  "French"}
          };
      }
  }
