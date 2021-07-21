/*
 * Activity Sampling - Backend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.backend.adapters;

import de.muspellheim.activitysampling.backend.PreferencesStore;
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
  public String loadActivityLogFile() {
    var file = Paths.get(System.getProperty("user.home"), "activity-log.csv");
    return preferences.get(KEY_ACTIVITY_LOG_FILE, file.toString());
  }

  @Override
  public void saveActivityLogFile(String activityLogFile) {
    preferences.put(KEY_ACTIVITY_LOG_FILE, activityLogFile);
  }
}
