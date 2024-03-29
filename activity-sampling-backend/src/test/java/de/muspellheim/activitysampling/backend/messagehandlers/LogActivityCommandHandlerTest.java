/*
 * Activity Sampling - Backend
 * Copyright (c) 2020 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.backend.messagehandlers;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import de.muspellheim.activitysampling.backend.Event;
import de.muspellheim.activitysampling.backend.adapters.MemoryEventStore;
import de.muspellheim.activitysampling.backend.events.ActivityLoggedEvent;
import de.muspellheim.activitysampling.contract.messages.commands.LogActivityCommand;
import de.muspellheim.activitysampling.contract.messages.commands.Success;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

class LogActivityCommandHandlerTest {
  @Test
  void handle_success() {
    var eventStore = new MemoryEventStore();
    var handler = new LogActivityCommandHandler(eventStore);

    var status =
        handler.handle(
            new LogActivityCommand(
                LocalDateTime.of(2020, 11, 22, 17, 47, 17),
                Duration.ofMinutes(20),
                "ACME Ltd.",
                "Foobar",
                "Design",
                "Lorem ipsum"));

    List<Event> events = eventStore.replay().collect(Collectors.toList());
    assertAll(
        () -> assertEquals(new Success(), status, "Command status"),
        () ->
            assertEquals(
                List.of(
                    new ActivityLoggedEvent(
                        LocalDateTime.of(2020, 11, 22, 17, 47, 17)
                            .atZone(ZoneId.systemDefault())
                            .toInstant(),
                        Duration.ofMinutes(20),
                        "ACME Ltd.",
                        "Foobar",
                        "Design",
                        "Lorem ipsum")),
                events));
  }
}
