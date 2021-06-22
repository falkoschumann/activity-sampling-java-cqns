/*
 * Activity Sampling - Frontend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.frontend;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.Duration;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ActivitySamplingModelTests {
  private ActivitySamplingModel fixture;

  @BeforeEach
  void init() {
    fixture = new ActivitySamplingModel();
  }

  @Test
  void testClockTicked_periodStarted() {
    fixture.setPeriodDuration(Duration.ofMinutes(20));
    fixture.resetPeriod();

    var currentTime = LocalDateTime.of(2020, 11, 8, 17, 20);
    fixture.progressPeriod(currentTime);

    assertAll(
        () -> assertEquals(Duration.ofMinutes(20), fixture.getRemainingTime(), "remainingTime"),
        () -> assertNull(fixture.getPeriodEnd(), "periodEnd"),
        () -> assertEquals(0.0, fixture.getPeriodProgress(), "periodProgress"));
  }

  @Test
  void testClockTicked_periodProgressed() {
    fixture.setPeriodDuration(Duration.ofMinutes(20));
    fixture.resetPeriod();
    var startTime = LocalDateTime.of(2020, 11, 8, 17, 20);
    fixture.progressPeriod(startTime);

    var currentTime = LocalDateTime.of(2020, 11, 8, 17, 31, 45);
    fixture.progressPeriod(currentTime);

    assertAll(
        () ->
            assertEquals(
                Duration.ofMinutes(8).plusSeconds(15), fixture.getRemainingTime(), "remainingTime"),
        () -> assertNull(fixture.getPeriodEnd(), "periodEnd"),
        () -> assertEquals(0.5875, fixture.getPeriodProgress(), "periodProgress"));
  }

  @Test
  void testClockTicked_periodEnded() {
    fixture.setPeriodDuration(Duration.ofMinutes(20));
    fixture.resetPeriod();
    var startTime = LocalDateTime.of(2020, 11, 8, 17, 20);
    fixture.progressPeriod(startTime);

    var currentTime = LocalDateTime.of(2020, 11, 8, 17, 40);
    fixture.progressPeriod(currentTime);

    assertAll(
        () -> assertEquals(Duration.ZERO, fixture.getRemainingTime(), "remainingTime"),
        () -> assertEquals(currentTime, fixture.getPeriodEnd(), "periodEnd"),
        () -> assertEquals(1.0, fixture.getPeriodProgress(), "periodProgress"));
  }
}
