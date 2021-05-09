/*
 * Activity Sampling - Backend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.backend;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;

public interface SettingsRepository {
  Duration DEFAULT_PERIOD_DURATION = Duration.ofMinutes(20);
  Path DEFAULT_ACTIVITY_LOG_FILE = Paths.get(System.getProperty("user.home"), "activity-log.csv");

  Duration loadPeriodDuration();

  void savePeriodDuration(Duration periodDuration);

  Path loadActivityLogFile();

  void saveActivityLogFile(Path activityLogFile);
}
