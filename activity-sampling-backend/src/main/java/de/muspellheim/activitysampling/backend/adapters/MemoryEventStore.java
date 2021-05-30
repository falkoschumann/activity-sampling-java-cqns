/*
 * Activity Sampling - Backend
 * Copyright (c) 2020 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.backend.adapters;

import de.muspellheim.activitysampling.backend.Event;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class MemoryEventStore extends AbstractEventStore {
  private final List<Event> events = new ArrayList<>();

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
