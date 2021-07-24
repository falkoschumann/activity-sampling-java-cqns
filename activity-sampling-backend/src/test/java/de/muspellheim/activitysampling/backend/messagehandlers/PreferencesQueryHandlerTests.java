/*
 * Activity Sampling - Backend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.backend.messagehandlers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import de.muspellheim.activitysampling.backend.adapters.MemoryPreferencesStore;
import de.muspellheim.activitysampling.contract.messages.queries.PreferencesQuery;
import de.muspellheim.activitysampling.contract.messages.queries.PreferencesQueryResult;
import java.time.Duration;
import org.junit.jupiter.api.Test;

public class PreferencesQueryHandlerTests {
  @Test
  void testHandle() {
    var store = new MemoryPreferencesStore().addExamples();
    var handler = new PreferencesQueryHandler(store);

    var result = handler.handle(new PreferencesQuery());

    assertEquals(new PreferencesQueryResult(Duration.ofMinutes(2)), result);
  }
}
