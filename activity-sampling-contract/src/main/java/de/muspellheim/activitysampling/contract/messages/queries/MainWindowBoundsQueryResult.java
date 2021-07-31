/*
 * Activity Sampling - Contract
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.contract.messages.queries;

import de.muspellheim.activitysampling.contract.data.Bounds;
import java.util.Objects;

public record MainWindowBoundsQueryResult(Bounds bounds) {
  public MainWindowBoundsQueryResult {
    Objects.requireNonNull(bounds, "bounds");
  }
}
