/*
 * Activity Sampling - Backend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.backend.adapters;

import de.muspellheim.activitysampling.backend.PreferencesStore;
import java.time.Duration;

public class MemoryPreferencesStore implements PreferencesStore {
  private Duration periodDuration;

  public MemoryPreferencesStore addExamples() {
    periodDuration = Duration.ofMinutes(2);
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
}
