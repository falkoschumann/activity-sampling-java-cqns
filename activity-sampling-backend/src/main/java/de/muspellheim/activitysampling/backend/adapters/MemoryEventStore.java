/*
 * Activity Sampling - Backend
 * Copyright (c) 2020 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.backend.adapters;

import de.muspellheim.activitysampling.backend.Event;
import de.muspellheim.activitysampling.backend.EventStore;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.Setter;

public class MemoryEventStore implements EventStore {
  @Getter @Setter Consumer<Event> onRecorded;

  private final List<Event> events = new ArrayList<>();

  @Override
  public void record(Event event) {
    events.add(event);
    publishRecorded(event);
  }

  private void publishRecorded(Event event) {
    if (onRecorded == null) {
      return;
    }

    onRecorded.accept(event);
  }

  @Override
  public Stream<Event> replay() {
    return events.stream();
  }
}
