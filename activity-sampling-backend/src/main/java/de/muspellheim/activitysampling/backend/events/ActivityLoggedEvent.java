/*
 * Activity Sampling - Backend
 * Copyright (c) 2020 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.backend.events;

import de.muspellheim.activitysampling.backend.Event;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

public record ActivityLoggedEvent(
    Instant timestamp, Duration period, String client, String project, String task, String notes)
    implements Event {
  public ActivityLoggedEvent {
    Objects.requireNonNull(timestamp, "timestamp");
    Objects.requireNonNull(period, "period");
    Objects.requireNonNull(client, "client");
    Objects.requireNonNull(project, "project");
    Objects.requireNonNull(task, "task");
    Objects.requireNonNull(notes, "notes");
  }
}
