/*
 * Activity Sampling - Backend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.backend.messagehandlers;

import de.muspellheim.activitysampling.backend.PreferencesRepository;
import de.muspellheim.activitysampling.contract.messages.commands.ChangePreferencesCommand;
import de.muspellheim.activitysampling.contract.messages.commands.CommandStatus;
import de.muspellheim.activitysampling.contract.messages.commands.Failure;
import de.muspellheim.activitysampling.contract.messages.commands.Success;

public class ChangePreferencesCommandHandler {
  private final PreferencesRepository repository;

  public ChangePreferencesCommandHandler(PreferencesRepository repository) {
    this.repository = repository;
  }

  public CommandStatus handle(ChangePreferencesCommand command) {
    try {
      repository.savePeriodDuration(command.periodDuration());
      return new Success();
    } catch (Exception e) {
      return new Failure("Storing preferences failed: " + e.getMessage());
    }
  }
}
