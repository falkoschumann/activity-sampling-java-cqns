/*
 * Activity Sampling - Contract
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.contract.messages.commands;

import java.time.LocalDateTime;
import java.util.Objects;

public record ProgressPeriodCommand(LocalDateTime currentTime) {
  public ProgressPeriodCommand {
    Objects.requireNonNull(currentTime, "currentTime");
  }
}
