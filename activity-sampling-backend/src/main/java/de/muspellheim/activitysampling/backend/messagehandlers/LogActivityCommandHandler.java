/*
 * Activity Sampling - Backend
 * Copyright (c) 2020 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.backend.messagehandlers;

import de.muspellheim.activitysampling.backend.EventStore;
import de.muspellheim.activitysampling.backend.events.ActivityLoggedEvent;
import de.muspellheim.activitysampling.contract.messages.commands.CommandStatus;
import de.muspellheim.activitysampling.contract.messages.commands.Failure;
import de.muspellheim.activitysampling.contract.messages.commands.LogActivityCommand;
import de.muspellheim.activitysampling.contract.messages.commands.Success;
import java.time.ZoneId;
import java.util.LinkedHashSet;
import java.util.List;

public class LogActivityCommandHandler {
  private final EventStore eventStore;

  public LogActivityCommandHandler(EventStore eventStore) {
    this.eventStore = eventStore;
  }

  public CommandStatus handle(LogActivityCommand command) {
    try {
      var tags = new LinkedHashSet<>(command.tags());
      eventStore.record(
          new ActivityLoggedEvent(
              command.timestamp().atZone(ZoneId.systemDefault()).toInstant(),
              command.period(),
              command.activity(),
              List.copyOf(tags)));
      return new Success();
    } catch (Exception e) {
      return new Failure("Recording event failed: " + e.getMessage());
    }
  }
}
