/*
 * Activity Sampling - Contract
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.contract.messages.queries;

import java.util.Set;

public record WorkingHoursByActivityQuery(Set<String> includedTags) {
  public WorkingHoursByActivityQuery() {
    this(Set.of());
  }
}
