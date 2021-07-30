/*
 * Activity Sampling - Frontend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.frontend;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.muspellheim.activitysampling.contract.data.Activity;
import de.muspellheim.activitysampling.contract.data.ActivityTemplate;
import de.muspellheim.activitysampling.contract.messages.queries.ActivityLogQueryResult;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MainWindowModelTests {
  private MainWindowModel fixture;

  @BeforeEach
  void init() {
    fixture = new MainWindowModel();
    fixture.setOnPeriodEnded(() -> {});
  }

  @Test
  void testClockTicked_periodStarted() {
    fixture.setPeriodDuration(Duration.ofMinutes(20));

    var currentTime = LocalDateTime.of(2020, 11, 8, 17, 20);
    fixture.progressPeriod(currentTime);

    assertAll(
        () -> assertEquals(Duration.ofMinutes(20), fixture.getRemainingTime(), "remainingTime"),
        () -> assertNull(fixture.getPeriodEnd(), "periodEnd"),
        () -> assertEquals(0.0, fixture.periodProgressBinding().get(), "periodProgress"),
        () -> assertTrue(fixture.isFormDisabled(), "formDisabled"),
        () -> assertTrue(fixture.logButtonDisabled.get(), "formUnsubmittable"));
  }

  @Test
  void testClockTicked_periodProgressed() {
    fixture.setPeriodDuration(Duration.ofMinutes(20));
    var startTime = LocalDateTime.of(2020, 11, 8, 17, 20);
    fixture.progressPeriod(startTime);

    var currentTime = LocalDateTime.of(2020, 11, 8, 17, 31, 45);
    fixture.progressPeriod(currentTime);

    assertAll(
        () ->
            assertEquals(
                Duration.ofMinutes(8).plusSeconds(15), fixture.getRemainingTime(), "remainingTime"),
        () -> assertNull(fixture.getPeriodEnd(), "periodEnd"),
        () -> assertEquals(0.5875, fixture.periodProgressBinding().get(), "periodProgress"),
        () -> assertTrue(fixture.isFormDisabled(), "formDisabled"),
        () -> assertTrue(fixture.logButtonDisabled.get(), "formUnsubmittable"));
  }

  @Test
  void testClockTicked_periodEnded() {
    fixture.setPeriodDuration(Duration.ofMinutes(20));
    var startTime = LocalDateTime.of(2020, 11, 8, 17, 20);
    fixture.progressPeriod(startTime);

    var currentTime = LocalDateTime.of(2020, 11, 8, 17, 40);
    fixture.progressPeriod(currentTime);

    assertAll(
        () -> assertEquals(Duration.ZERO, fixture.getRemainingTime(), "remainingTime"),
        () -> assertEquals(currentTime, fixture.getPeriodEnd(), "periodEnd"),
        () -> assertEquals(1.0, fixture.periodProgressBinding().get(), "periodProgress"),
        () -> assertFalse(fixture.isFormDisabled(), "formDisabled"),
        () -> assertTrue(fixture.logButtonDisabled.get(), "formUnsubmittable"));
  }

  @Test
  void testUpdateWith() {
    Locale.setDefault(Locale.GERMANY);
    fixture.updateWith(
        new ActivityLogQueryResult(
            List.of(
                new Activity(
                    "a7caf1b0-886e-406f-8fbc-71da9f34714e",
                    LocalDateTime.of(2020, 12, 30, 17, 52),
                    Duration.ofMinutes(20),
                    "A",
                    List.of("Foo", "Bar")),
                new Activity(
                    "d5abc0dd-60b0-4a3b-9b2f-8b02005fb256",
                    LocalDateTime.of(2020, 12, 30, 21, 20),
                    Duration.ofMinutes(20),
                    "B"),
                new Activity(
                    "e9ed7915-8109-402d-b9e6-2d5764ef688d",
                    LocalDateTime.of(2021, 1, 4, 13, 52),
                    Duration.ofMinutes(20),
                    "B"),
                new Activity(
                    "d36a20db-56ae-48af-9221-0630911cdb8d",
                    LocalDateTime.of(2021, 1, 4, 14, 20),
                    Duration.ofMinutes(20),
                    "A",
                    List.of("Foo", "Bar"))),
            List.of(new ActivityTemplate("A", List.of("Foo", "Bar")), new ActivityTemplate("B")),
            new ActivityTemplate("A", List.of("Foo", "Bar")),
            List.of("Bar", "Foo")));

    assertAll(
        () -> assertEquals("A", fixture.getActivity(), "activity"),
        () ->
            assertEquals(
                List.of(
                    new ActivityTemplate("A", List.of("Foo", "Bar")), new ActivityTemplate("B")),
                fixture.getRecentActivities(),
                "recentActivities"),
        () -> assertEquals(List.of("Foo", "Bar"), fixture.getTags(), "tags"),
        () -> assertEquals(List.of("Bar", "Foo"), fixture.getRecentTags(), "recentTags"),
        () ->
            assertEquals(
                """
          Mittwoch, 30. Dezember 2020
          17:52 - [Foo, Bar] A
          21:20 - B
          Montag, 4. Januar 2021
          13:52 - B
          14:20 - [Foo, Bar] A
          """,
                fixture.getLog(),
                "log"));
  }
}
