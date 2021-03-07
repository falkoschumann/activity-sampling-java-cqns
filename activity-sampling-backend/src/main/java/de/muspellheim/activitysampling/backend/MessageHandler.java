/*
 * Activity Sampling - Backend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.backend;

import de.muspellheim.activitysampling.backend.messagehandlers.ActivityLogQueryHandler;
import de.muspellheim.activitysampling.backend.messagehandlers.ChangeActivityLogFileCommandHandler;
import de.muspellheim.activitysampling.backend.messagehandlers.ChangePeriodDurationCommandHandler;
import de.muspellheim.activitysampling.backend.messagehandlers.LogActivityCommandHandler;
import de.muspellheim.activitysampling.backend.messagehandlers.PreferencesQueryHandler;
import de.muspellheim.activitysampling.contract.MessageHandling;
import de.muspellheim.activitysampling.contract.messages.commands.ChangeActivityLogFileCommand;
import de.muspellheim.activitysampling.contract.messages.commands.ChangePeriodDurationCommand;
import de.muspellheim.activitysampling.contract.messages.commands.CommandStatus;
import de.muspellheim.activitysampling.contract.messages.commands.LogActivityCommand;
import de.muspellheim.activitysampling.contract.messages.queries.ActivityLogQuery;
import de.muspellheim.activitysampling.contract.messages.queries.ActivityLogQueryResult;
import de.muspellheim.activitysampling.contract.messages.queries.PreferencesQuery;
import de.muspellheim.activitysampling.contract.messages.queries.PreferencesQueryResult;

public class MessageHandler implements MessageHandling {
  private final LogActivityCommandHandler logActivityCommandHandler;
  private final ChangePeriodDurationCommandHandler changePeriodDurationCommandHandler;
  private final ChangeActivityLogFileCommandHandler changeActivityLogFileCommandHandler;
  private final ActivityLogQueryHandler activityLogQueryHandler;
  private final PreferencesQueryHandler preferencesQueryHandler;

  public MessageHandler(EventStore eventStore, PreferencesRepository preferencesStore) {
    logActivityCommandHandler = new LogActivityCommandHandler(eventStore);
    changePeriodDurationCommandHandler = new ChangePeriodDurationCommandHandler(preferencesStore);
    changeActivityLogFileCommandHandler =
        new ChangeActivityLogFileCommandHandler(preferencesStore, eventStore);
    activityLogQueryHandler = new ActivityLogQueryHandler(eventStore);
    preferencesQueryHandler = new PreferencesQueryHandler(preferencesStore);
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
  public PreferencesQueryResult handle(PreferencesQuery query) {
    return preferencesQueryHandler.handle(query);
  }
}
