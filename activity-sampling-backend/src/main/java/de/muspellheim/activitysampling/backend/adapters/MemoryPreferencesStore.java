/*
 * Activity Sampling - Backend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.backend.adapters;

import de.muspellheim.activitysampling.backend.PreferencesStore;
import java.nio.file.Paths;
import java.time.Duration;

public class MemoryPreferencesStore implements PreferencesStore {
  private Duration periodDuration =
      Boolean.parseBoolean(System.getProperty("demoMode"))
          ? Duration.ofMinutes(1)
          : Duration.ofMinutes(20);
  private String activityLogFile =
      Paths.get(System.getProperty("user.home"), "activity-log.csv").toString();

  @Override
  public Duration loadPeriodDuration() {
    return periodDuration;
  }

  @Override
  public void savePeriodDuration(Duration periodDuration) {
    this.periodDuration = periodDuration;
  }

  @Override
  public String loadActivityLogFile() {
    return activityLogFile;
  }

  @Override
  public void saveActivityLogFile(String activityLogFile) {
    this.activityLogFile = activityLogFile;
  }
}
