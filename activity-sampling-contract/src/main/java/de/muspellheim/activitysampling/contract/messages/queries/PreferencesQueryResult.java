/*
 * Activity Sampling - Contract
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.contract.messages.queries;

import java.time.Duration;
import java.util.Objects;

public record PreferencesQueryResult(Duration periodDuration, String activityLogFile) {
  public PreferencesQueryResult {
    Objects.requireNonNull(periodDuration, "periodDuration");
    Objects.requireNonNull(activityLogFile, "activityLogFile");
  }
}
