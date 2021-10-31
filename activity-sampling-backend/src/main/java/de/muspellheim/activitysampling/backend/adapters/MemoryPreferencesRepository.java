/*
 * Activity Sampling - Backend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.backend.adapters;

import de.muspellheim.activitysampling.contract.data.Bounds;
import java.time.Duration;

public class MemoryPreferencesRepository extends AbstractPreferencesRepository {
  private Duration period = Duration.ofMinutes(20);
  private Bounds mainWindowBounds = Bounds.NULL;

  public MemoryPreferencesRepository addExamples() {
    period = Duration.ofMinutes(2);
    mainWindowBounds = new Bounds(360, 240, 640, 480);
    return this;
  }

  @Override
  public Duration getPeriod() {
    return period;
  }

  @Override
  public void setPeriod(Duration period) {
    this.period = period;
    notifyPeriodObservers(period);
  }

  @Override
  public Bounds getMainWindowBounds() {
    return mainWindowBounds;
  }

  @Override
  public void setMainWindowBounds(Bounds bounds) {
    this.mainWindowBounds = bounds;
  }
}
