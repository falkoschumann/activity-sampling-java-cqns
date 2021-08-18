/*
 * Activity Sampling - Backend
 * Copyright (c) 2020 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.backend.events;

import de.muspellheim.activitysampling.backend.Event;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public record ActivityLoggedEvent(
    String id,
    Instant timestamp,
    Duration period,
    String client,
    String project,
    String task,
    String notes,
    @Deprecated List<String> tags)
    implements Event {
  public ActivityLoggedEvent {
    Objects.requireNonNull(id, "id");
    Objects.requireNonNull(timestamp, "timestamp");
    Objects.requireNonNull(period, "period");
    Objects.requireNonNull(notes, "notes");
    Objects.requireNonNull(tags, "tags");
  }

  public ActivityLoggedEvent(
      String id,
      Instant timestamp,
      Duration period,
      String client,
      String project,
      String task,
      String notes) {
    this(id, timestamp, period, client, project, task, notes, List.of());
  }

  @Deprecated
  public ActivityLoggedEvent(
      String id, Instant timestamp, Duration period, String activity, List<String> tags) {
    this(id, timestamp, period, "", "", "", activity, tags);
  }

  @Deprecated
  public ActivityLoggedEvent(String id, Instant timestamp, Duration period, String activity) {
    this(id, timestamp, period, activity, List.of());
  }

  @Deprecated
  public ActivityLoggedEvent(
      Instant timestamp, Duration period, String activity, List<String> tags) {
    this(UUID.randomUUID().toString(), timestamp, period, activity, tags);
  }

  @Deprecated
  public String activity() {
    return notes();
  }
}
