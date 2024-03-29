/*
 * Activity Sampling - Backend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.backend.messagehandlers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import de.muspellheim.activitysampling.backend.adapters.MemoryEventStore;
import de.muspellheim.activitysampling.backend.events.ActivityLoggedEvent;
import de.muspellheim.activitysampling.contract.data.ActivityTemplate;
import de.muspellheim.activitysampling.contract.messages.queries.ActivityLogQuery;
import de.muspellheim.activitysampling.contract.messages.queries.ActivityLogQueryResult;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Locale;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ActivityLogQueryHandlerTest {
  @BeforeEach
  void init() {
    Locale.setDefault(Locale.GERMANY);
  }

  @Test
  void handle_success() {
    var store = new MemoryEventStore();
    store.record(
        List.of(
            new ActivityLoggedEvent(
                LocalDateTime.of(2021, 8, 18, 19, 18).atZone(ZoneId.systemDefault()).toInstant(),
                Duration.ofMinutes(20),
                "Wayne Enterprises",
                "Batmobil",
                "Design",
                "Create black car"),
            new ActivityLoggedEvent(
                LocalDateTime.of(2021, 8, 18, 19, 38).atZone(ZoneId.systemDefault()).toInstant(),
                Duration.ofMinutes(20),
                "Axis Chemical Co.",
                "Gotham",
                "Production",
                "Double the production of Smylex"),
            new ActivityLoggedEvent(
                LocalDateTime.of(2021, 8, 18, 19, 58).atZone(ZoneId.systemDefault()).toInstant(),
                Duration.ofMinutes(20),
                "Muspellheim",
                "Activity Sampling",
                "Analyze",
                "Write user stories")));
    var handler = new ActivityLogQueryHandler(store);

    var result = handler.handle(new ActivityLogQuery());

    assertEquals(
        new ActivityLogQueryResult(
            """
          Mittwoch, 18. August 2021
          19:18 - Batmobil (Wayne Enterprises) Design - Create black car
          19:38 - Gotham (Axis Chemical Co.) Production - Double the production of Smylex
          19:58 - Activity Sampling (Muspellheim) Analyze - Write user stories
          """,
            List.of(
                new ActivityTemplate(
                    "Muspellheim", "Activity Sampling", "Analyze", "Write user stories"),
                new ActivityTemplate(
                    "Axis Chemical Co.", "Gotham", "Production", "Double the production of Smylex"),
                new ActivityTemplate(
                    "Wayne Enterprises", "Batmobil", "Design", "Create black car")),
            new ActivityTemplate(
                "Muspellheim", "Activity Sampling", "Analyze", "Write user stories"),
            List.of("Axis Chemical Co.", "Muspellheim", "Wayne Enterprises"),
            List.of("Activity Sampling", "Batmobil", "Gotham"),
            List.of("Analyze", "Design", "Production")),
        result);
  }
}
