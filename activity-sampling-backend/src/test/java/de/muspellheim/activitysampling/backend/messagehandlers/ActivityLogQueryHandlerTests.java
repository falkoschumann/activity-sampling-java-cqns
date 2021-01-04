/*
 * Activity Sampling - Backend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.backend.messagehandlers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import de.muspellheim.activitysampling.backend.adapters.MemoryEventStore;
import de.muspellheim.activitysampling.backend.events.ActivityLoggedEvent;
import de.muspellheim.activitysampling.contract.data.Activity;
import de.muspellheim.activitysampling.contract.messages.queries.ActivityLogQuery;
import de.muspellheim.activitysampling.contract.messages.queries.ActivityLogQueryResult;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import org.junit.jupiter.api.Test;

public class ActivityLogQueryHandlerTests {
  @Test
  void activityLog() throws Exception {
    var eventStore = new MemoryEventStore();
    eventStore.record(createEvents());
    var handler = new ActivityLogQueryHandler(eventStore);

    var result = handler.handle(new ActivityLogQuery());

    assertEquals(
        new ActivityLogQueryResult(
            List.of(
                new Activity(
                    "a7caf1b0-886e-406f-8fbc-71da9f34714e",
                    LocalDateTime.of(2020, 12, 30, 17, 52),
                    Duration.ofMinutes(20),
                    "Taste JavaScript"),
                new Activity(
                    "d5abc0dd-60b0-4a3b-9b2f-8b02005fb256",
                    LocalDateTime.of(2020, 12, 30, 21, 20),
                    Duration.ofMinutes(20),
                    "Create a TodoMVC template",
                    List.of("Test", "TodoMVC")))),
        result);
  }

  private static List<ActivityLoggedEvent> createEvents() {
    return List.of(
        new ActivityLoggedEvent(
            "a7caf1b0-886e-406f-8fbc-71da9f34714e",
            LocalDateTime.of(2020, 12, 30, 17, 52).atZone(ZoneId.systemDefault()).toInstant(),
            Duration.ofMinutes(20),
            "Taste JavaScript",
            null),
        new ActivityLoggedEvent(
            "d5abc0dd-60b0-4a3b-9b2f-8b02005fb256",
            LocalDateTime.of(2020, 12, 30, 21, 20).atZone(ZoneId.systemDefault()).toInstant(),
            Duration.ofMinutes(20),
            "Create a TodoMVC template",
            "Test, TodoMVC"));
  }
}
