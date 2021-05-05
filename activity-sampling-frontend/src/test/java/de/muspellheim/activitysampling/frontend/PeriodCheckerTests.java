/*
 * Activity Sampling - Frontend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.frontend;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.openMocks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.function.Consumer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

public class PeriodCheckerTests {
  @Mock private Consumer<Duration> remainingTimeChanged;
  @Mock private Consumer<LocalDateTime> periodEnded;

  private PeriodChecker checker;

  @BeforeEach
  void setUp() {
    openMocks(this);

    checker = new PeriodChecker();
    checker.setOnRemainingTimeChanged(remainingTimeChanged);
    checker.setOnPeriodEnded(periodEnded);
  }

  @Test
  void periodStarted() {
    checker.initWith(Duration.ofMinutes(20));

    var currentTime = LocalDateTime.of(2020, 11, 8, 17, 20);
    checker.clockTicked(currentTime);

    verify(remainingTimeChanged).accept(Duration.ofMinutes(20));
    verify(periodEnded, never()).accept(any());
  }

  @Test
  void periodProgressed() {
    checker.initWith(Duration.ofMinutes(20));
    var startTime = LocalDateTime.of(2020, 11, 8, 17, 20);
    checker.clockTicked(startTime);

    var currentTime = LocalDateTime.of(2020, 11, 8, 17, 31, 45);
    checker.clockTicked(currentTime);

    verify(remainingTimeChanged).accept(Duration.ofMinutes(8).plusSeconds(15));
    verify(periodEnded, never()).accept(any());
  }

  @Test
  void periodEnded() {
    checker.initWith(Duration.ofMinutes(20));
    var startTime = LocalDateTime.of(2020, 11, 8, 17, 20);
    checker.clockTicked(startTime);

    var currentTime = LocalDateTime.of(2020, 11, 8, 17, 40);
    checker.clockTicked(currentTime);

    verify(remainingTimeChanged).accept(Duration.ZERO);
    verify(periodEnded).accept(currentTime);
  }
}
