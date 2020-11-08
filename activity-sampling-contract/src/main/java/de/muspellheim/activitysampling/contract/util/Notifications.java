/*
 * Activity Sampling - Contract
 * Copyright (c) 2020 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.contract.util;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import lombok.NonNull;

public class Notifications {

  private static final Notifications DEFAULT = new Notifications();

  private final Map<Class<?>, List<Consumer>> subscribersPerType = new ConcurrentHashMap<>();

  public static Notifications getDefault() {
    return DEFAULT;
  }

  public <T> void subscribe(
      @NonNull Class<? extends T> notificationType, @NonNull Consumer<T> subscriber) {
    subscribersPerType.merge(
        notificationType,
        new CopyOnWriteArrayList(List.of(subscriber)),
        (a, b) -> {
          a.addAll(b);
          return a;
        });
  }

  public void unsubscribe(@NonNull Consumer<?> subscriber) {
    subscribersPerType.values().forEach(subscribers -> subscribers.remove(subscriber));
  }

  public <T> void unsubscribe(
      @NonNull Class<? extends T> notificationType, @NonNull Consumer<T> subscriber) {
    subscribersPerType.keySet().stream()
        .filter(type -> notificationType.isAssignableFrom(type))
        .map(type -> subscribersPerType.get(type))
        .forEach(subscribers -> subscribers.remove(subscriber));
  }

  public void publish(@NonNull Object notification) {
    Class<?> notificationType = notification.getClass();
    subscribersPerType.keySet().stream()
        .filter(type -> type.isAssignableFrom(notificationType))
        .flatMap(type -> subscribersPerType.get(type).stream())
        .forEach(subscriber -> subscriber.accept(notification));
  }
}
