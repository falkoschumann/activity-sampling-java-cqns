/*
 * Activity Sampling - Backend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.backend.messagehandlers;

import de.muspellheim.activitysampling.backend.PreferencesStore;
import de.muspellheim.activitysampling.contract.messages.commands.ChangeActivityLogFileCommand;
import de.muspellheim.messages.CommandHandling;
import de.muspellheim.messages.CommandStatus;
import de.muspellheim.messages.Success;

public class ChangeActivityLogFileCommandHandler
    implements CommandHandling<ChangeActivityLogFileCommand> {
  private final PreferencesStore preferencesStore;

  public ChangeActivityLogFileCommandHandler(PreferencesStore preferencesStore) {
    this.preferencesStore = preferencesStore;
  }

  @Override
  public CommandStatus handle(ChangeActivityLogFileCommand command) {
    preferencesStore.saveActivityLogFile(command.getActivityLogFile());
    return new Success();
  }
}
