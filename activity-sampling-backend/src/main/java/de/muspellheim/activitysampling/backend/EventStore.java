/*
 * Activity Sampling - Backend
 * Copyright (c) 2020 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.backend;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public interface EventStore {
  Consumer<Event> getOnRecorded();

  void setOnRecorded(Consumer<Event> consumer);

  void record(Event event);

  default void record(List<Event> events) {
    events.forEach(this::record);
  }

  List<Event> replay();

  default List<Event> replay(Class<? extends Event> eventType) {
    return replay().stream()
        .filter(it -> it.getClass().equals(eventType))
        .collect(Collectors.toList());
  }

  default List<Event> replay(List<Class<? extends Event>> eventTypes) {
    return replay().stream()
        .filter(it -> eventTypes.contains(it.getClass()))
        .collect(Collectors.toList());
  }
}
