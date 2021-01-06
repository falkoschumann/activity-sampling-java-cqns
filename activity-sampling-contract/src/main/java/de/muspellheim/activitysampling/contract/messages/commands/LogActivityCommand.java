/*
 * Activity Sampling - Contract
 * Copyright (c) 2020 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.contract.messages.commands;

import de.muspellheim.messages.Command;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import lombok.NonNull;
import lombok.Value;

@Value
public class LogActivityCommand implements Command {
  @NonNull LocalDateTime timestamp;
  @NonNull Duration period;
  @NonNull String activity;
  @NonNull List<String> tags;
}
