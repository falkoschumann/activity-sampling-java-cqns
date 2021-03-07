/*
 * Activity Sampling - Backend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.backend.messagehandlers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.muspellheim.activitysampling.backend.adapters.MemoryPreferencesRepository;
import de.muspellheim.activitysampling.contract.messages.queries.PreferencesQuery;
import java.time.Duration;
import org.junit.jupiter.api.Test;

public class PreferencesQueryHandlerTests {
  @Test
  void preferences() {
    var store = new MemoryPreferencesRepository();
    var handler = new PreferencesQueryHandler(store);

    var result = handler.handle(new PreferencesQuery());

    assertEquals(result.getPeriodDuration(), Duration.ofMinutes(20));
    assertTrue(result.getActivityLogFile().toString().endsWith("activity-log.csv"));
  }
}
