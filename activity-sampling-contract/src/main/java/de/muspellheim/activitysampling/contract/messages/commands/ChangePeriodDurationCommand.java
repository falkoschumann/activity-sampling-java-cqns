/*
 * Activity Sampling - Contract
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.contract.messages.commands;

import de.muspellheim.messages.Command;
import java.time.Duration;
import lombok.NonNull;
import lombok.Value;

@Value
public class ChangePeriodDurationCommand implements Command {
  @NonNull Duration periodDuration;
}
