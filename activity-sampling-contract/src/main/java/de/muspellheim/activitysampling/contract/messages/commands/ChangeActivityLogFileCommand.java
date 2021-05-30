/*
 * Activity Sampling - Contract
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.contract.messages.commands;

import java.nio.file.Path;
import java.util.Objects;

public record ChangeActivityLogFileCommand(Path activityLogFile) {
  public ChangeActivityLogFileCommand {
    Objects.requireNonNull(activityLogFile, "activityLogFile");
  }
}
