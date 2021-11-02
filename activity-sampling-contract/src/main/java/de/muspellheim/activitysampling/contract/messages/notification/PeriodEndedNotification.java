/*
 * Activity Sampling - Contract
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.contract.messages.notification;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public record PeriodEndedNotification(LocalDateTime timestamp, Duration period) {
  public PeriodEndedNotification {
    Objects.requireNonNull(timestamp, "timestamp");
    Objects.requireNonNull(period, "period");
  }
}
