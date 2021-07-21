/*
 * Activity Sampling - Backend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.backend;

import java.time.Duration;

public interface PreferencesStore {
  Duration loadPeriodDuration();

  void savePeriodDuration(Duration periodDuration);

  String loadActivityLogFile();

  void saveActivityLogFile(String activityLogFile);
}
