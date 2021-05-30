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
    LocalDateTime timestamp, Duration period, String activity, List<String> tags) {
  public LogActivityCommand {
    Objects.requireNonNull(timestamp, "timestamp");
    Objects.requireNonNull(period, "period");
    Objects.requireNonNull(activity, "activity");
    Objects.requireNonNull(tags, "tags");
  }
}
