/*
 * Activity Sampling - Backend
 * Copyright (c) 2020 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.backend.messagehandlers;

import de.muspellheim.activitysampling.backend.EventStore;
import de.muspellheim.activitysampling.backend.events.ActivityLoggedEvent;
import de.muspellheim.activitysampling.contract.messages.commands.CommandStatus;
import de.muspellheim.activitysampling.contract.messages.commands.LogActivityCommand;
import de.muspellheim.activitysampling.contract.messages.commands.Success;
import de.muspellheim.activitysampling.contract.messages.notifications.PeriodEndedNotification;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

public class LogActivityCommandHandler {
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
    eventStore.record(
        new ActivityLoggedEvent(timestamp, period, command.getActivity(), command.getTags()));
    return new Success();
  }

  public void handle(PeriodEndedNotification notification) {
    timestamp = Instant.now(clock);
    period = notification.getPeriod();
  }
}
