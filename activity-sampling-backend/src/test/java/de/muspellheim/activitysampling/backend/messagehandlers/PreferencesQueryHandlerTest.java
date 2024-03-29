/*
 * Activity Sampling - Backend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.backend.messagehandlers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import de.muspellheim.activitysampling.backend.adapters.MemoryPreferencesRepository;
import de.muspellheim.activitysampling.contract.messages.queries.PreferencesQuery;
import de.muspellheim.activitysampling.contract.messages.queries.PreferencesQueryResult;
import java.time.Duration;
import org.junit.jupiter.api.Test;

class PreferencesQueryHandlerTest {
  @Test
  void handle_success() {
    var repository = new MemoryPreferencesRepository().addExamples();
    var handler = new PreferencesQueryHandler(repository);

    var result = handler.handle(new PreferencesQuery());

    assertEquals(new PreferencesQueryResult(Duration.ofMinutes(2)), result);
  }
}
