/*
 * Activity Sampling - Contract
 * Copyright (c) 2020 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.contract.messages.commands;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public record LogActivityCommand(
    LocalDateTime timestamp,
    Duration period,
    String client,
    String project,
    String task,
    String notes,
    @Deprecated List<String> tags) {
  public LogActivityCommand {
    Objects.requireNonNull(timestamp, "timestamp");
    Objects.requireNonNull(period, "period");
    Objects.requireNonNull(notes, "notes");
    Objects.requireNonNull(tags, "tags");
  }

  public LogActivityCommand(
      LocalDateTime timestamp,
      Duration period,
      String client,
      String project,
      String task,
      String notes) {
    this(timestamp, period, client, project, task, notes, List.of());
  }

  @Deprecated
  public LogActivityCommand(
      LocalDateTime timestamp, Duration period, String activity, List<String> tags) {
    this(timestamp, period, null, null, null, activity, tags);
  }
}
