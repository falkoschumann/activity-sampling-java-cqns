/*
 * Activity Sampling - Contract
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.contract;

import de.muspellheim.activitysampling.contract.messages.commands.ChangeMainWindowBoundsCommand;
import de.muspellheim.activitysampling.contract.messages.commands.ChangePreferencesCommand;
import de.muspellheim.activitysampling.contract.messages.commands.CommandStatus;
import de.muspellheim.activitysampling.contract.messages.commands.LogActivityCommand;
import de.muspellheim.activitysampling.contract.messages.commands.ProgressPeriodCommand;
import de.muspellheim.activitysampling.contract.messages.notification.ClockTickedNotification;
import de.muspellheim.activitysampling.contract.messages.notification.PeriodEndedNotification;
import de.muspellheim.activitysampling.contract.messages.notification.PeriodProgressedNotification;
import de.muspellheim.activitysampling.contract.messages.queries.ActivityLogQuery;
import de.muspellheim.activitysampling.contract.messages.queries.ActivityLogQueryResult;
import de.muspellheim.activitysampling.contract.messages.queries.MainWindowBoundsQuery;
import de.muspellheim.activitysampling.contract.messages.queries.MainWindowBoundsQueryResult;
import de.muspellheim.activitysampling.contract.messages.queries.PreferencesQuery;
import de.muspellheim.activitysampling.contract.messages.queries.PreferencesQueryResult;
import de.muspellheim.activitysampling.contract.messages.queries.TimeReportQuery;
import de.muspellheim.activitysampling.contract.messages.queries.TimeReportQueryResult;
import java.util.function.Consumer;

public interface MessageHandling {
  Consumer<PeriodProgressedNotification> getOnPeriodProgressedNotification();

  void setOnPeriodProgressedNotification(Consumer<PeriodProgressedNotification> handler);

  Consumer<PeriodEndedNotification> getOnPeriodEndedNotification();

  void setOnPeriodEndedNotification(Consumer<PeriodEndedNotification> handler);

  CommandStatus handle(ChangeMainWindowBoundsCommand command);

  CommandStatus handle(ChangePreferencesCommand command);

  CommandStatus handle(LogActivityCommand command);

  CommandStatus handle(ProgressPeriodCommand command);

  ActivityLogQueryResult handle(ActivityLogQuery query);

  MainWindowBoundsQueryResult handle(MainWindowBoundsQuery query);

  PreferencesQueryResult handle(PreferencesQuery query);

  TimeReportQueryResult handle(TimeReportQuery query);

  void handle(ClockTickedNotification notification);
}
