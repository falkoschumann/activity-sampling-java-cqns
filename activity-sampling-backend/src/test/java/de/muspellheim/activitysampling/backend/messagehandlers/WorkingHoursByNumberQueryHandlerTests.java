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
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
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
            List.of(
                new WorkingHoursCategory(Duration.ofHours(1), 2),
                new WorkingHoursCategory(Duration.ofHours(2), 1)),
            new TreeSet<>(List.of("Bar", "Foo"))),
        result);
  }

  @Test
  void testHandle_WithIncludedTags() {
    var store = new MemoryEventStore();
    store.record(createEvents());
    var handler = new WorkingHoursByNumberQueryHandler(store);

    var result = handler.handle(new WorkingHoursByNumberQuery(Set.of("Foo")));

    assertEquals(
        new WorkingHoursByNumberQueryResult(
            List.of(new WorkingHoursCategory(Duration.ofHours(1), 1)),
            new TreeSet<>(List.of("Bar", "Foo"))),
        result);
  }

  private static List<ActivityLoggedEvent> createEvents() {
    var factory = new EventFactory();
    return List.of(
        factory.create("B", List.of("Foo", "Bar")),
        factory.create("C", List.of("Bar")),
        factory.create("C", List.of("Bar")),
        factory.create("C", List.of("Bar")),
        factory.create("C", List.of("Bar")),
        factory.create("A", List.of()),
        factory.create("B", List.of("Foo")),
        factory.create("B", List.of("Foo")));
  }
}
