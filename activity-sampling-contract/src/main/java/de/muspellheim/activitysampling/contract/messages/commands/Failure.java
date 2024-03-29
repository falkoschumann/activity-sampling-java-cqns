/*
 * Activity Sampling - Contract
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.contract.messages.commands;

import java.util.Objects;

public record Failure(String errorMessage) implements CommandStatus {
  public Failure {
    Objects.requireNonNull(errorMessage, "errorMessage");
  }
}
