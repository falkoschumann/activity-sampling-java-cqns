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

  void record(Event event) throws Exception;

  default void record(List<Event> events) throws Exception {
    for (Event event : events) {
      record(event);
    }
  }

  // TODO Replay List in Replay Stream ändern
  List<Event> replay() throws Exception;

  default List<Event> replay(Class<? extends Event> eventType) throws Exception {
    return replay().stream()
        .filter(it -> it.getClass().equals(eventType))
        .collect(Collectors.toList());
  }

  default List<Event> replay(List<Class<? extends Event>> eventTypes) throws Exception {
    return replay().stream()
        .filter(it -> eventTypes.contains(it.getClass()))
        .collect(Collectors.toList());
  }
}
