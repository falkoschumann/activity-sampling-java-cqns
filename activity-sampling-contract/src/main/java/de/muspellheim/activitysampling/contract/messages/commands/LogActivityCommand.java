/*
 * Activity Sampling - Contract
 * Copyright (c) 2020 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.contract.messages.commands;

import de.muspellheim.messages.Command;
import lombok.NonNull;
import lombok.Value;

@Value
public class LogActivityCommand implements Command {
  @NonNull String activity;
  String tags;
}
