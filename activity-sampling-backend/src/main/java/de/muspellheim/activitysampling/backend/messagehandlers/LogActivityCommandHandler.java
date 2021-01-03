/*
 * Activity Sampling - Backend
 * Copyright (c) 2020 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.backend.messagehandlers;

import de.muspellheim.activitysampling.backend.EventStore;
import de.muspellheim.activitysampling.backend.events.ActivityLoggedEvent;
import de.muspellheim.activitysampling.contract.messages.commands.LogActivityCommand;
import de.muspellheim.activitysampling.contract.messages.notifications.PeriodEndedNotification;
import de.muspellheim.messages.CommandHandling;
import de.muspellheim.messages.CommandStatus;
import de.muspellheim.messages.Failure;
import de.muspellheim.messages.Success;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

public class LogActivityCommandHandler implements CommandHandling<LogActivityCommand> {
  private final EventStore eventStore;
  private final Clock clock;

  private Instant timestamp;
  private Duration period;

  public LogActivityCommandHandler(EventStore eventStore) {
    this(eventStore, Clock.systemDefaultZone());
  }

  public LogActivityCommandHandler(EventStore eventStore, Clock clock) {
    this.eventStore = eventStore;
    this.clock = clock;
  }

  public CommandStatus handle(LogActivityCommand command) {
    try {
      eventStore.record(
          new ActivityLoggedEvent(timestamp, period, command.getActivity(), command.getTags()));
      return new Success();
    } catch (Exception e) {
      return new Failure(e.toString());
    }
  }

  public void handle(PeriodEndedNotification notification) {
    timestamp = Instant.now(clock);
    period = notification.getPeriod();
  }
}
