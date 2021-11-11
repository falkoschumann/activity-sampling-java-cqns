/*
 * Activity Sampling - Backend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.backend.messagehandlers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import de.muspellheim.activitysampling.backend.adapters.MemoryPreferencesRepository;
import de.muspellheim.activitysampling.contract.data.Bounds;
import de.muspellheim.activitysampling.contract.messages.queries.MainWindowBoundsQuery;
import de.muspellheim.activitysampling.contract.messages.queries.MainWindowBoundsQueryResult;
import org.junit.jupiter.api.Test;

class MainWindowBoundsQueryHandlerTest {
  @Test
  void handle_success() {
    var repository = new MemoryPreferencesRepository();
    repository.addExamples();
    var handler = new MainWindowBoundsQueryHandler(repository);

    var result = handler.handle(new MainWindowBoundsQuery());

    assertEquals(new MainWindowBoundsQueryResult(new Bounds(360, 240, 640, 480)), result);
  }
}
