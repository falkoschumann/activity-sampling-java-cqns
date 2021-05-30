/*
 * Activity Sampling - Contract
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.contract.messages.commands;

import java.time.Duration;
import java.util.Objects;

public record ChangePeriodDurationCommand(Duration periodDuration) {
  public ChangePeriodDurationCommand {
    Objects.requireNonNull(periodDuration, "periodDuration");
  }
}
