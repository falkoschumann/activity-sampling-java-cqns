/*
 * Activity Sampling - Frontend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.frontend;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.function.Consumer;
import lombok.Getter;
import lombok.Setter;

public class PeriodChecker {
  @Getter @Setter Consumer<Duration> onRemainingTimeChanged;
  @Getter @Setter Consumer<LocalDateTime> onPeriodEnded;

  private Duration period;
  private LocalDateTime startTime;
  private LocalDateTime endTime;

  public void initWith(Duration period) {
    this.period = period;
    startTime = null;
  }

  public void clockTicked(LocalDateTime timestamp) {
    if (startTime == null) {
      startTime = timestamp;
      onRemainingTimeChanged.accept(period);
      return;
    }

    var elapsedTime = Duration.between(startTime, timestamp);
    var remainingTime = period.minus(elapsedTime);
    if (remainingTime.toSeconds() <= 0) {
      endTime = timestamp;
      onRemainingTimeChanged.accept(Duration.ZERO);
      onPeriodEnded.accept(timestamp);
      startTime = null;
    } else {
      onRemainingTimeChanged.accept(remainingTime);
    }
  }
}
