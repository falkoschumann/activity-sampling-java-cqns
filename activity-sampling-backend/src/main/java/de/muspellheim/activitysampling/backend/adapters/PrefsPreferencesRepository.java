/*
 * Activity Sampling - Backend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.backend.adapters;

import de.muspellheim.activitysampling.contract.data.Bounds;
import java.time.Duration;
import java.util.prefs.Preferences;

public class PrefsPreferencesRepository extends AbstractPreferencesRepository {
  private static final String APP_NODE = "/de/muspellheim/activitysampling";
  private static final String KEY_PERIOD = "period";
  private static final String MAIN_WINDOW_BOUNDS_X = "mainWindowBounds/x";
  private static final String MAIN_WINDOW_BOUNDS_Y = "mainWindowBounds/y";
  private static final String MAIN_WINDOW_BOUNDS_WIDTH = "mainWindowBounds/width";
  private static final String MAIN_WINDOW_BOUNDS_HEIGHT = "mainWindowBounds/height";

  private final Preferences preferences = Preferences.userRoot().node(APP_NODE);

  @Override
  public Duration getPeriod() {
    var value = preferences.get(KEY_PERIOD, "PT20M");
    return Duration.parse(value);
  }

  @Override
  public void setPeriod(Duration period) {
    preferences.put(KEY_PERIOD, period.toString());
  }

  @Override
  public Bounds getMainWindowBounds() {
    var x = preferences.getDouble(MAIN_WINDOW_BOUNDS_X, 0);
    var y = preferences.getDouble(MAIN_WINDOW_BOUNDS_Y, 0);
    var width = preferences.getDouble(MAIN_WINDOW_BOUNDS_WIDTH, 0);
    var height = preferences.getDouble(MAIN_WINDOW_BOUNDS_HEIGHT, 0);
    return new Bounds(x, y, width, height);
  }

  @Override
  public void setMainWindowBounds(Bounds bounds) {
    preferences.putDouble(MAIN_WINDOW_BOUNDS_X, bounds.x());
    preferences.putDouble(MAIN_WINDOW_BOUNDS_Y, bounds.y());
    preferences.putDouble(MAIN_WINDOW_BOUNDS_WIDTH, bounds.width());
    preferences.putDouble(MAIN_WINDOW_BOUNDS_HEIGHT, bounds.height());
  }
}
