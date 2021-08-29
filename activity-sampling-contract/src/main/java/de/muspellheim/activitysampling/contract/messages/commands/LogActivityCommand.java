/*
 * Activity Sampling - Contract
 * Copyright (c) 2020 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.contract.messages.commands;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public record LogActivityCommand(
    LocalDateTime timestamp,
    Duration period,
    String client,
    String project,
    String task,
    String notes) {
  public LogActivityCommand {
    Objects.requireNonNull(timestamp, "timestamp");
    Objects.requireNonNull(period, "period");
    Objects.requireNonNull(client, "client");
    Objects.requireNonNull(project, "project");
    Objects.requireNonNull(task, "task");
    Objects.requireNonNull(notes, "notes");
  }
}
