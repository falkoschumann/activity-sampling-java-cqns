/*
 * Activity Sampling - Backend
 * Copyright (c) 2020 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.backend;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.stream.Stream;

public interface EventStore {
  void addRecordedObserver(Consumer<Event> observer);

  void removeRecordedObserver(Consumer<Event> observer);

  void record(Event event);

  default void record(Iterable<? extends Event> events) {
    for (Event event : events) {
      record(event);
    }
  }

  Stream<? extends Event> replay();

  @SuppressWarnings("unchecked")
  default <E extends Event> Stream<E> replay(Class<E> eventType) {
    return (Stream<E>) replay().filter(it -> eventType.equals(it.getClass()));
  }

  default Stream<? extends Event> replay(Collection<Class<? extends Event>> eventTypes) {
    return replay().filter(it -> eventTypes.contains(it.getClass()));
  }
}
