/*
 * Activity Sampling - Backend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.backend.messagehandlers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import de.muspellheim.activitysampling.backend.adapters.MemoryEventStore;
import de.muspellheim.activitysampling.backend.events.ActivityLoggedEvent;
import de.muspellheim.activitysampling.contract.data.Activity;
import de.muspellheim.activitysampling.contract.messages.queries.WorkingHoursThisWeekQuery;
import de.muspellheim.activitysampling.contract.messages.queries.WorkingHoursThisWeekQueryResult;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import org.junit.jupiter.api.Test;

public class WorkingHoursThisWeekQueryHandlerTests {
  @Test
  void testHandle() {
    var store = new MemoryEventStore();
    store.record(createEvents());
    var handler = new WorkingHoursThisWeekQueryHandler(store);

    var result = handler.handle(new WorkingHoursThisWeekQuery());

    assertEquals(
        new WorkingHoursThisWeekQueryResult(
            25,
            Duration.ofMinutes(60),
            List.of(
                new Activity(
                    "d5abc0dd-60b0-4a3b-9b2f-8b02005fb256",
                    LocalDateTime.of(2021, 6, 21, 21, 20),
                    Duration.ofMinutes(20),
                    "B"),
                new Activity(
                    "e9ed7915-8109-402d-b9e6-2d5764ef688d",
                    LocalDateTime.of(2021, 6, 22, 13, 52),
                    Duration.ofMinutes(20),
                    "B"),
                new Activity(
                    "d36a20db-56ae-48af-9221-0630911cdb8d",
                    LocalDateTime.of(2021, 6, 22, 14, 20),
                    Duration.ofMinutes(20),
                    "A",
                    List.of("Foo", "Bar")))),
        result);
  }

  private static List<ActivityLoggedEvent> createEvents() {
    return List.of(
        new ActivityLoggedEvent(
            "a7caf1b0-886e-406f-8fbc-71da9f34714e",
            LocalDateTime.of(2021, 6, 18, 17, 52).atZone(ZoneId.systemDefault()).toInstant(),
            Duration.ofMinutes(20),
            "A",
            List.of("Foo", "Bar")),
        new ActivityLoggedEvent(
            "d5abc0dd-60b0-4a3b-9b2f-8b02005fb256",
            LocalDateTime.of(2021, 6, 21, 21, 20).atZone(ZoneId.systemDefault()).toInstant(),
            Duration.ofMinutes(20),
            "B"),
        new ActivityLoggedEvent(
            "e9ed7915-8109-402d-b9e6-2d5764ef688d",
            LocalDateTime.of(2021, 6, 22, 13, 52).atZone(ZoneId.systemDefault()).toInstant(),
            Duration.ofMinutes(20),
            "B"),
        new ActivityLoggedEvent(
            "d36a20db-56ae-48af-9221-0630911cdb8d",
            LocalDateTime.of(2021, 6, 22, 14, 20).atZone(ZoneId.systemDefault()).toInstant(),
            Duration.ofMinutes(20),
            "A",
            List.of("Foo", "Bar")));
  }
}
