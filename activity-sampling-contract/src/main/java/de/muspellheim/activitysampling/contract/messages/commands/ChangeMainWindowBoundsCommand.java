/*
 * Activity Sampling - Contract
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.contract.messages.commands;

import de.muspellheim.activitysampling.contract.data.Bounds;
import java.util.Objects;

public record ChangeMainWindowBoundsCommand(Bounds bounds) {
  public ChangeMainWindowBoundsCommand {
    Objects.requireNonNull(bounds, "bounds");
  }
}
