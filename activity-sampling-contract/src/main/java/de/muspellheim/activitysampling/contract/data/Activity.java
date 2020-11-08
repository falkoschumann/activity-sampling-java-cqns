/*
 * Activity Sampling - Contract
 * Copyright (c) 2020 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.contract.data;

import java.time.Duration;
import java.time.LocalDate;
import lombok.NonNull;
import lombok.Value;

@Value
public class Activity {
  @NonNull LocalDate timestamp;
  @NonNull Duration period;
  @NonNull String activity;
  String tags;
}
