/*
 * Activity Sampling - Backend
 * Copyright (c) 2020 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.backend.events;

import de.muspellheim.activitysampling.backend.Event;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Value;

@Value
@AllArgsConstructor
public class ActivityLoggedEvent implements Event {
  @NonNull String id;
  @NonNull Instant timestamp;
  @NonNull Duration period;
  @NonNull String activity;
  @NonNull List<String> tags;

  public ActivityLoggedEvent(String id, Instant timestamp, Duration period, String activity) {
    this(id, timestamp, period, activity, List.of());
  }

  public ActivityLoggedEvent(
      Instant timestamp, Duration period, String activity, List<String> tags) {
    this(UUID.randomUUID().toString(), timestamp, period, activity, tags);
  }
}
