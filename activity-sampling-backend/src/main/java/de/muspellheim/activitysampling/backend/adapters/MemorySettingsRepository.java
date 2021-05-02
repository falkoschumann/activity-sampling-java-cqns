/*
 * Activity Sampling - Backend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.backend.adapters;

import de.muspellheim.activitysampling.backend.SettingsRepository;
import java.nio.file.Path;
import java.time.Duration;

public class MemorySettingsRepository implements SettingsRepository {
  private Duration periodDuration = DEFAULT_PERIOD_DURATION;
  private Path activityLogFile = DEFAULT_ACTIVITY_LOG_FILE;

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
