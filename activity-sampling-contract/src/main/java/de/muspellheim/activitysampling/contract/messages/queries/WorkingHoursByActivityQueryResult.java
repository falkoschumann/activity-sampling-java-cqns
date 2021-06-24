/*
 * Activity Sampling - Contract
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.contract.messages.queries;

import java.time.Duration;
import java.util.List;

public record WorkingHoursByActivityQueryResult(List<WorkingHours> workingHours) {
  public static record WorkingHours(String activity, Duration workingHours) {}
}
