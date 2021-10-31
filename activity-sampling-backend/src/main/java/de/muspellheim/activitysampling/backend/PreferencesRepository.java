/*
 * Activity Sampling - Backend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.backend;

import de.muspellheim.activitysampling.contract.data.Bounds;
import java.time.Duration;
import java.util.function.Consumer;

public interface PreferencesRepository {
  Duration getPeriod();

  void setPeriod(Duration period);

  void addPeriodChangedObserver(Consumer<Duration> observer);

  void removePeriodChangedObserver(Consumer<Duration> observer);

  Bounds getMainWindowBounds();

  void setMainWindowBounds(Bounds bounds);
}
