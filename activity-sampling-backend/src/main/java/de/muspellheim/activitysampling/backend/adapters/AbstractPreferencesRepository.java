/*
 * Activity Sampling - Backend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.backend.adapters;

import de.muspellheim.activitysampling.backend.PreferencesRepository;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

abstract class AbstractPreferencesRepository implements PreferencesRepository {
  private final List<Consumer<Duration>> periodChangedObserver = new CopyOnWriteArrayList<>();

  @Override
  public void addPeriodChangedObserver(Consumer<Duration> observer) {
    periodChangedObserver.add(observer);
  }

  @Override
  public void removePeriodChangedObserver(Consumer<Duration> observer) {
    periodChangedObserver.remove(observer);
  }

  protected void notifyPeriodObservers(Duration duration) {
    periodChangedObserver.forEach(it -> it.accept(duration));
  }
}
