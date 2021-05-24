/*
 * Activity Sampling - Frontend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.frontend;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

public class TagsStringConverterTests {
  @Test
  void todoConvertToString() {
    var converter = new TagsStringConverter();

    assertEquals("", converter.toString(List.of()));
    assertEquals("Foo", converter.toString(List.of("Foo")));
    assertEquals("Foo, Bar", converter.toString(List.of("Foo", "Bar")));
  }

  @Test
  void todoConvertFromString() {
    var converter = new TagsStringConverter();

    assertEquals(Collections.emptyList(), converter.fromString(""));
    assertEquals(List.of("Foo"), converter.fromString("Foo"));
    assertEquals(List.of("Foo", "Bar"), converter.fromString("Foo, Bar"));
  }
}
