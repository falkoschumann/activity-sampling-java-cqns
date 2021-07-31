/*
 * Activity Sampling - Backend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.backend.messagehandlers;

import de.muspellheim.activitysampling.backend.PreferencesRepository;
import de.muspellheim.activitysampling.contract.messages.queries.PreferencesQuery;
import de.muspellheim.activitysampling.contract.messages.queries.PreferencesQueryResult;

public class PreferencesQueryHandler {
  private final PreferencesRepository repository;

  public PreferencesQueryHandler(PreferencesRepository repository) {
    this.repository = repository;
  }

  public PreferencesQueryResult handle(PreferencesQuery query) {
    var periodDuration = repository.loadPeriodDuration();
    return new PreferencesQueryResult(periodDuration);
  }
}
