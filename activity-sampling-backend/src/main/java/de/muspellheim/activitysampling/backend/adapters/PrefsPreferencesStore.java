/*
 * Activity Sampling - Backend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.backend.adapters;

import de.muspellheim.activitysampling.backend.PreferencesStore;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.prefs.Preferences;

public class PrefsPreferencesStore implements PreferencesStore {
  private static final String APP_NODE = "/de/muspellheim/activitysampling";
  private static final String KEY_PERIOD_DURATION = "periodDuration";
  private static final String KEY_ACTIVITY_LOG_FILE = "activityLogFile";

  private final Preferences preferences = Preferences.userRoot().node(APP_NODE);

  @Override
  public Duration loadPeriodDuration() {
    var value = preferences.get(KEY_PERIOD_DURATION, "PT20M");
    return Duration.parse(value);
  }

  @Override
  public void savePeriodDuration(Duration periodDuration) {
    preferences.put(KEY_PERIOD_DURATION, periodDuration.toString());
  }

  @Override
  public Path loadActivityLogFile() {
    var userHome = System.getProperty("user.home");
    var file = Paths.get(userHome, "activity-log.csv");
    var value = preferences.get(KEY_ACTIVITY_LOG_FILE, file.toString());
    return Paths.get(value);
  }

  @Override
  public void saveActivityLogFile(Path activityLogFile) {
    preferences.put(KEY_ACTIVITY_LOG_FILE, activityLogFile.toString());
  }
}
