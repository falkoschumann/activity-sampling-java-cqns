/*
 * Activity Sampling - Backend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.backend.messagehandlers;

import de.muspellheim.activitysampling.backend.PreferencesStore;
import de.muspellheim.activitysampling.contract.messages.queries.PreferencesQuery;
import de.muspellheim.activitysampling.contract.messages.queries.PreferencesQueryResult;
import de.muspellheim.messages.QueryHandling;

public class PreferencesQueryHandler
    implements QueryHandling<PreferencesQuery, PreferencesQueryResult> {
  private final PreferencesStore preferencesStore;

  public PreferencesQueryHandler(PreferencesStore preferencesStore) {
    this.preferencesStore = preferencesStore;
  }

  @Override
  public PreferencesQueryResult handle(PreferencesQuery query) {
    var periodDuration = preferencesStore.loadPeriodDuration();
    var activityLogFile = preferencesStore.loadActivityLogFile();
    return new PreferencesQueryResult(periodDuration, activityLogFile);
  }
}
