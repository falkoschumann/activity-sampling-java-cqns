/*
 * Activity Sampling - Backend
 * Copyright (c) 2020 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.backend;

import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;

public interface EventStore {
  String getUri();

  void setUri(String uri);

  void addRecordedObserver(Consumer<Event> handler);

  void removeRecordedObserver(Consumer<Event> handler);

  void record(Event event);

  default void record(Iterable<? extends Event> events) {
    events.forEach(this::record);
  }

  Stream<? extends Event> replay();

  @SuppressWarnings("unchecked")
  default <E extends Event> Stream<E> replay(Class<E> eventType) {
    return (Stream<E>) replay().filter(it -> it.getClass().equals(eventType));
  }

  default Stream<? extends Event> replay(Set<Class<? extends Event>> eventTypes) {
    return replay().filter(it -> eventTypes.contains(it.getClass()));
  }
}
