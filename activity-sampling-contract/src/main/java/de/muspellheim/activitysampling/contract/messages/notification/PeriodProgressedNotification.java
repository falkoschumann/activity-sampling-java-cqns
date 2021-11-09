/*
 * Activity Sampling - Contract
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.contract.messages.notification;

import java.time.LocalTime;
import java.util.Objects;

public record PeriodProgressedNotification(LocalTime remaining, double progress) {
  public PeriodProgressedNotification {
    Objects.requireNonNull(remaining, "remaining");
  }
}
