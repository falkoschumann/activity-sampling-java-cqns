/*
 * Activity Sampling - Backend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.backend;

import de.muspellheim.activitysampling.backend.messagehandlers.ActivityLogQueryHandler;
import de.muspellheim.activitysampling.backend.messagehandlers.ChangeActivityLogFileCommandHandler;
import de.muspellheim.activitysampling.backend.messagehandlers.ChangePeriodDurationCommandHandler;
import de.muspellheim.activitysampling.backend.messagehandlers.LogActivityCommandHandler;
import de.muspellheim.activitysampling.backend.messagehandlers.SettingsQueryHandler;
import de.muspellheim.activitysampling.contract.MessageHandling;
import de.muspellheim.activitysampling.contract.messages.commands.ChangeActivityLogFileCommand;
import de.muspellheim.activitysampling.contract.messages.commands.ChangePeriodDurationCommand;
import de.muspellheim.activitysampling.contract.messages.commands.CommandStatus;
import de.muspellheim.activitysampling.contract.messages.commands.LogActivityCommand;
import de.muspellheim.activitysampling.contract.messages.queries.ActivityLogQuery;
import de.muspellheim.activitysampling.contract.messages.queries.ActivityLogQueryResult;
import de.muspellheim.activitysampling.contract.messages.queries.SettingsQuery;
import de.muspellheim.activitysampling.contract.messages.queries.SettingsQueryResult;

public class MessageHandler implements MessageHandling {
  private final LogActivityCommandHandler logActivityCommandHandler;
  private final ChangePeriodDurationCommandHandler changePeriodDurationCommandHandler;
  private final ChangeActivityLogFileCommandHandler changeActivityLogFileCommandHandler;
  private final ActivityLogQueryHandler activityLogQueryHandler;
  private final SettingsQueryHandler settingsQueryHandler;

  public MessageHandler(EventStore eventStore, SettingsRepository settingsRepository) {
    logActivityCommandHandler = new LogActivityCommandHandler(eventStore);
    changePeriodDurationCommandHandler = new ChangePeriodDurationCommandHandler(settingsRepository);
    changeActivityLogFileCommandHandler =
        new ChangeActivityLogFileCommandHandler(settingsRepository, eventStore);
    activityLogQueryHandler = new ActivityLogQueryHandler(eventStore);
    settingsQueryHandler = new SettingsQueryHandler(settingsRepository);
  }

  @Override
  public CommandStatus handle(ChangeActivityLogFileCommand command) {
    return changeActivityLogFileCommandHandler.handle(command);
  }

  @Override
  public CommandStatus handle(ChangePeriodDurationCommand command) {
    return changePeriodDurationCommandHandler.handle(command);
  }

  @Override
  public CommandStatus handle(LogActivityCommand command) {
    return logActivityCommandHandler.handle(command);
  }

  @Override
  public ActivityLogQueryResult handle(ActivityLogQuery query) {
    return activityLogQueryHandler.handle(query);
  }

  @Override
  public SettingsQueryResult handle(SettingsQuery query) {
    return settingsQueryHandler.handle(query);
  }
}
