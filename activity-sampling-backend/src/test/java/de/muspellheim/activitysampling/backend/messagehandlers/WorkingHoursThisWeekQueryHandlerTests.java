/*
 * Activity Sampling - Backend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.backend.messagehandlers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import de.muspellheim.activitysampling.backend.adapters.MemoryEventStore;
import de.muspellheim.activitysampling.backend.events.ActivityLoggedEvent;
import de.muspellheim.activitysampling.contract.messages.queries.Queries;
import de.muspellheim.activitysampling.contract.messages.queries.WorkingHoursThisWeekQuery;
import de.muspellheim.activitysampling.contract.messages.queries.WorkingHoursThisWeekQueryResult;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.junit.jupiter.api.Test;

public class WorkingHoursThisWeekQueryHandlerTests {
  private static final Instant START_TIMESTAMP =
      LocalDateTime.of(2021, 7, 5, 9, 0).atZone(ZoneId.systemDefault()).toInstant();
  private static final Instant CURRENT_TIMESTAMP = START_TIMESTAMP.plus(9, ChronoUnit.DAYS);

  @Test
  void testHandle() {
    var store = new MemoryEventStore();
    var events = createEvents();
    store.record(events);
    var handler =
        new WorkingHoursThisWeekQueryHandler(
            store, Clock.fixed(CURRENT_TIMESTAMP, ZoneId.systemDefault()));

    var result = handler.handle(new WorkingHoursThisWeekQuery());

    assertEquals(
        new WorkingHoursThisWeekQueryResult(
            28,
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
        new WorkingHoursThisWeekQueryHandler(
            store, Clock.fixed(CURRENT_TIMESTAMP, ZoneId.systemDefault()));

    var result = handler.handle(new WorkingHoursThisWeekQuery(Set.of("Foo", Queries.NO_TAG)));

    assertEquals(
        new WorkingHoursThisWeekQueryResult(
            28,
            Duration.ofMinutes(60),
            List.of(
                ActivityFactory.create(events.get(1)),
                ActivityFactory.create(events.get(3)),
                ActivityFactory.create(events.get(4))),
            new TreeSet<>(List.of("Bar", "Foo"))),
        result);
  }

  private static List<ActivityLoggedEvent> createEvents() {
    var factory = new EventFactory();
    return List.of(
        factory.create("B", List.of("Foo", "Bar")),
        factory.nextWeek().create("B", List.of("Foo", "Bar")),
        factory.create("C", List.of("Bar")),
        factory.nextDay().create("A", List.of()),
        factory.create("B", List.of("Foo")));
  }
}
