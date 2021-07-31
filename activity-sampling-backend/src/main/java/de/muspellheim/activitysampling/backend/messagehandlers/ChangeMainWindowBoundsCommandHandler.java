/*
 * Activity Sampling - Backend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.backend.messagehandlers;

import de.muspellheim.activitysampling.backend.PreferencesRepository;
import de.muspellheim.activitysampling.contract.messages.commands.ChangeMainWindowBoundsCommand;
import de.muspellheim.activitysampling.contract.messages.commands.CommandStatus;
import de.muspellheim.activitysampling.contract.messages.commands.Failure;
import de.muspellheim.activitysampling.contract.messages.commands.Success;

public class ChangeMainWindowBoundsCommandHandler {
  private final PreferencesRepository repository;

  public ChangeMainWindowBoundsCommandHandler(PreferencesRepository repository) {
    this.repository = repository;
  }

  public CommandStatus handle(ChangeMainWindowBoundsCommand command) {
    try {
      repository.storeMainWindowBounds(command.bounds());
      return new Success();
    } catch (Exception e) {
      return new Failure("Storing settings failed: " + e.getMessage());
    }
  }
}
