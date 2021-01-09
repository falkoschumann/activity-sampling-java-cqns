/*
 * Activity Sampling - Backend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.backend.messagehandlers;

import de.muspellheim.activitysampling.backend.EventStore;
import de.muspellheim.activitysampling.backend.PreferencesStore;
import de.muspellheim.activitysampling.contract.messages.commands.ChangeActivityLogFileCommand;
import de.muspellheim.messages.CommandHandling;
import de.muspellheim.messages.CommandStatus;
import de.muspellheim.messages.Success;

public class ChangeActivityLogFileCommandHandler
    implements CommandHandling<ChangeActivityLogFileCommand> {
  private final PreferencesStore preferencesStore;
  private final EventStore eventStore;

  public ChangeActivityLogFileCommandHandler(
      PreferencesStore preferencesStore, EventStore eventStore) {
    this.preferencesStore = preferencesStore;
    this.eventStore = eventStore;
  }

  @Override
  public CommandStatus handle(ChangeActivityLogFileCommand command) {
    eventStore.setUri(command.getActivityLogFile().toString());
    preferencesStore.saveActivityLogFile(command.getActivityLogFile());
    return new Success();
  }
}
