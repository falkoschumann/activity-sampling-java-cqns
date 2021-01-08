/*
 * Activity Sampling - Backend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.backend.messagehandlers;

import de.muspellheim.activitysampling.backend.PreferencesStore;
import de.muspellheim.activitysampling.contract.messages.commands.ChangePeriodDurationCommand;
import de.muspellheim.messages.CommandHandling;
import de.muspellheim.messages.CommandStatus;
import de.muspellheim.messages.Success;

public class ChangePeriodDurationCommandHandler
    implements CommandHandling<ChangePeriodDurationCommand> {
  private final PreferencesStore preferencesStore;

  public ChangePeriodDurationCommandHandler(PreferencesStore preferencesStore) {
    this.preferencesStore = preferencesStore;
  }

  @Override
  public CommandStatus handle(ChangePeriodDurationCommand command) {
    preferencesStore.savePeriodDuration(command.getPeriodDuration());
    return new Success();
  }
}
