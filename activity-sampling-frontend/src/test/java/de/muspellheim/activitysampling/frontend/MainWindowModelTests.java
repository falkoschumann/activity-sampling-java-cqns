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
import de.muspellheim.activitysampling.contract.messages.commands.LogActivityCommand;
import de.muspellheim.activitysampling.contract.messages.queries.ActivityLogQueryResult;
import de.muspellheim.activitysampling.contract.messages.queries.PreferencesQueryResult;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Locale;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MainWindowModelTests {
  private MainWindowModel model;
  private boolean periodEnded;
  private LogActivityCommand logActivityCommand;

  @BeforeEach
  void init() {
    model = new MainWindowModel();
    model.setOnPeriodEnded(() -> periodEnded = true);
    model.setOnLogActivityCommand(c -> logActivityCommand = c);
  }

  @Test
  void testClockTicked_periodStarted() {
    model.display(new PreferencesQueryResult(Duration.ofMinutes(20)));

    var currentTime = LocalDateTime.of(2020, 11, 8, 17, 20);
    model.progressPeriod(currentTime);

    assertAll(
        () -> assertEquals("", model.getActivity(), "activity"),
        () -> assertEquals(List.of(), model.getRecentActivities(), "recentActivities"),
        () -> assertEquals(List.of(), model.getTags(), "tags"),
        () -> assertEquals(List.of(), model.getRecentTags(), "recentTags"),
        () -> assertTrue(model.isFormDisabled(), "formDisabled"),
        () -> assertTrue(model.isAddTagButtonDisabled(), "addTagButtonDisabled"),
        () -> assertTrue(model.isLogButtonDisabled(), "logButtonDisabled"),
        () -> assertFalse(model.isTrayIconVisible(), "trayIconVisible"),
        () -> assertEquals(LocalTime.of(0, 20), model.getRemainingTime(), "remainingTime"),
        () -> assertEquals(0.0, model.getPeriodProgress(), "periodProgress"),
        () -> assertEquals("", model.getLog(), "log"),
        () -> assertFalse(periodEnded, "periodEnded"),
        () -> assertNull(logActivityCommand, "onLogActivityCommand"));
  }

  @Test
  void testClockTicked_periodProgressed() {
    model.display(new PreferencesQueryResult(Duration.ofMinutes(20)));
    var startTime = LocalDateTime.of(2020, 11, 8, 17, 20);
    model.progressPeriod(startTime);

    var currentTime = LocalDateTime.of(2020, 11, 8, 17, 31, 45);
    model.progressPeriod(currentTime);

    assertAll(
        () -> assertEquals("", model.getActivity(), "activity"),
        () -> assertEquals(List.of(), model.getRecentActivities(), "recentActivities"),
        () -> assertEquals(List.of(), model.getTags(), "tags"),
        () -> assertEquals(List.of(), model.getRecentTags(), "recentTags"),
        () -> assertTrue(model.isFormDisabled(), "formDisabled"),
        () -> assertTrue(model.isAddTagButtonDisabled(), "addTagButtonDisabled"),
        () -> assertTrue(model.isLogButtonDisabled(), "logButtonDisabled"),
        () -> assertFalse(model.isTrayIconVisible(), "trayIconVisible"),
        () -> assertEquals(LocalTime.of(0, 8, 15), model.getRemainingTime(), "remainingTime"),
        () -> assertEquals(0.5875, model.getPeriodProgress(), "periodProgress"),
        () -> assertEquals("", model.getLog(), "log"),
        () -> assertFalse(periodEnded, "periodEnded"),
        () -> assertNull(logActivityCommand, "onLogActivityCommand"));
  }

  @Test
  void testClockTicked_periodEnded() {
    model.display(new PreferencesQueryResult(Duration.ofMinutes(20)));
    var startTime = LocalDateTime.of(2020, 11, 8, 17, 20);
    model.progressPeriod(startTime);

    var currentTime = LocalDateTime.of(2020, 11, 8, 17, 40);
    model.progressPeriod(currentTime);

    assertAll(
        () -> assertEquals("", model.getActivity(), "activity"),
        () -> assertEquals(List.of(), model.getRecentActivities(), "recentActivities"),
        () -> assertEquals(List.of(), model.getTags(), "tags"),
        () -> assertEquals(List.of(), model.getRecentTags(), "recentTags"),
        () -> assertFalse(model.isFormDisabled(), "formDisabled"),
        () -> assertTrue(model.isAddTagButtonDisabled(), "addTagButtonDisabled"),
        () -> assertTrue(model.isLogButtonDisabled(), "logButtonDisabled"),
        () -> assertTrue(model.isTrayIconVisible(), "trayIconVisible"),
        () -> assertEquals(LocalTime.MIN, model.getRemainingTime(), "remainingTime"),
        () -> assertEquals(1.0, model.getPeriodProgress(), "periodProgress"),
        () -> assertEquals("", model.getLog(), "log"),
        () -> assertTrue(periodEnded, "periodEnded"),
        () -> assertNull(logActivityCommand, "onLogActivityCommand"));
  }

  @Test
  void testDisplayActivityLogQueryResult() {
    Locale.setDefault(Locale.GERMANY);

    model.display(
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
        () -> assertEquals("A", model.getActivity(), "activity"),
        () ->
            assertEquals(
                List.of(
                    new ActivityTemplate("A", List.of("Foo", "Bar")), new ActivityTemplate("B")),
                model.getRecentActivities(),
                "recentActivities"),
        () -> assertEquals(List.of("Foo", "Bar"), model.getTags(), "tags"),
        () -> assertEquals(List.of("Bar", "Foo"), model.getRecentTags(), "recentTags"),
        () -> assertTrue(model.isFormDisabled(), "formDisabled"),
        () -> assertTrue(model.isAddTagButtonDisabled(), "addTagButtonDisabled"),
        () -> assertTrue(model.isLogButtonDisabled(), "logButtonDisabled"),
        () -> assertFalse(model.isTrayIconVisible(), "trayIconVisible"),
        () -> assertEquals(LocalTime.of(0, 20), model.getRemainingTime(), "remainingTime"),
        () -> assertEquals(0.0, model.getPeriodProgress(), "periodProgress"),
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
                model.getLog(),
                "log"),
        () -> assertFalse(periodEnded, "periodEnded"),
        () -> assertNull(logActivityCommand, "onLogActivityCommand"));
  }

  @Test
  void testLogActivity() {
    model.display(new PreferencesQueryResult(Duration.ofMinutes(20)));
    var startTime = LocalDateTime.of(2020, 11, 8, 17, 20);
    model.progressPeriod(startTime);
    var currentTime = LocalDateTime.of(2020, 11, 8, 17, 40);
    model.progressPeriod(currentTime);

    model.logActivity(new ActivityTemplate("A", List.of("Foo")));

    assertAll(
        () -> assertEquals("A", model.getActivity(), "activity"),
        () -> assertEquals(List.of(), model.getRecentActivities(), "recentActivities"),
        () -> assertEquals(List.of("Foo"), model.getTags(), "tags"),
        () -> assertEquals(List.of(), model.getRecentTags(), "recentTags"),
        () -> assertTrue(model.isFormDisabled(), "formDisabled"),
        () -> assertTrue(model.isAddTagButtonDisabled(), "addTagButtonDisabled"),
        () -> assertTrue(model.isLogButtonDisabled(), "logButtonDisabled"),
        () -> assertFalse(model.isTrayIconVisible(), "trayIconVisible"),
        () -> assertEquals(LocalTime.MIN, model.getRemainingTime(), "remainingTime"),
        () -> assertEquals(1.0, model.getPeriodProgress(), "periodProgress"),
        () -> assertEquals("", model.getLog(), "log"),
        () -> assertTrue(periodEnded, "periodEnded"),
        () ->
            assertEquals(
                new LogActivityCommand(currentTime, Duration.ofMinutes(20), "A", List.of("Foo")),
                logActivityCommand,
                "onLogActivityCommand"));
  }

  @Test
  void testAddTag() {
    model.display(new PreferencesQueryResult(Duration.ofMinutes(20)));
    var startTime = LocalDateTime.of(2020, 11, 8, 17, 20);
    model.progressPeriod(startTime);
    var currentTime = LocalDateTime.of(2020, 11, 8, 17, 40);
    model.progressPeriod(currentTime);

    model.setActivity("A");
    model.setTags(List.of("Foo"));
    model.addTag("Bar");

    assertAll(
        () -> assertEquals("A", model.getActivity(), "activity"),
        () -> assertEquals(List.of(), model.getRecentActivities(), "recentActivities"),
        () -> assertEquals(List.of("Foo", "Bar"), model.getTags(), "tags"),
        () -> assertEquals(List.of(), model.getRecentTags(), "recentTags"),
        () -> assertFalse(model.isFormDisabled(), "formDisabled"),
        () -> assertTrue(model.isAddTagButtonDisabled(), "addTagButtonDisabled"),
        () -> assertFalse(model.isLogButtonDisabled(), "logButtonDisabled"),
        () -> assertTrue(model.isTrayIconVisible(), "trayIconVisible"),
        () -> assertEquals(LocalTime.MIN, model.getRemainingTime(), "remainingTime"),
        () -> assertEquals(1.0, model.getPeriodProgress(), "periodProgress"),
        () -> assertEquals("", model.getLog(), "log"),
        () -> assertTrue(periodEnded, "periodEnded"),
        () -> assertNull(logActivityCommand, "onLogActivityCommand"));
  }
}
