/*
 * Activity Sampling - Backend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.backend.messagehandlers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import de.muspellheim.activitysampling.backend.adapters.MemoryEventStore;
import de.muspellheim.activitysampling.backend.events.ActivityLoggedEvent;
import de.muspellheim.activitysampling.contract.messages.queries.WorkingHoursByNumberQuery;
import de.muspellheim.activitysampling.contract.messages.queries.WorkingHoursByNumberQueryResult;
import de.muspellheim.activitysampling.contract.messages.queries.WorkingHoursByNumberQueryResult.WorkingHoursCategory;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import org.junit.jupiter.api.Test;

public class WorkingHoursByNumberQueryHandlerTests {
  @Test
  void testHandle() {
    var store = new MemoryEventStore();
    store.record(createEvents());
    var handler = new WorkingHoursByNumberQueryHandler(store);

    var result = handler.handle(new WorkingHoursByNumberQuery());

    assertEquals(
        new WorkingHoursByNumberQueryResult(
            List.of(new WorkingHoursCategory(Duration.ofHours(1), 3))),
        result);
  }

  private static List<ActivityLoggedEvent> createEvents() {
    return List.of(
        new ActivityLoggedEvent(
            "a7caf1b0-886e-406f-8fbc-71da9f34714e",
            LocalDateTime.of(2020, 12, 30, 17, 52).atZone(ZoneId.systemDefault()).toInstant(),
            Duration.ofMinutes(20),
            "B",
            List.of("Foo", "Bar")),
        new ActivityLoggedEvent(
            "d5abc0dd-60b0-4a3b-9b2f-8b02005fb256",
            LocalDateTime.of(2020, 12, 30, 21, 20).atZone(ZoneId.systemDefault()).toInstant(),
            Duration.ofMinutes(20),
            "A"),
        new ActivityLoggedEvent(
            "3a3e7c93-16f3-4a25-a43e-f8d28590a26f",
            LocalDateTime.of(2021, 1, 4, 13, 0).atZone(ZoneId.systemDefault()).toInstant(),
            Duration.ofMinutes(20),
            "C"),
        new ActivityLoggedEvent(
            "d36a20db-56ae-48af-9221-0630911cdb8d",
            LocalDateTime.of(2021, 1, 4, 14, 20).atZone(ZoneId.systemDefault()).toInstant(),
            Duration.ofMinutes(20),
            "B",
            List.of("Foo")));
  }
}
