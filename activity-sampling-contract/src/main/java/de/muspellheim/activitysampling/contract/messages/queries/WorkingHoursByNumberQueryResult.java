/*
 * Activity Sampling - Contract
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.contract.messages.queries;

import java.time.Duration;
import java.util.List;
import java.util.SortedSet;

public record WorkingHoursByNumberQueryResult(
    List<WorkingHoursCategory> catogories, SortedSet<String> tags) {
  public static record WorkingHoursCategory(Duration workingHours, int number) {}
}
