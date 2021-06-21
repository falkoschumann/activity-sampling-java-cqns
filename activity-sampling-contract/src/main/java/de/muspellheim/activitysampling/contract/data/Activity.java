/*
 * Activity Sampling - Contract
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.contract.data;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

public record Activity(
    String id, LocalDateTime timestamp, Duration period, String activity, List<String> tags) {
  public static final Activity NULL =
      new Activity(
          "", LocalDateTime.of(LocalDate.EPOCH, LocalTime.MIDNIGHT), Duration.ZERO, "", List.of());

  public Activity {
    Objects.requireNonNull(id, "id");
    Objects.requireNonNull(timestamp, "timestamp");
    Objects.requireNonNull(period, "period");
    Objects.requireNonNull(activity, "activity");
    Objects.requireNonNull(tags, "tags");
  }

  public Activity(String id, LocalDateTime timestamp, Duration period, String activity) {
    this(id, timestamp, period, activity, List.of());
  }
}
