/*
 * Activity Sampling - Contract
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.contract.messages.queries;

import de.muspellheim.activitysampling.contract.data.Activity;
import java.util.List;
import java.util.Objects;

public record ActivityLogQueryResult(List<Activity> log, List<Activity> recent, Activity last) {
  public ActivityLogQueryResult {
    Objects.requireNonNull(log, "log");
    Objects.requireNonNull(recent, "recent");
  }
}
