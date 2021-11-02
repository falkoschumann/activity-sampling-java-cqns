/*
 * Activity Sampling - Backend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.backend.messagehandlers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import de.muspellheim.activitysampling.backend.adapters.MemoryPreferencesRepository;
import de.muspellheim.activitysampling.contract.messages.commands.ProgressPeriodCommand;
import de.muspellheim.activitysampling.contract.messages.notification.PeriodEndedNotification;
import de.muspellheim.activitysampling.contract.messages.notification.PeriodProgressedNotification;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import org.junit.jupiter.api.Test;

class ProgressPeriodCommandHandlerTests {
  private PeriodProgressedNotification periodProgressedNotification;
  private PeriodEndedNotification periodEndedNotification;

  @Test
  void clockTicked_periodStarted() {
    var preferencesRepository = new MemoryPreferencesRepository();
    var handler = new ProgressPeriodCommandHandler(preferencesRepository);
    handler.setOnPeriodProgressedNotification(n -> periodProgressedNotification = n);
    handler.setOnPeriodEndedNotification(n -> periodEndedNotification = n);

    var currentTime = LocalDateTime.of(2020, 11, 8, 17, 20);
    handler.handle(new ProgressPeriodCommand(currentTime));

    assertEquals(
        new PeriodProgressedNotification(LocalTime.of(0, 20), 0.0), periodProgressedNotification);
    assertNull(periodEndedNotification);
  }

  @Test
  void clockTicked_periodProgressed() {
    var preferencesRepository = new MemoryPreferencesRepository();
    var handler = new ProgressPeriodCommandHandler(preferencesRepository);
    handler.setOnPeriodProgressedNotification(n -> periodProgressedNotification = n);
    handler.setOnPeriodEndedNotification(n -> periodEndedNotification = n);
    var startTime = LocalDateTime.of(2020, 11, 8, 17, 20);
    handler.handle(new ProgressPeriodCommand(startTime));

    var currentTime = LocalDateTime.of(2020, 11, 8, 17, 31, 45);
    handler.handle(new ProgressPeriodCommand(currentTime));

    assertEquals(
        new PeriodProgressedNotification(LocalTime.of(0, 8, 15), 0.5875),
        periodProgressedNotification);
    assertNull(periodEndedNotification);
  }

  @Test
  void clockTicked_periodEnded() {
    var preferencesRepository = new MemoryPreferencesRepository();
    var handler = new ProgressPeriodCommandHandler(preferencesRepository);
    handler.setOnPeriodProgressedNotification(n -> periodProgressedNotification = n);
    handler.setOnPeriodEndedNotification(n -> periodEndedNotification = n);
    var startTime = LocalDateTime.of(2020, 11, 8, 17, 20);
    handler.handle(new ProgressPeriodCommand(startTime));

    var currentTime = LocalDateTime.of(2020, 11, 8, 17, 40);
    handler.handle(new ProgressPeriodCommand(currentTime));

    assertEquals(
        new PeriodEndedNotification(LocalDateTime.of(2020, 11, 8, 17, 40), Duration.ofMinutes(20)),
        periodEndedNotification);
  }
}
