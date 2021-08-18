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
import java.time.ZoneId;
import java.util.UUID;
import java.util.function.Supplier;

public class LogActivityCommandHandler {
  private final EventStore eventStore;
  private final Supplier<String> idGenerator;

  public LogActivityCommandHandler(EventStore eventStore) {
    this(eventStore, () -> UUID.randomUUID().toString());
  }

  public LogActivityCommandHandler(EventStore eventStore, Supplier<String> idGenerator) {
    this.eventStore = eventStore;
    this.idGenerator = idGenerator;
  }

  public CommandStatus handle(LogActivityCommand command) {
    eventStore.record(
        new ActivityLoggedEvent(
            idGenerator.get(),
            command.timestamp().atZone(ZoneId.systemDefault()).toInstant(),
            command.period(),
            command.client(),
            command.project(),
            command.task(),
            command.notes(),
            command.tags()));
    return new Success();
  }
}
