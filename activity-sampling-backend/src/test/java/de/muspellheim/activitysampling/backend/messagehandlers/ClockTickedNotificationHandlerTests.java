/*
 * Activity Sampling - Backend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.backend.messagehandlers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import de.muspellheim.activitysampling.contract.messages.commands.ProgressPeriodCommand;
import de.muspellheim.activitysampling.contract.messages.notification.ClockTickedNotification;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class ClockTickedNotificationHandlerTests {
  private ProgressPeriodCommand progressPeriodCommand;

  @Test
  void handle() {
    var handler = new ClockTickedNotificationHandler();
    handler.setOnProgressPeriodCommand(c -> progressPeriodCommand = c);

    handler.handle(new ClockTickedNotification(LocalDateTime.of(2021, 8, 26, 19, 59)));

    assertEquals(
        new ProgressPeriodCommand(LocalDateTime.of(2021, 8, 26, 19, 59)), progressPeriodCommand);
  }
}
