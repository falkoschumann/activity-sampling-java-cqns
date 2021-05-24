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

public class PeriodCheckTests {
  @Mock private Consumer<Duration> remainingTimeChanged;
  @Mock private Consumer<LocalDateTime> periodEnded;

  private PeriodCheck periodCheck;

  @BeforeEach
  void setUp() {
    openMocks(this);

    periodCheck = new PeriodCheck();
    periodCheck.setOnRemainingTimeChanged(remainingTimeChanged);
    periodCheck.setOnPeriodEnded(periodEnded);
  }

  @Test
  void periodStarted() {
    periodCheck.setPeriod(Duration.ofMinutes(20));

    var currentTime = LocalDateTime.of(2020, 11, 8, 17, 20);
    periodCheck.check(currentTime);

    verify(remainingTimeChanged).accept(Duration.ofMinutes(20));
    verify(periodEnded, never()).accept(any());
  }

  @Test
  void periodProgressed() {
    periodCheck.setPeriod(Duration.ofMinutes(20));
    var startTime = LocalDateTime.of(2020, 11, 8, 17, 20);
    periodCheck.check(startTime);

    var currentTime = LocalDateTime.of(2020, 11, 8, 17, 31, 45);
    periodCheck.check(currentTime);

    verify(remainingTimeChanged).accept(Duration.ofMinutes(8).plusSeconds(15));
    verify(periodEnded, never()).accept(any());
  }

  @Test
  void periodEnded() {
    periodCheck.setPeriod(Duration.ofMinutes(20));
    var startTime = LocalDateTime.of(2020, 11, 8, 17, 20);
    periodCheck.check(startTime);

    var currentTime = LocalDateTime.of(2020, 11, 8, 17, 40);
    periodCheck.check(currentTime);

    verify(remainingTimeChanged).accept(Duration.ZERO);
    verify(periodEnded).accept(currentTime);
  }
}
