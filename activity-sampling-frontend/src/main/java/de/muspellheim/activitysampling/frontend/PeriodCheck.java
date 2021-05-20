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

public class PeriodCheck {
  @Getter @Setter Consumer<Duration> onRemainingTimeChanged;
  @Getter @Setter Consumer<LocalDateTime> onPeriodEnded;

  private Duration period = Duration.ofMinutes(20);
  private LocalDateTime start;

  public Duration getPeriod() {
    return period;
  }

  public void setPeriod(Duration period) {
    this.period = period;
    start = null;
  }

  public void check(LocalDateTime timestamp) {
    if (start == null) {
      start = timestamp;
      onRemainingTimeChanged.accept(period);
      return;
    }

    var elapsed = Duration.between(start, timestamp);
    var remaining = period.minus(elapsed);
    if (remaining.toSeconds() <= 0) {
      onRemainingTimeChanged.accept(Duration.ZERO);
      onPeriodEnded.accept(timestamp);
      start = null;
    } else {
      onRemainingTimeChanged.accept(remaining);
    }
  }
}
