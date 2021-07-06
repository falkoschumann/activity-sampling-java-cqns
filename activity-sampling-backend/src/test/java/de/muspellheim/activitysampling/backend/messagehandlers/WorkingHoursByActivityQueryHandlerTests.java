/*
 * Activity Sampling - Backend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.backend.messagehandlers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import de.muspellheim.activitysampling.backend.adapters.MemoryEventStore;
import de.muspellheim.activitysampling.backend.events.ActivityLoggedEvent;
import de.muspellheim.activitysampling.contract.data.WorkingHours;
import de.muspellheim.activitysampling.contract.messages.queries.WorkingHoursByActivityQuery;
import de.muspellheim.activitysampling.contract.messages.queries.WorkingHoursByActivityQueryResult;
import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.junit.jupiter.api.Test;

public class WorkingHoursByActivityQueryHandlerTests {
  @Test
  void testHandle() {
    var store = new MemoryEventStore();
    store.record(createEvents());
    var handler = new WorkingHoursByActivityQueryHandler(store);

    var result = handler.handle(new WorkingHoursByActivityQuery());

    assertEquals(
        new WorkingHoursByActivityQueryResult(
            List.of(
                new WorkingHours("A", List.of(), Duration.ofMinutes(20)),
                new WorkingHours("B", List.of("Foo", "Bar"), Duration.ofMinutes(40)),
                new WorkingHours("C", List.of("Bar"), Duration.ofMinutes(40))),
            new TreeSet<>(List.of("Bar", "Foo"))),
        result);
  }

  @Test
  void testHandle_WithIncludedTags() {
    var store = new MemoryEventStore();
    store.record(createEvents());
    var handler = new WorkingHoursByActivityQueryHandler(store);

    var result = handler.handle(new WorkingHoursByActivityQuery(Set.of("")));

    assertEquals(
        new WorkingHoursByActivityQueryResult(
            List.of(new WorkingHours("A", List.of(), Duration.ofMinutes(20))),
            new TreeSet<>(List.of("Bar", "Foo"))),
        result);
  }

  // TODO Unterscheide nach Aktivität und Tags
  private static List<ActivityLoggedEvent> createEvents() {
    var factory = new EventFactory();
    return List.of(
        factory.create("B", List.of("Foo", "Bar")),
        factory.create("C", List.of("Bar")),
        factory.create("C", List.of("Bar")),
        factory.create("A", List.of()),
        factory.create("B", List.of("Foo")));
  }
}
