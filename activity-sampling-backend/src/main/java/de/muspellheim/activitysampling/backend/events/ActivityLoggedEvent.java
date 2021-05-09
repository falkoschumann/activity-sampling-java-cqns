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
import lombok.NonNull;

public record ActivityLoggedEvent(
    @NonNull String id,
    @NonNull Instant timestamp,
    @NonNull Duration period,
    @NonNull String activity,
    @NonNull List<String> tags)
    implements Event {

  public ActivityLoggedEvent(
      Instant timestamp, Duration period, String activity, List<String> tags) {
    this(UUID.randomUUID().toString(), timestamp, period, activity, tags);
  }
}
