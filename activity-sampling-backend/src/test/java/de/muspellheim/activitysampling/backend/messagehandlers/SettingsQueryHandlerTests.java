/*
 * Activity Sampling - Backend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.backend.messagehandlers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.muspellheim.activitysampling.backend.adapters.MemorySettingsRepository;
import de.muspellheim.activitysampling.contract.messages.queries.SettingsQuery;
import java.time.Duration;
import org.junit.jupiter.api.Test;

public class SettingsQueryHandlerTests {
  @Test
  void settings() {
    var settingsRepository = new MemorySettingsRepository();
    var queryHandler = new SettingsQueryHandler(settingsRepository);

    var result = queryHandler.handle(new SettingsQuery());

    assertEquals(result.periodDuration(), Duration.ofMinutes(20));
    assertTrue(result.activityLogFile().toString().endsWith("activity-log.csv"));
  }
}
