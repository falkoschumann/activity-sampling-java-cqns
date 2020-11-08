package de.muspellheim.activitysampling.backend;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import de.muspellheim.activitysampling.contract.messages.notifications.ClockTickedNotification;
import de.muspellheim.activitysampling.contract.messages.notifications.PeriodEndedNotification;
import de.muspellheim.activitysampling.contract.messages.notifications.PeriodProgressedNotification;
import de.muspellheim.activitysampling.contract.messages.notifications.PeriodStartedNotification;
import java.time.Duration;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PeriodCheckTests {
  private PeriodCheck periodCheck;
  private PeriodStartedNotification periodStartedNotification;
  private PeriodProgressedNotification periodProgressedNotification;
  private PeriodEndedNotification periodEndedNotification;

  @BeforeEach
  void setUp() {
    periodCheck = new PeriodCheck();
    periodCheck.setOnPeriodStartedNotification(n -> periodStartedNotification = n);
    periodCheck.setOnPeriodProgressedNotification(n -> periodProgressedNotification = n);
    periodCheck.setOnPeriodEndedNotification(n -> periodEndedNotification = n);
  }

  @Test
  void periodStarted() {
    periodCheck.handle(new ClockTickedNotification(LocalDateTime.of(2020, 11, 8, 17, 20)));

    assertEquals(new PeriodStartedNotification(Duration.ofMinutes(20)), periodStartedNotification);
    assertNull(periodProgressedNotification);
    assertNull(periodEndedNotification);
  }

  @Test
  void periodProgressed() {
    periodCheck.handle(new ClockTickedNotification(LocalDateTime.of(2020, 11, 8, 17, 20)));

    periodCheck.handle(new ClockTickedNotification(LocalDateTime.of(2020, 11, 8, 17, 31, 45)));

    assertEquals(new PeriodStartedNotification(Duration.ofMinutes(20)), periodStartedNotification);
    assertEquals(
        new PeriodProgressedNotification(Duration.ofMinutes(8).plusSeconds(15)),
        periodProgressedNotification);
    assertNull(periodEndedNotification);
  }

  @Test
  void periodEnded() {}
}
