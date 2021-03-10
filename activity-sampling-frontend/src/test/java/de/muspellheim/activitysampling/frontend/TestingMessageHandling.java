/*
 * Activity Sampling - Frontend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.frontend;

import de.muspellheim.activitysampling.contract.MessageHandling;
import de.muspellheim.activitysampling.contract.messages.commands.ChangeActivityLogFileCommand;
import de.muspellheim.activitysampling.contract.messages.commands.ChangePeriodDurationCommand;
import de.muspellheim.activitysampling.contract.messages.commands.CommandStatus;
import de.muspellheim.activitysampling.contract.messages.commands.LogActivityCommand;
import de.muspellheim.activitysampling.contract.messages.commands.Success;
import de.muspellheim.activitysampling.contract.messages.queries.ActivityLogQuery;
import de.muspellheim.activitysampling.contract.messages.queries.ActivityLogQueryResult;
import de.muspellheim.activitysampling.contract.messages.queries.PreferencesQuery;
import de.muspellheim.activitysampling.contract.messages.queries.PreferencesQueryResult;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;

public class TestingMessageHandling implements MessageHandling {
  @Override
  public CommandStatus handle(ChangeActivityLogFileCommand command) {
    return new Success();
  }

  @Override
  public CommandStatus handle(ChangePeriodDurationCommand command) {
    return new Success();
  }

  @Override
  public CommandStatus handle(LogActivityCommand command) {
    return new Success();
  }

  @Override
  public ActivityLogQueryResult handle(ActivityLogQuery query) {
    return new ActivityLogQueryResult(List.of(), List.of());
  }

  @Override
  public PreferencesQueryResult handle(PreferencesQuery query) {
    return new PreferencesQueryResult(Duration.ofMinutes(20), Paths.get("~/activity-log.csv"));
  }
}
