/*
 * Activity Sampling - Contract
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.contract.data;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public record Activity(
    String id,
    LocalDateTime timestamp,
    Duration period,
    String client,
    String project,
    String task,
    String notes,
    @Deprecated List<String> tags) {
  public Activity {
    Objects.requireNonNull(id, "id");
    Objects.requireNonNull(timestamp, "timestamp");
    Objects.requireNonNull(period, "period");
    Objects.requireNonNull(notes, "notes");
  }

  public Activity(
      String id,
      LocalDateTime timestamp,
      Duration period,
      String client,
      String project,
      String task,
      String notes) {
    this(id, timestamp, period, client, project, task, notes, List.of());
  }

  @Deprecated
  public Activity(String id, LocalDateTime timestamp, Duration period, String activity) {
    this(id, timestamp, period, null, null, null, activity, List.of());
  }

  @Deprecated
  public Activity(
      String id, LocalDateTime timestamp, Duration period, String activity, List<String> tags) {
    this(id, timestamp, period, null, null, null, activity, tags);
  }

  @Deprecated
  public String activity() {
    return notes;
  }
}
