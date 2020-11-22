/*
 * Activity Sampling - Backend
 * Copyright (c) 2020 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.backend.messagehandlers;

import de.muspellheim.activitysampling.contract.messages.commands.CommandStatus;
import de.muspellheim.activitysampling.contract.messages.commands.LogActivityCommand;
import de.muspellheim.activitysampling.contract.messages.commands.Success;
import de.muspellheim.activitysampling.contract.messages.events.ActivityLoggedEvent;
import de.muspellheim.activitysampling.contract.messages.notifications.PeriodEndedNotification;
import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.function.Consumer;
import lombok.Getter;
import lombok.Setter;

public class LogActivityCommandHandler {

  @Getter @Setter private Consumer<ActivityLoggedEvent> onActivityLoggedEvent;

  private final Clock clock;

  private LocalDateTime timestamp;
  private Duration period;

  public LogActivityCommandHandler() {
    this(Clock.systemDefaultZone());
  }

  public LogActivityCommandHandler(Clock clock) {
    this.clock = clock;
  }

  public CommandStatus handle(LogActivityCommand command) {
    onActivityLoggedEvent.accept(
        new ActivityLoggedEvent(timestamp, period, command.getActivity(), command.getTags()));
    return new Success();
  }

  public void handle(PeriodEndedNotification notification) {
    timestamp = LocalDateTime.now(clock);
    period = notification.getPeriod();
  }
}
