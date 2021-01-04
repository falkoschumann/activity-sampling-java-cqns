/*
 * Activity Sampling - Backend
 * Copyright (c) 2020 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.backend;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

public interface EventStore {
  Consumer<Event> getOnRecorded();

  void setOnRecorded(Consumer<Event> consumer);

  void record(Event event) throws Exception;

  default void record(Iterable<? extends Event> events) throws Exception {
    for (Event event : events) {
      record(event);
    }
  }

  Stream<? extends Event> replay() throws Exception;

  @SuppressWarnings("unchecked")
  default <E extends Event> Stream<E> replay(Class<E> eventType) throws Exception {
    return (Stream<E>) replay().filter(it -> it.getClass().equals(eventType));
  }

  default Stream<? extends Event> replay(List<Class<? extends Event>> eventTypes) throws Exception {
    return replay().filter(it -> eventTypes.contains(it.getClass()));
  }
}
