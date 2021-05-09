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
  private static final Activity ACTIVITY_1 =
      new Activity(
          "a7caf1b0-886e-406f-8fbc-71da9f34714e",
          LocalDateTime.of(2020, 12, 30, 17, 52),
          Duration.ofMinutes(20),
          "A",
          List.of("Foo", "Bar"));
  private static final Activity ACTIVITY_2 =
      new Activity(
          "d5abc0dd-60b0-4a3b-9b2f-8b02005fb256",
          LocalDateTime.of(2020, 12, 30, 21, 20),
          Duration.ofMinutes(20),
          "B");
  private static final Activity ACTIVITY_3 =
      new Activity(
          "e9ed7915-8109-402d-b9e6-2d5764ef688d",
          LocalDateTime.of(2021, 1, 4, 13, 52),
          Duration.ofMinutes(20),
          "B");
  private static final Activity ACTIVITY_4 =
      new Activity(
          "d36a20db-56ae-48af-9221-0630911cdb8d",
          LocalDateTime.of(2021, 1, 4, 14, 20),
          Duration.ofMinutes(20),
          "A",
          List.of("Foo", "Bar"));

  @Test
  void activityLog() throws Exception {
    var eventStore = new MemoryEventStore();
    eventStore.record(createEvents());
    var queryHandler = new ActivityLogQueryHandler(eventStore);

    var result = queryHandler.handle(new ActivityLogQuery());

    assertEquals(
        new ActivityLogQueryResult(List.of(ACTIVITY_1, ACTIVITY_2, ACTIVITY_3, ACTIVITY_4)),
        result);
  }

  private static List<ActivityLoggedEvent> createEvents() {
    return List.of(
        new ActivityLoggedEvent(
            ACTIVITY_1.id(),
            ACTIVITY_1.timestamp().atZone(ZoneId.systemDefault()).toInstant(),
            ACTIVITY_1.period(),
            ACTIVITY_1.activity(),
            ACTIVITY_1.tags()),
        new ActivityLoggedEvent(
            ACTIVITY_2.id(),
            ACTIVITY_2.timestamp().atZone(ZoneId.systemDefault()).toInstant(),
            ACTIVITY_2.period(),
            ACTIVITY_2.activity(),
            List.of()),
        new ActivityLoggedEvent(
            ACTIVITY_3.id(),
            ACTIVITY_3.timestamp().atZone(ZoneId.systemDefault()).toInstant(),
            ACTIVITY_3.period(),
            ACTIVITY_3.activity(),
            List.of()),
        new ActivityLoggedEvent(
            ACTIVITY_4.id(),
            ACTIVITY_4.timestamp().atZone(ZoneId.systemDefault()).toInstant(),
            ACTIVITY_4.period(),
            ACTIVITY_4.activity(),
            ACTIVITY_4.tags()));
  }
}
