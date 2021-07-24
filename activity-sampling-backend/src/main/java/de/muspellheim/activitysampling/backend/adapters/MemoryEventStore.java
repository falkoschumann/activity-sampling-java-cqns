/*
 * Activity Sampling - Backend
 * Copyright (c) 2020 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.backend.adapters;

import de.muspellheim.activitysampling.backend.Event;
import de.muspellheim.activitysampling.backend.events.ActivityLoggedEvent;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class MemoryEventStore extends AbstractEventStore {
  private final List<Event> events = new ArrayList<>();

  public MemoryEventStore addExamples() {
    events.addAll(
        List.of(
            new ActivityLoggedEvent(
                "a7caf1b0-886e-406f-8fbc-71da9f34714e",
                LocalDateTime.of(2020, 12, 30, 17, 52).atZone(ZoneId.systemDefault()).toInstant(),
                Duration.ofMinutes(20),
                "Taste JavaScript"),
            new ActivityLoggedEvent(
                "d5abc0dd-60b0-4a3b-9b2f-8b02005fb256",
                LocalDateTime.of(2020, 12, 30, 21, 20).atZone(ZoneId.systemDefault()).toInstant(),
                Duration.ofMinutes(20),
                "Create a TodoMVC template",
                List.of("Test", "TodoMVC"))));
    return this;
  }

  @Override
  public void record(Event event) {
    events.add(event);
    notifyRecordedObservers(event);
  }

  @Override
  public Stream<Event> replay() {
    return events.stream();
  }
}
