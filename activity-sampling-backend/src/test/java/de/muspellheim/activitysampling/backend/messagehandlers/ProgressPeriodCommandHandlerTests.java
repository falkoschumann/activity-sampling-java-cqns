/*
 * Activity Sampling - Backend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.backend.messagehandlers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import de.muspellheim.activitysampling.contract.messages.commands.ProgressPeriodCommand;
import de.muspellheim.activitysampling.contract.messages.notification.PeriodProgressedNotification;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import org.junit.jupiter.api.Test;

class ProgressPeriodCommandHandlerTests {
  private PeriodProgressedNotification periodProgressedNotification;

  @Test
  void clockTicked_periodStarted() {
    var handler = new ProgressPeriodCommandHandler(Duration.ofMinutes(20));
    handler.setOnPeriodProgressedNotification(n -> periodProgressedNotification = n);

    var currentTime = LocalDateTime.of(2020, 11, 8, 17, 20);
    handler.handle(new ProgressPeriodCommand(currentTime));

    assertEquals(
        new PeriodProgressedNotification(LocalTime.of(0, 20), 0.0, null),
        periodProgressedNotification);
  }

  @Test
  void clockTicked_periodProgressed() {
    var handler = new ProgressPeriodCommandHandler(Duration.ofMinutes(20));
    handler.setOnPeriodProgressedNotification(n -> periodProgressedNotification = n);
    var startTime = LocalDateTime.of(2020, 11, 8, 17, 20);
    handler.handle(new ProgressPeriodCommand(startTime));

    var currentTime = LocalDateTime.of(2020, 11, 8, 17, 31, 45);
    handler.handle(new ProgressPeriodCommand(currentTime));

    assertEquals(
        new PeriodProgressedNotification(LocalTime.of(0, 8, 15), 0.5875, null),
        periodProgressedNotification);
  }

  @Test
  void clockTicked_periodEnded() {
    var handler = new ProgressPeriodCommandHandler(Duration.ofMinutes(20));
    handler.setOnPeriodProgressedNotification(n -> periodProgressedNotification = n);
    var startTime = LocalDateTime.of(2020, 11, 8, 17, 20);
    handler.handle(new ProgressPeriodCommand(startTime));

    var currentTime = LocalDateTime.of(2020, 11, 8, 17, 40);
    handler.handle(new ProgressPeriodCommand(currentTime));

    assertEquals(
        new PeriodProgressedNotification(
            LocalTime.of(0, 0), 1.0, LocalDateTime.of(2020, 11, 8, 17, 40)),
        periodProgressedNotification);
  }
}