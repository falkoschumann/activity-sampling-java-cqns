/*
 * Activity Sampling - Contract
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.contract.messages.commands;

import java.time.Duration;
import lombok.NonNull;
import lombok.Value;

@Value
public class ChangePeriodDurationCommand {
  @NonNull Duration periodDuration;
}
