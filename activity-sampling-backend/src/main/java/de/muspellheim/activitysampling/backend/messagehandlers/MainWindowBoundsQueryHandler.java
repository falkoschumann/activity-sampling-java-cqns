/*
 * Activity Sampling - Backend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.backend.messagehandlers;

import de.muspellheim.activitysampling.backend.PreferencesRepository;
import de.muspellheim.activitysampling.contract.messages.queries.MainWindowBoundsQuery;
import de.muspellheim.activitysampling.contract.messages.queries.MainWindowBoundsQueryResult;

public class MainWindowBoundsQueryHandler {
  private final PreferencesRepository repository;

  public MainWindowBoundsQueryHandler(PreferencesRepository repository) {
    this.repository = repository;
  }

  public MainWindowBoundsQueryResult handle(MainWindowBoundsQuery query) {
    var windowBounds = repository.getMainWindowBounds();
    return new MainWindowBoundsQueryResult(windowBounds);
  }
}
