/*
 * Activity Sampling - Backend
 * Copyright (c) 2020 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.backend;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import lombok.Getter;
import lombok.Setter;

public class MemoryEventStore implements EventStore {
  @Getter @Setter Consumer<Event> onRecorded;

  private final List<Event> events = new ArrayList<>();

  @Override
  public void record(Event event) {
    events.add(event);
  }

  @Override
  public List<Event> replay() {
    return events;
  }
}
