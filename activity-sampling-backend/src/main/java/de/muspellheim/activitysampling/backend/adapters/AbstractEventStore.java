/*
 * Activity Sampling - Backend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.backend.adapters;

import de.muspellheim.activitysampling.backend.Event;
import de.muspellheim.activitysampling.backend.EventStore;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import lombok.Getter;
import lombok.Setter;

public abstract class AbstractEventStore implements EventStore {
  @Getter @Setter private String uri;

  private final List<Consumer<Event>> recordedObserver = new CopyOnWriteArrayList<>();

  @Override
  public void addRecordedObserver(Consumer<Event> observer) {
    recordedObserver.add(observer);
  }

  @Override
  public void removeRecordedObserver(Consumer<Event> observer) {
    recordedObserver.remove(observer);
  }

  protected void notifyRecordedObservers(Event event) {
    recordedObserver.forEach(it -> it.accept(event));
  }
}
