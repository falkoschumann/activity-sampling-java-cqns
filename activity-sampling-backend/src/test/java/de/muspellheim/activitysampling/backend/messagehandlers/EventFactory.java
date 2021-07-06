/*
 * Activity Sampling - Backend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.backend.messagehandlers;

import de.muspellheim.activitysampling.backend.events.ActivityLoggedEvent;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

class EventFactory {
  private Instant timestamp;

  EventFactory() {
    this(LocalDateTime.of(2021, 7, 5, 9, 0).atZone(ZoneId.systemDefault()).toInstant());
  }

  EventFactory(Instant startTimestamp) {
    this.timestamp = startTimestamp;
  }

  EventFactory nextDay() {
    timestamp = timestamp.plus(1, ChronoUnit.DAYS);
    return this;
  }

  EventFactory nextWeek() {
    timestamp = timestamp.plus(7, ChronoUnit.DAYS);
    return this;
  }

  ActivityLoggedEvent create(String activity, List<String> tags) {
    timestamp = timestamp.plus(20, ChronoUnit.MINUTES);
    return new ActivityLoggedEvent(
        UUID.randomUUID().toString(), timestamp, Duration.ofMinutes(20), activity, tags);
  }
}
