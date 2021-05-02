/*
 * Activity Sampling - Backend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.backend.messagehandlers;

import de.muspellheim.activitysampling.backend.SettingsRepository;
import de.muspellheim.activitysampling.contract.messages.commands.ChangePeriodDurationCommand;
import de.muspellheim.activitysampling.contract.messages.commands.CommandStatus;
import de.muspellheim.activitysampling.contract.messages.commands.Failure;
import de.muspellheim.activitysampling.contract.messages.commands.Success;
import java.util.logging.Level;
import lombok.extern.java.Log;

@Log
public class ChangePeriodDurationCommandHandler {
  private final SettingsRepository settingsRepository;

  public ChangePeriodDurationCommandHandler(SettingsRepository settingsRepository) {
    this.settingsRepository = settingsRepository;
  }

  public CommandStatus handle(ChangePeriodDurationCommand command) {
    try {
      settingsRepository.savePeriodDuration(command.periodDuration());
      return new Success();
    } catch (Exception e) {
      log.log(Level.WARNING, "Can not handle command: " + command, e);
      return new Failure(e.getLocalizedMessage());
    }
  }
}
