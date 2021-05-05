/*
 * Activity Sampling - Frontend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.frontend;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.muspellheim.activitysampling.contract.data.Activity;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;

class ActivitySamplingViewModelTests {
  private static final Activity ACTIVITY_1 =
      new Activity(
          "a7caf1b0-886e-406f-8fbc-71da9f34714e",
          LocalDateTime.of(2020, 12, 30, 17, 52),
          Duration.ofMinutes(20),
          "A",
          List.of("Foo", "Bar"));
  private static final Activity ACTIVITY_2 =
      new Activity(
          "d5abc0dd-60b0-4a3b-9b2f-8b02005fb256",
          LocalDateTime.of(2020, 12, 30, 21, 20),
          Duration.ofMinutes(20),
          "B");
  private static final Activity ACTIVITY_3 =
      new Activity(
          "e9ed7915-8109-402d-b9e6-2d5764ef688d",
          LocalDateTime.of(2021, 1, 4, 13, 52),
          Duration.ofMinutes(20),
          "B");
  private static final Activity ACTIVITY_4 =
      new Activity(
          "d36a20db-56ae-48af-9221-0630911cdb8d",
          LocalDateTime.of(2021, 1, 4, 14, 20),
          Duration.ofMinutes(20),
          "A",
          List.of("Foo", "Bar"));
  /*
    @BeforeEach
    void setUp() {
      Locale.setDefault(Locale.GERMANY);

      messageHandling = mock(MessageHandling.class);
      when(messageHandling.handle(new SettingsQuery()))
          .thenReturn(
              new SettingsQueryResult(Duration.ofMinutes(20), Paths.get("~/activity-log.csv")));

      when(messageHandling.handle(new ActivityLogQuery()))
          .thenReturn(
              new ActivityLogQueryResult(
                  List.of(ACTIVITY_1, ACTIVITY_2, ACTIVITY_3, ACTIVITY_4),
                  List.of(ACTIVITY_4, ACTIVITY_3)));
    }
  */
  @Test
  void initialState() {
    var viewModel = new ActivitySamplingViewModel();

    assertAll(
        () -> assertTrue(viewModel.formDisabledProperty().get(), "form disabled"),
        () -> assertEquals("", viewModel.activityProperty().get(), "activity"),
        () -> assertEquals("", viewModel.tagsProperty().get(), "tags"),
        () -> assertEquals(List.of(), viewModel.getRecentActivities(), "recent activities"),
        () -> assertEquals("20:00", viewModel.remainingTimeProperty().get(), "remaining time"),
        () -> assertEquals(0.0, viewModel.progressProperty().get(), "progress"),
        () -> assertNull(viewModel.periodDurationProperty().get(), "period duration"),
        () -> assertEquals("", viewModel.activityLogProperty().get(), "activity log"),
        () -> assertNull(viewModel.activityLogFileProperty().get(), "activity log file"));
  }
  /*
    @Test
    void reloadActivityLog() {
      var viewModel = new ActivitySamplingViewModel(messageHandling);
      viewModel.loadActivityLog();

      assertAll(
          () -> assertEquals("A", viewModel.activityProperty().get(), "activity"),
          () -> assertEquals("Foo, Bar", viewModel.tagsProperty().get(), "tags"),
          () ->
              assertEquals(
                  List.of("[Foo, Bar] A", "B"), viewModel.getRecentActivities(), "recent activities"),
          () ->
              assertEquals(
                  "Mittwoch, 30. Dezember 2020\n"
                      + "17:52 - [Foo, Bar] A\n"
                      + "21:20 - B\n"
                      + "Montag, 4. Januar 2021\n"
                      + "13:52 - B\n"
                      + "14:20 - [Foo, Bar] A\n",
                  viewModel.activityLogProperty().get(),
                  "activity log"));
    }

    @Test
    void periodStarted() {
      var viewModel = new ActivitySamplingViewModel(messageHandling);
      viewModel.loadPreferences();

      var currentTime = LocalDateTime.of(2020, 11, 8, 17, 20);
      viewModel.clockTicked(currentTime);

      assertAll(
          () -> assertTrue(viewModel.formDisabledProperty().get(), "form disabled"),
          () -> assertEquals("20:00", viewModel.remainingTimeProperty().get(), "remaining time"),
          () -> assertEquals(0.0, viewModel.progressProperty().get(), "progress"));
    }

    @Test
    void periodProgressed() {
      var viewModel = new ActivitySamplingViewModel(messageHandling);
      viewModel.loadPreferences();
      var startTime = LocalDateTime.of(2020, 11, 8, 17, 20);
      viewModel.clockTicked(startTime);

      var currentTime = LocalDateTime.of(2020, 11, 8, 17, 31, 45);
      viewModel.clockTicked(currentTime);

      assertAll(
          () -> assertTrue(viewModel.formDisabledProperty().get(), "form disabled"),
          () -> assertEquals("08:15", viewModel.remainingTimeProperty().get(), "remaining time"),
          () -> assertEquals(0.5875, viewModel.progressProperty().get(), "progress"));
    }

    @Test
    void periodEnded() {
      var viewModel = new ActivitySamplingViewModel(messageHandling);
      viewModel.loadPreferences();
      var startTime = LocalDateTime.of(2020, 11, 8, 17, 20);
      viewModel.clockTicked(startTime);

      var currentTime = LocalDateTime.of(2020, 11, 8, 17, 40);
      viewModel.clockTicked(currentTime);

      assertAll(
          () -> assertFalse(viewModel.formDisabledProperty().get(), "form disabled"),
          () -> assertEquals("00:00", viewModel.remainingTimeProperty().get(), "remaining time"),
          () -> assertEquals(1.0, viewModel.progressProperty().get(), "progress"));
    }

    @Test
    void activityLogged() {
      var viewModel = new ActivitySamplingViewModel(messageHandling);
      var newActivity =
          new Activity(
              "xxx",
              LocalDateTime.of(2021, 2, 8, 17, 40),
              Duration.ofMinutes(20),
              "Rule the world",
              List.of("Foo"));
      when(messageHandling.handle(new ActivityLogQuery()))
          .thenReturn(
              new ActivityLogQueryResult(
                  List.of(ACTIVITY_1, ACTIVITY_2, ACTIVITY_3, ACTIVITY_4, newActivity),
                  List.of(newActivity, ACTIVITY_4, ACTIVITY_3)));
      viewModel.loadPreferences();
      var startTime = LocalDateTime.of(2021, 2, 8, 17, 20);
      viewModel.clockTicked(startTime);
      var endTime = LocalDateTime.of(2021, 2, 8, 17, 40);
      viewModel.clockTicked(endTime);

      viewModel.activityProperty().set("Rule the world");
      viewModel.tagsProperty().set("Foo");
      viewModel.logActivity();

      assertAll(
          () -> assertTrue(viewModel.formDisabledProperty().get(), "form disabled"),
          () ->
              verify(messageHandling)
                  .handle(
                      new LogActivityCommand(
                          endTime, Duration.ofMinutes(20), "Rule the world", List.of("Foo"))),
          () ->
              assertEquals(
                  List.of("[Foo] Rule the world", "[Foo, Bar] A", "B"),
                  viewModel.getRecentActivities(),
                  "recent activities"),
          () ->
              assertEquals(
                  "Mittwoch, 30. Dezember 2020\n"
                      + "17:52 - [Foo, Bar] A\n"
                      + "21:20 - B\n"
                      + "Montag, 4. Januar 2021\n"
                      + "13:52 - B\n"
                      + "14:20 - [Foo, Bar] A\n"
                      + "Montag, 8. Februar 2021\n"
                      + "17:40 - [Foo] Rule the world\n",
                  viewModel.activityLogProperty().get(),
                  "activity log"));
    }
  */
}
