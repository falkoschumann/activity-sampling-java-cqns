/*
 * Activity Sampling - Contract
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.contract.messages.notification;

import java.time.LocalTime;
import java.util.Objects;

// TODO 2. Event für Period Ended anlegen?

public record PeriodProgressedNotification(LocalTime remaining, double progress) {
  public PeriodProgressedNotification {
    Objects.requireNonNull(remaining, "remaining");
  }
}
