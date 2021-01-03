/*
 * Activity Sampling - Contract
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.contract.data;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import lombok.NonNull;
import lombok.Value;

@Value
public class Activity {
  @NonNull String id;
  @NonNull LocalDateTime timestamp;
  @NonNull Duration period;
  @NonNull String activity;
  @NonNull List<String> tags;
}
