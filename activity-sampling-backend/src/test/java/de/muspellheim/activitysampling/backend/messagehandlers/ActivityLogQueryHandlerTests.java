/*
 * Activity Sampling - Backend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.backend.messagehandlers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import de.muspellheim.activitysampling.backend.adapters.MemoryEventStore;
import de.muspellheim.activitysampling.backend.events.ActivityLoggedEvent;
import de.muspellheim.activitysampling.contract.data.Activity;
import de.muspellheim.activitysampling.contract.data.ActivityTemplate;
import de.muspellheim.activitysampling.contract.messages.queries.ActivityLogQuery;
import de.muspellheim.activitysampling.contract.messages.queries.ActivityLogQueryResult;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import org.junit.jupiter.api.Test;

public class ActivityLogQueryHandlerTests {
  @Test
  void testHandle() {
    var store = new MemoryEventStore();
    store.record(
        List.of(
            new ActivityLoggedEvent(
                "a7caf1b0-886e-406f-8fbc-71da9f34714e",
                LocalDateTime.of(2021, 8, 18, 19, 18).atZone(ZoneId.systemDefault()).toInstant(),
                Duration.ofMinutes(20),
                "Wayne Enterprises",
                "Batmobil",
                "Design",
                "Black car"),
            new ActivityLoggedEvent(
                "d5abc0dd-60b0-4a3b-9b2f-8b02005fb256",
                LocalDateTime.of(2021, 8, 18, 19, 38).atZone(ZoneId.systemDefault()).toInstant(),
                Duration.ofMinutes(20),
                "Axis Chemical Co.",
                null,
                null,
                "Double the production of Smylex"),
            new ActivityLoggedEvent(
                "e9ed7915-8109-402d-b9e6-2d5764ef688d",
                LocalDateTime.of(2021, 8, 18, 19, 58).atZone(ZoneId.systemDefault()).toInstant(),
                Duration.ofMinutes(20),
                null,
                "Activity Sampling",
                "Analyze",
                "Write user stories")));
    var handler = new ActivityLogQueryHandler(store);

    var result = handler.handle(new ActivityLogQuery());

    assertEquals(
        new ActivityLogQueryResult(
            List.of(
                new Activity(
                    "a7caf1b0-886e-406f-8fbc-71da9f34714e",
                    LocalDateTime.of(2021, 8, 18, 19, 18),
                    Duration.ofMinutes(20),
                    "Wayne Enterprises",
                    "Batmobil",
                    "Design",
                    "Black car"),
                new Activity(
                    "d5abc0dd-60b0-4a3b-9b2f-8b02005fb256",
                    LocalDateTime.of(2021, 8, 18, 19, 38),
                    Duration.ofMinutes(20),
                    "Axis Chemical Co.",
                    null,
                    null,
                    "Double the production of Smylex"),
                new Activity(
                    "e9ed7915-8109-402d-b9e6-2d5764ef688d",
                    LocalDateTime.of(2021, 8, 18, 19, 58),
                    Duration.ofMinutes(20),
                    null,
                    "Activity Sampling",
                    "Analyze",
                    "Write user stories")),
            List.of(
                new ActivityTemplate(null, "Activity Sampling", "Analyze", "Write user stories"),
                new ActivityTemplate(
                    "Axis Chemical Co.", null, null, "Double the production of Smylex"),
                new ActivityTemplate("Wayne Enterprises", "Batmobil", "Design", "Black car")),
            new ActivityTemplate(null, "Activity Sampling", "Analyze", "Write user stories"),
            List.of("Axis Chemical Co.", "Wayne Enterprises"),
            List.of("Activity Sampling", "Batmobil"),
            List.of("Analyze", "Design")),
        result);
  }
}
