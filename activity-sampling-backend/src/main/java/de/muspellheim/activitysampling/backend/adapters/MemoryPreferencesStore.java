/*
 * Activity Sampling - Backend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.backend.adapters;

import de.muspellheim.activitysampling.backend.PreferencesStore;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;

public class MemoryPreferencesStore implements PreferencesStore {
  private Duration periodDuration = Duration.ofMinutes(20);
  private Path activityLogFile = Paths.get("~/activity-log.csv");

  @Override
  public Duration loadPeriodDuration() {
    return periodDuration;
  }

  @Override
  public void savePeriodDuration(Duration periodDuration) {
    this.periodDuration = periodDuration;
  }

  @Override
  public Path loadActivityLogFile() {
    return activityLogFile;
  }

  @Override
  public void saveActivityLogFile(Path activityLogFile) {
    this.activityLogFile = activityLogFile;
  }
}
