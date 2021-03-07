/*
 * Activity Sampling - Backend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.backend;

import java.nio.file.Path;
import java.time.Duration;

public interface PreferencesRepository {
  Duration loadPeriodDuration();

  void savePeriodDuration(Duration periodDuration);

  Path loadActivityLogFile();

  void saveActivityLogFile(Path activityLogFile);
}
