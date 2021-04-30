/*
 * Activity Sampling - Contract
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.contract.data;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public record Activity(
    String id, LocalDateTime timestamp, Duration period, String activity, List<String> tags) {

  public Activity(String id, LocalDateTime timestamp, Duration period, String activity) {
    this(id, timestamp, period, activity, List.of());
  }
}
