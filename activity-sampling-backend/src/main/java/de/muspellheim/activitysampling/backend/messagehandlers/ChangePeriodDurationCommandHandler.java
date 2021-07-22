/*
 * Activity Sampling - Backend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.backend.messagehandlers;

import de.muspellheim.activitysampling.backend.PreferencesStore;
import de.muspellheim.activitysampling.contract.messages.commands.ChangePeriodDurationCommand;
import de.muspellheim.activitysampling.contract.messages.commands.CommandStatus;
import de.muspellheim.activitysampling.contract.messages.commands.Failure;
import de.muspellheim.activitysampling.contract.messages.commands.Success;

public class ChangePeriodDurationCommandHandler {
  private final PreferencesStore preferencesStore;

  public ChangePeriodDurationCommandHandler(PreferencesStore preferencesStore) {
    this.preferencesStore = preferencesStore;
  }

  public CommandStatus handle(ChangePeriodDurationCommand command) {
    try {
      preferencesStore.savePeriodDuration(command.periodDuration());
      return new Success();
    } catch (Exception e) {
      return new Failure("Storing settings failed: " + e.getMessage());
    }
  }
}
