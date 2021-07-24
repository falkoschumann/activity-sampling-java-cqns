/*
 * Activity Sampling - Backend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.backend.adapters;

import de.muspellheim.activitysampling.backend.PreferencesStore;
import java.time.Duration;
import java.util.prefs.Preferences;

public class PrefsPreferencesStore implements PreferencesStore {
  private static final String APP_NODE = "/de/muspellheim/activitysampling";
  private static final String KEY_PERIOD_DURATION = "periodDuration";

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
}
