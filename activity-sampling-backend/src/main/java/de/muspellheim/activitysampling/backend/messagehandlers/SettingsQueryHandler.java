/*
 * Activity Sampling - Backend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.backend.messagehandlers;

import de.muspellheim.activitysampling.backend.SettingsRepository;
import de.muspellheim.activitysampling.contract.messages.queries.SettingsQuery;
import de.muspellheim.activitysampling.contract.messages.queries.SettingsQueryResult;

public class SettingsQueryHandler {
  private final SettingsRepository settingsStore;

  public SettingsQueryHandler(SettingsRepository settingsStore) {
    this.settingsStore = settingsStore;
  }

  public SettingsQueryResult handle(SettingsQuery query) {
    try {
      var periodDuration = settingsStore.loadPeriodDuration();
      var activityLogFile = settingsStore.loadActivityLogFile();
      return new SettingsQueryResult(periodDuration, activityLogFile);
    } catch (Exception e) {
      return new SettingsQueryResult(
          SettingsRepository.DEFAULT_PERIOD_DURATION, SettingsRepository.DEFAULT_ACTIVITY_LOG_FILE);
    }
  }
}
