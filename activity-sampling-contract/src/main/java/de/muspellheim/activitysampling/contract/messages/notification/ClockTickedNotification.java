/*
 * Activity Sampling - Contract
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.contract.messages.notification;

import java.time.LocalDateTime;
import java.util.Objects;

public record ClockTickedNotification(LocalDateTime timestamp) {
  public ClockTickedNotification {
    Objects.requireNonNull(timestamp, "timestamp");
  }
}
