/*
 * Activity Sampling - Backend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.backend.messagehandlers;

import de.muspellheim.activitysampling.contract.messages.commands.ProgressPeriodCommand;
import de.muspellheim.activitysampling.contract.messages.notification.ClockTickedNotification;
import java.util.function.Consumer;
import lombok.Getter;
import lombok.Setter;

public class ClockTickedNotificationHandler {
  @Getter @Setter Consumer<ProgressPeriodCommand> onProgressPeriodCommand;

  public void handle(ClockTickedNotification notification) {
    onProgressPeriodCommand.accept(new ProgressPeriodCommand(notification.timestamp()));
  }
}
