/*
 * Activity Sampling - Backend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.backend.messagehandlers;

import de.muspellheim.activitysampling.backend.EventStore;
import de.muspellheim.activitysampling.backend.PreferencesStore;
import de.muspellheim.activitysampling.contract.messages.commands.ChangeActivityLogFileCommand;
import de.muspellheim.activitysampling.contract.messages.commands.CommandStatus;
import de.muspellheim.activitysampling.contract.messages.commands.Failure;
import de.muspellheim.activitysampling.contract.messages.commands.Success;

public class ChangeActivityLogFileCommandHandler {
  private final PreferencesStore preferencesStore;
  private final EventStore eventStore;

  public ChangeActivityLogFileCommandHandler(
      PreferencesStore preferencesStore, EventStore eventStore) {
    this.preferencesStore = preferencesStore;
    this.eventStore = eventStore;
  }

  public CommandStatus handle(ChangeActivityLogFileCommand command) {
    try {
      eventStore.setUri(command.activityLogFile());
      preferencesStore.saveActivityLogFile(command.activityLogFile());
      return new Success();
    } catch (Exception e) {
      return new Failure("Storing settings failed: " + e.getMessage());
    }
  }
}
