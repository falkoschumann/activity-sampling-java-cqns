/*
 * Activity Sampling - Backend
 * Copyright (c) 2020 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.backend.messagehandlers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import de.muspellheim.activitysampling.contract.messages.notifications.ClockTickedNotification;
import de.muspellheim.activitysampling.contract.messages.notifications.PeriodEndedNotification;
import de.muspellheim.activitysampling.contract.messages.notifications.PeriodProgressedNotification;
import de.muspellheim.activitysampling.contract.messages.notifications.PeriodStartedNotification;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ClockTickedNotificationHandlerTests {
  private ClockTickedNotificationHandler periodCheck;
  private List<Object> messages;

  @BeforeEach
  void setUp() {
    periodCheck = new ClockTickedNotificationHandler();
    messages = new ArrayList<>();
    periodCheck.setOnPeriodStartedNotification(n -> messages.add(n));
    periodCheck.setOnPeriodProgressedNotification(n -> messages.add(n));
    periodCheck.setOnPeriodEndedNotification(n -> messages.add(n));
  }

  @Test
  void periodStarted() {
    periodCheck.handle(new ClockTickedNotification(LocalDateTime.of(2020, 11, 8, 17, 20)));

    assertEquals(List.of(new PeriodStartedNotification(Duration.ofMinutes(20))), messages);
  }

  @Test
  void periodProgressed() {
    periodCheck.handle(new ClockTickedNotification(LocalDateTime.of(2020, 11, 8, 17, 20)));

    periodCheck.handle(new ClockTickedNotification(LocalDateTime.of(2020, 11, 8, 17, 31, 45)));

    assertEquals(
        List.of(
            new PeriodStartedNotification(Duration.ofMinutes(20)),
            new PeriodProgressedNotification(
                Duration.ofMinutes(20),
                Duration.ofMinutes(11).plusSeconds(45),
                Duration.ofMinutes(8).plusSeconds(15))),
        messages);
  }

  @Test
  void periodEnded() {
    periodCheck.handle(new ClockTickedNotification(LocalDateTime.of(2020, 11, 8, 17, 20)));

    periodCheck.handle(new ClockTickedNotification(LocalDateTime.of(2020, 11, 8, 17, 40)));

    assertEquals(
        List.of(
            new PeriodStartedNotification(Duration.ofMinutes(20)),
            new PeriodEndedNotification(Duration.ofMinutes(20))),
        messages);
  }
}
