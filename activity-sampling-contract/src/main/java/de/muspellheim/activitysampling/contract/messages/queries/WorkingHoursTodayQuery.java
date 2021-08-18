/*
 * Activity Sampling - Contract
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.contract.messages.queries;

import java.util.Set;

public record WorkingHoursTodayQuery(@Deprecated Set<String> includedTags) {
  // TODO Ersetze Working Hours Today und This Week durch Query mit Start Date und End Date
  public WorkingHoursTodayQuery() {
    this(Set.of());
  }
}
