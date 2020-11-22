/*
 * Activity Sampling - Backend
 * Copyright (c) 2020 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.backend.messagehandlers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import de.muspellheim.activitysampling.contract.messages.commands.LogActivityCommand;
import de.muspellheim.activitysampling.contract.messages.events.ActivityLoggedEvent;
import de.muspellheim.activitysampling.contract.messages.notifications.PeriodEndedNotification;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LogActivityCommandHandlerTests {
  private LogActivityCommandHandler handler;
  private List<Object> messages;

  @BeforeEach
  void setUp() {
    handler =
        new LogActivityCommandHandler(
            Clock.fixed(Instant.ofEpochSecond(1606063637), ZoneId.of("Europe/Berlin")));
    messages = new ArrayList<>();
    handler.setOnActivityLoggedEvent(e -> messages.add(e));
  }

  @Test
  void periodEnded() {
    handler.handle(new PeriodEndedNotification(Duration.ofMinutes(20)));

    assertEquals(List.of(), messages);
  }

  @Test
  void logActivity() {
    handler.handle(new PeriodEndedNotification(Duration.ofMinutes(20)));

    handler.handle(new LogActivityCommand("Lorem ipsum", "Foobar"));

    assertEquals(
        List.of(
            new ActivityLoggedEvent(
                LocalDateTime.of(2020, 11, 22, 17, 47, 17),
                Duration.ofMinutes(20),
                "Lorem ipsum",
                "Foobar")),
        messages);
  }
}
