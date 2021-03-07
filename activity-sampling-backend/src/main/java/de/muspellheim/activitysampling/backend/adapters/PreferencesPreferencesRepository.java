/*
 * Activity Sampling - Backend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.backend.adapters;

import de.muspellheim.activitysampling.backend.PreferencesRepository;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.prefs.Preferences;

public class PreferencesPreferencesRepository implements PreferencesRepository {
  private static final String KEY_PREFIX = "de.muspellheim.activitysampling";
  private static final String KEY_PERIOD_DURATION = KEY_PREFIX + "/periodDuration";
  private static final String KEY_ACTIVITY_LOG_FILE = KEY_PREFIX + "/activityLogFile";

  private final Preferences preferences = Preferences.userRoot();

  @Override
  public Duration loadPeriodDuration() {
    var defaultValue = Duration.ofMinutes(20).toString();
    var value = preferences.get(KEY_PERIOD_DURATION, defaultValue);
    return Duration.parse(value);
  }

  @Override
  public void savePeriodDuration(Duration periodDuration) {
    preferences.put(KEY_PERIOD_DURATION, periodDuration.toString());
  }

  @Override
  public Path loadActivityLogFile() {
    var defaultValue = Paths.get(System.getProperty("user.home"), "activity-log.csv").toString();
    var value = preferences.get(KEY_ACTIVITY_LOG_FILE, defaultValue);
    return Paths.get(value);
  }

  @Override
  public void saveActivityLogFile(Path activityLogFile) {
    preferences.put(KEY_ACTIVITY_LOG_FILE, activityLogFile.toString());
  }
}
