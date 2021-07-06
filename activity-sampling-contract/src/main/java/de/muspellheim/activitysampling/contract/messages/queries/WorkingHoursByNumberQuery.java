/*
 * Activity Sampling - Contract
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.contract.messages.queries;

import java.util.Set;

public record WorkingHoursByNumberQuery(Set<String> includedTags) {
  public WorkingHoursByNumberQuery() {
    this(Set.of());
  }
}
