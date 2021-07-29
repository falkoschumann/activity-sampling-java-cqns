/*
 * Activity Sampling - Frontend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.frontend;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import de.muspellheim.activitysampling.contract.data.ActivityTemplate;
import java.util.List;
import org.junit.jupiter.api.Test;

class ActivityStringConverterTests {
  @Test
  void testToString() {
    var converter = new ActivityTemplateStringConverter();

    assertAll(
        () ->
            assertEquals(
                "Lorem ipsum",
                converter.toString(new ActivityTemplate("Lorem ipsum")),
                "without tag"),
        () ->
            assertEquals(
                "[Foo] Lorem ipsum",
                converter.toString(new ActivityTemplate("Lorem ipsum", List.of("Foo"))),
                "with one tag"),
        () ->
            assertEquals(
                "[Foo, Bar] Lorem ipsum",
                converter.toString(new ActivityTemplate("Lorem ipsum", List.of("Foo", "Bar"))),
                "with multiple tags"));
  }

  @Test
  void testFRomString() {
    var converter = new ActivityTemplateStringConverter();

    assertAll(
        () ->
            assertEquals(
                new ActivityTemplate("Lorem ipsum"),
                converter.fromString("Lorem ipsum"),
                "without tag"),
        () ->
            assertEquals(
                new ActivityTemplate("Lorem ipsum", List.of("Foo")),
                converter.fromString("[Foo] Lorem ipsum"),
                "with one tag"),
        () ->
            assertEquals(
                new ActivityTemplate("Lorem ipsum", List.of("Foo", "Bar")),
                converter.fromString("[Foo, Bar] Lorem ipsum"),
                "with multiple tags"));
  }
}
