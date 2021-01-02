/*
 * Activity Sampling - Backend
 * Copyright (c) 2020 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.backend.messagehandlers;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import de.muspellheim.activitysampling.backend.Event;
import de.muspellheim.activitysampling.backend.EventStore;
import de.muspellheim.activitysampling.backend.adapters.MemoryEventStore;
import de.muspellheim.activitysampling.backend.events.ActivityLoggedEvent;
import de.muspellheim.activitysampling.contract.messages.commands.LogActivityCommand;
import de.muspellheim.activitysampling.contract.messages.notifications.PeriodEndedNotification;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LogActivityCommandHandlerTests {
  private static final Instant TIMESTAMP = Instant.ofEpochSecond(1606063637);

  private EventStore eventStore;
  private LogActivityCommandHandler handler;

  @BeforeEach
  void setUp() {
    eventStore = new MemoryEventStore();
    handler =
        new LogActivityCommandHandler(
            eventStore, Clock.fixed(TIMESTAMP, ZoneId.of("Europe/Berlin")));
  }

  @Test
  void periodEnded() throws Exception {
    handler.handle(new PeriodEndedNotification(Duration.ofMinutes(20)));

    var events = eventStore.replay();

    assertEquals(List.of(), events.collect(Collectors.toList()));
  }

  @Test
  void logActivity() throws Exception {
    handler.handle(new PeriodEndedNotification(Duration.ofMinutes(20)));

    handler.handle(new LogActivityCommand("Lorem ipsum", "Foobar"));

    List<Event> events = eventStore.replay().collect(Collectors.toList());
    assertAll(
        () -> assertEquals(1, events.size(), "Number of events"),
        () -> assertNotNull(events.get(0).getId(), "Event id"),
        () -> assertEquals(TIMESTAMP, events.get(0).getTimestamp(), "Event timestamp"),
        () ->
            assertEquals(
                Duration.ofMinutes(20),
                ((ActivityLoggedEvent) events.get(0)).getPeriod(),
                "Event period"),
        () ->
            assertEquals(
                "Lorem ipsum",
                ((ActivityLoggedEvent) events.get(0)).getActivity(),
                "Event activity"),
        () ->
            assertEquals("Foobar", ((ActivityLoggedEvent) events.get(0)).getTags(), "Event tags"));
  }
}
