/*
 * Activity Sampling - Frontend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.frontend;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ActivitySamplingViewModelTests {
  private PeriodCheck periodCheck;
  private List<Object> messages;

  @BeforeEach
  void setUp() {
    periodCheck = new PeriodCheck();
    messages = new ArrayList<>();
    periodCheck.setOnPeriodStarted(n -> messages.add(n));
    periodCheck.setOnPeriodProgressed(n -> messages.add(n));
    periodCheck.setOnPeriodEnded(n -> messages.add(n));
  }

  @Test
  void periodStarted() {
    periodCheck.check(LocalDateTime.of(2020, 11, 8, 17, 20));

    assertEquals(List.of(Duration.ofMinutes(20)), messages);
  }

  @Test
  void periodProgressed() {
    periodCheck.check(LocalDateTime.of(2020, 11, 8, 17, 20));

    periodCheck.check(LocalDateTime.of(2020, 11, 8, 17, 31, 45));

    assertEquals(List.of(Duration.ofMinutes(20), Duration.ofMinutes(11).plusSeconds(45)), messages);
  }

  @Test
  void periodEnded() {
    periodCheck.check(LocalDateTime.of(2020, 11, 8, 17, 20));

    periodCheck.check(LocalDateTime.of(2020, 11, 8, 17, 40));

    assertEquals(List.of(Duration.ofMinutes(20), LocalDateTime.of(2020, 11, 8, 17, 40)), messages);
  }
}
