/*
 * Activity Sampling - Backend
 * Copyright (c) 2020 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.backend.messagehandlers;

import de.muspellheim.activitysampling.backend.EventStore;
import de.muspellheim.activitysampling.backend.events.ActivityLoggedEvent;
import de.muspellheim.activitysampling.contract.messages.commands.LogActivityCommand;
import de.muspellheim.messages.CommandHandling;
import de.muspellheim.messages.CommandStatus;
import de.muspellheim.messages.Failure;
import de.muspellheim.messages.Success;
import java.time.ZoneId;
import java.util.logging.Level;
import lombok.extern.java.Log;

@Log
public class LogActivityCommandHandler implements CommandHandling<LogActivityCommand> {
  private final EventStore eventStore;

  public LogActivityCommandHandler(EventStore eventStore) {
    this.eventStore = eventStore;
  }

  public CommandStatus handle(LogActivityCommand command) {
    try {
      eventStore.record(
          new ActivityLoggedEvent(
              command.getTimestamp().atZone(ZoneId.systemDefault()).toInstant(),
              command.getPeriod(),
              command.getActivity(),
              command.getTags()));
      return new Success();
    } catch (Exception e) {
      log.log(Level.WARNING, "Can not handle command: " + command, e);
      return new Failure(e.toString());
    }
  }
}
