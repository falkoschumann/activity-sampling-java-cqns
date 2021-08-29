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
                LocalDateTime.of(2021, 8, 18, 19, 18).atZone(ZoneId.systemDefault()).toInstant(),
                Duration.ofMinutes(20),
                "Wayne Enterprises",
                "Batmobil",
                "Design",
                "Create black car"),
            new ActivityLoggedEvent(
                "d5abc0dd-60b0-4a3b-9b2f-8b02005fb256",
                LocalDateTime.of(2021, 8, 18, 19, 38).atZone(ZoneId.systemDefault()).toInstant(),
                Duration.ofMinutes(20),
                "Axis Chemical Co.",
                "Gotham",
                "Production",
                "Double the production of Smylex"),
            new ActivityLoggedEvent(
                "e9ed7915-8109-402d-b9e6-2d5764ef688d",
                LocalDateTime.of(2021, 8, 18, 19, 58).atZone(ZoneId.systemDefault()).toInstant(),
                Duration.ofMinutes(20),
                "Muspellheim",
                "Activity Sampling",
                "Analyze",
                "Write user stories")));
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
