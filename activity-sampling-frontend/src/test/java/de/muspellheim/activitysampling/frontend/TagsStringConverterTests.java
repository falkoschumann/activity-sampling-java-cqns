/*
 * Activity Sampling - Frontend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.frontend;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;

class TagsStringConverterTests {
  @Test
  void testToString() {
    var converter = new TagsStringConverter();

    assertAll(
        () -> assertEquals("", converter.toString(List.of()), "without tag"),
        () -> assertEquals("Foo", converter.toString(List.of("Foo")), "with one tag"),
        () ->
            assertEquals(
                "Foo, Bar", converter.toString(List.of("Foo", "Bar")), "with multiple tags"));
  }

  @Test
  void testFromString() {
    var converter = new TagsStringConverter();

    assertAll(
        () -> assertEquals(List.of(), converter.fromString(""), "without tag"),
        () -> assertEquals(List.of("Foo"), converter.fromString("Foo"), "with one tag"),
        () ->
            assertEquals(
                List.of("Foo", "Bar"), converter.fromString("Foo, Bar"), "with multiple tags"));
  }
}
