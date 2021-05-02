/*
 * Activity Sampling - Backend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.backend.messagehandlers;

import de.muspellheim.activitysampling.backend.EventStore;
import de.muspellheim.activitysampling.backend.SettingsRepository;
import de.muspellheim.activitysampling.contract.messages.commands.ChangeActivityLogFileCommand;
import de.muspellheim.activitysampling.contract.messages.commands.CommandStatus;
import de.muspellheim.activitysampling.contract.messages.commands.Failure;
import de.muspellheim.activitysampling.contract.messages.commands.Success;
import java.util.logging.Level;
import lombok.extern.java.Log;

@Log
public class ChangeActivityLogFileCommandHandler {
  private final SettingsRepository settingsRepository;
  private final EventStore eventStore;

  public ChangeActivityLogFileCommandHandler(
      SettingsRepository settingsRepository, EventStore eventStore) {
    this.settingsRepository = settingsRepository;
    this.eventStore = eventStore;
  }

  public CommandStatus handle(ChangeActivityLogFileCommand command) {
    try {
      eventStore.setUri(command.activityLogFile().toString());
      settingsRepository.saveActivityLogFile(command.activityLogFile());
      return new Success();
    } catch (Exception e) {
      log.log(Level.WARNING, "Can not handle command: " + command, e);
      return new Failure(e.getLocalizedMessage());
    }
  }
}
