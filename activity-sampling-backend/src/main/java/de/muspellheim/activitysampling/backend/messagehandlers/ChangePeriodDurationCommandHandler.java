/*
 * Activity Sampling - Backend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.backend.messagehandlers;

import de.muspellheim.activitysampling.backend.PreferencesRepository;
import de.muspellheim.activitysampling.contract.messages.commands.ChangePeriodDurationCommand;
import de.muspellheim.activitysampling.contract.messages.commands.CommandStatus;
import de.muspellheim.activitysampling.contract.messages.commands.Success;

public class ChangePeriodDurationCommandHandler {
  private final PreferencesRepository preferencesStore;

  public ChangePeriodDurationCommandHandler(PreferencesRepository preferencesStore) {
    this.preferencesStore = preferencesStore;
  }

  public CommandStatus handle(ChangePeriodDurationCommand command) {
    preferencesStore.savePeriodDuration(command.getPeriodDuration());
    return new Success();
  }
}
