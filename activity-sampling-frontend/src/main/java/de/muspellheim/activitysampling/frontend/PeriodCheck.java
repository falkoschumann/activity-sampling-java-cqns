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

class PeriodCheck {
  @Getter private Duration period;
  @Getter @Setter private Consumer<Duration> onPeriodStarted;
  @Getter @Setter private Consumer<Duration> onPeriodProgressed;
  @Getter @Setter private Consumer<LocalDateTime> onPeriodEnded;

  private LocalDateTime start;

  PeriodCheck() {
    this(Duration.ofMinutes(20));
  }

  PeriodCheck(Duration period) {
    setPeriod(period);
  }

  public void setPeriod(Duration period) {
    this.period = period;
    start = null;
  }

  void check(LocalDateTime timestamp) {
    if (start == null) {
      start = timestamp;
      onPeriodStarted.accept(period);
      return;
    }

    var elapsedTime = Duration.between(start, timestamp);
    var remainingTime = period.minus(elapsedTime);
    if (remainingTime.toSeconds() <= 0) {
      onPeriodEnded.accept(timestamp);
      start = null;
    } else {
      onPeriodProgressed.accept(elapsedTime);
    }
  }
}
