/*
 * Activity Sampling - Contract
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.contract.messages.queries;

import java.time.Duration;
import java.util.List;

public record WorkingHoursByNumberQueryResult(List<WorkingHoursCategory> catogories) {
  public static record WorkingHoursCategory(Duration workingHours, int number) {}
}
