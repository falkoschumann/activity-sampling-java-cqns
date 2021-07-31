/*
 * Activity Sampling - Backend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.backend;

import de.muspellheim.activitysampling.contract.data.Bounds;
import java.time.Duration;

public interface PreferencesRepository {
  Duration loadPeriodDuration();

  void savePeriodDuration(Duration periodDuration);

  Bounds loadMainWindowBounds();

  void storeMainWindowBounds(Bounds bounds);
}
