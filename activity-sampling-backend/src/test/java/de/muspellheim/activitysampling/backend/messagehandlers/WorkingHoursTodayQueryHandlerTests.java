/*
 * Activity Sampling - Backend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.backend.messagehandlers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import de.muspellheim.activitysampling.backend.adapters.MemoryEventStore;
import de.muspellheim.activitysampling.backend.events.ActivityLoggedEvent;
import de.muspellheim.activitysampling.contract.messages.queries.WorkingHoursTodayQuery;
import de.muspellheim.activitysampling.contract.messages.queries.WorkingHoursTodayQueryResult;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.junit.jupiter.api.Test;

public class WorkingHoursTodayQueryHandlerTests {
  private static final Instant START_TIMESTAMP =
      LocalDateTime.of(2021, 7, 5, 9, 0).atZone(ZoneId.systemDefault()).toInstant();
  private static final Instant CURRENT_TIMESTAMP = START_TIMESTAMP.plus(1, ChronoUnit.DAYS);

  @Test
  void testHandle() {
    var store = new MemoryEventStore();
    var events = createEvents();
    store.record(events);
    var handler =
        new WorkingHoursTodayQueryHandler(
            store, Clock.fixed(CURRENT_TIMESTAMP, ZoneId.systemDefault()));

    var result = handler.handle(new WorkingHoursTodayQuery());

    assertEquals(
        new WorkingHoursTodayQueryResult(
            LocalDate.ofInstant(CURRENT_TIMESTAMP, ZoneId.systemDefault()),
            Duration.ofMinutes(80),
            List.of(
                ActivityFactory.create(events.get(1)),
                ActivityFactory.create(events.get(2)),
                ActivityFactory.create(events.get(3)),
                ActivityFactory.create(events.get(4))),
            new TreeSet<>(List.of("Bar", "Foo"))),
        result);
  }

  @Test
  void testHandle_WithIncludedTags() {
    var store = new MemoryEventStore();
    var events = createEvents();
    store.record(events);
    var handler =
        new WorkingHoursTodayQueryHandler(
            store, Clock.fixed(CURRENT_TIMESTAMP, ZoneId.systemDefault()));

    var result = handler.handle(new WorkingHoursTodayQuery(Set.of("Bar")));

    assertEquals(
        new WorkingHoursTodayQueryResult(
            LocalDate.ofInstant(CURRENT_TIMESTAMP, ZoneId.systemDefault()),
            Duration.ofMinutes(40),
            List.of(ActivityFactory.create(events.get(1)), ActivityFactory.create(events.get(2))),
            new TreeSet<>(List.of("Bar", "Foo"))),
        result);
  }

  private static List<ActivityLoggedEvent> createEvents() {
    var factory = new EventFactory();
    return List.of(
        factory.create("B", List.of("Foo", "Bar")),
        factory.nextDay().create("B", List.of("Foo", "Bar")),
        factory.create("C", List.of("Bar")),
        factory.create("A", List.of()),
        factory.create("B", List.of("Foo")));
  }
}
