/*
 * Activity Sampling - Frontend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.frontend;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class ActivitySamplingViewModelTests {
  @Test
  void periodStarted() {
    var messageHandling = new TestingMessageHandling();
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
    var messageHandling = new TestingMessageHandling();
    var viewModel = new ActivitySamplingViewModel(messageHandling);
    viewModel.loadPreferences();
    var startTime = LocalDateTime.of(2020, 11, 8, 17, 20);
    viewModel.clockTicked(startTime);

    var currentTime = LocalDateTime.of(2020, 11, 8, 17, 31, 45);
    viewModel.clockTicked(currentTime);

    assertAll(
        () -> assertTrue(viewModel.formDisabledProperty().get(), "form disabled"),
        () -> assertEquals("08:15", viewModel.remainingTimeProperty().get(), "remaining time"),
        () -> assertEquals(0.4125, viewModel.progressProperty().get(), "progress"));
  }

  @Test
  void periodEnded() {
    var messageHandling = new TestingMessageHandling();
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
  @Disabled("Not implemented yet")
  void activityLogged() {
    fail("Not implemented yet");
  }
}
