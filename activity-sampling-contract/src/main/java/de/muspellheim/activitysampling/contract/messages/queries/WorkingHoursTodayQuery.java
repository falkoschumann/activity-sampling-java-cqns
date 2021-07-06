/*
 * Activity Sampling - Contract
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.contract.messages.queries;

import java.util.Set;

public record WorkingHoursTodayQuery(Set<String> includedTags) {
  public WorkingHoursTodayQuery() {
    this(Set.of());
  }
}
