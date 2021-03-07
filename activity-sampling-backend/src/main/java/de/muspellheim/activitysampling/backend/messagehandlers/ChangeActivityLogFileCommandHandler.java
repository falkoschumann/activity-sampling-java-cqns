/*
 * Activity Sampling - Backend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.backend.messagehandlers;

import de.muspellheim.activitysampling.backend.EventStore;
import de.muspellheim.activitysampling.backend.PreferencesRepository;
import de.muspellheim.activitysampling.contract.messages.commands.ChangeActivityLogFileCommand;
import de.muspellheim.activitysampling.contract.messages.commands.CommandStatus;
import de.muspellheim.activitysampling.contract.messages.commands.Success;

public class ChangeActivityLogFileCommandHandler {
  private final PreferencesRepository preferencesStore;
  private final EventStore eventStore;

  public ChangeActivityLogFileCommandHandler(
      PreferencesRepository preferencesStore, EventStore eventStore) {
    this.preferencesStore = preferencesStore;
    this.eventStore = eventStore;
  }

  public CommandStatus handle(ChangeActivityLogFileCommand command) {
    eventStore.setUri(command.getActivityLogFile().toString());
    preferencesStore.saveActivityLogFile(command.getActivityLogFile());
    return new Success();
  }
}
