/*
 * Activity Sampling - Backend
 * Copyright (c) 2020 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.backend.messagehandlers;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import de.muspellheim.activitysampling.backend.Event;
import de.muspellheim.activitysampling.backend.adapters.MemoryEventStore;
import de.muspellheim.activitysampling.backend.events.ActivityLoggedEvent;
import de.muspellheim.activitysampling.contract.messages.commands.LogActivityCommand;
import de.muspellheim.messages.Success;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

class LogActivityCommandHandlerTests {

  @Test
  void logActivity() throws Exception {
    var eventStore = new MemoryEventStore();
    var handler = new LogActivityCommandHandler(eventStore);

    var status =
        handler.handle(
            new LogActivityCommand(
                LocalDateTime.of(2020, 11, 22, 17, 47, 17),
                Duration.ofMinutes(20),
                "Lorem ipsum",
                "Foobar"));

    List<Event> events = eventStore.replay().collect(Collectors.toList());
    assertAll(
        () -> assertEquals(new Success(), status, "Command status"),
        () -> assertEquals(1, events.size(), "Number of events"),
        () -> assertNotNull(events.get(0).getId(), "Event id"),
        () ->
            assertEquals(
                LocalDateTime.of(2020, 11, 22, 17, 47, 17)
                    .atZone(ZoneId.systemDefault())
                    .toInstant(),
                events.get(0).getTimestamp(),
                "Event timestamp"),
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
