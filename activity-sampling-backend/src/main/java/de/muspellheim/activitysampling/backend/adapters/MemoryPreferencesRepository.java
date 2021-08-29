/*
 * Activity Sampling - Backend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.backend.adapters;

import de.muspellheim.activitysampling.backend.PreferencesRepository;
import de.muspellheim.activitysampling.contract.data.Bounds;
import java.time.Duration;

public class MemoryPreferencesRepository implements PreferencesRepository {
  private Duration periodDuration = Duration.ofMinutes(20);
  private Bounds mainWindowBounds = Bounds.NULL;

  public MemoryPreferencesRepository addExamples() {
    periodDuration = Duration.ofMinutes(2);
    mainWindowBounds = new Bounds(360, 240, 640, 480);
    return this;
  }

  @Override
  public Duration loadPeriodDuration() {
    return periodDuration;
  }

  @Override
  public void savePeriodDuration(Duration periodDuration) {
    this.periodDuration = periodDuration;
  }

  @Override
  public Bounds loadMainWindowBounds() {
    return mainWindowBounds;
  }

  @Override
  public void storeMainWindowBounds(Bounds bounds) {
    this.mainWindowBounds = bounds;
  }
}
