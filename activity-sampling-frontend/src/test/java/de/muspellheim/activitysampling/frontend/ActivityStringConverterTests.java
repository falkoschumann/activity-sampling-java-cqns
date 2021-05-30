/*
 * Activity Sampling - Frontend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.frontend;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import de.muspellheim.activitysampling.contract.data.Activity;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;

public class ActivityStringConverterTests {
  @Test
  void activityToString() {
    var converter = new ActivityStringConverter();

    assertEquals("Lorem ipsum", converter.toString(createB()));
  }

  @Test
  void activityToString_tags() {
    var converter = new ActivityStringConverter();

    assertEquals("[Foo, Bar] Lorem ipsum", converter.toString(createA()));
  }

  @Test
  void activityFromString() {
    var converter = new ActivityStringConverter();

    var activity = converter.fromString("Lorem ipsum");

    assertAll(
        () -> assertEquals("Lorem ipsum", activity.activity()),
        () -> assertEquals(List.of(), activity.tags()));
  }

  @Test
  void activityFromString_tags() {
    var converter = new ActivityStringConverter();

    var activity = converter.fromString("[Foo, Bar] Lorem ipsum");

    assertAll(
        () -> assertEquals("Lorem ipsum", activity.activity()),
        () -> assertEquals(List.of("Foo", "Bar"), activity.tags()));
  }

  private static Activity createA() {
    return new Activity(
        "d36a20db-56ae-48af-9221-0630911cdb8d",
        LocalDateTime.of(2021, 1, 4, 14, 20),
        Duration.ofMinutes(20),
        "Lorem ipsum",
        List.of("Foo", "Bar"));
  }

  private static Activity createB() {
    return new Activity(
        "e9ed7915-8109-402d-b9e6-2d5764ef688d",
        LocalDateTime.of(2021, 1, 4, 13, 52),
        Duration.ofMinutes(20),
        "Lorem ipsum");
  }
}
