/*
 * Activity Sampling - Contract
 * Copyright (c) 2020 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.contract.messages.events;

import java.time.Duration;
import java.time.LocalDateTime;
import lombok.NonNull;
import lombok.Value;

@Value
public class ActivityLoggedEvent {
  @NonNull LocalDateTime timestamp;
  @NonNull Duration period;
  @NonNull String activity;
  String tags;
}
