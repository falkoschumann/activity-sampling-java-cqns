/*
 * Activity Sampling - Contract
 * Copyright (c) 2020 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.contract.messages.commands;

import lombok.NonNull;
import lombok.Value;

@Value
public class NewTodoCommand {
  @NonNull String title;
}
