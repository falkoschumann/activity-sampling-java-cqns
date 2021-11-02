/*
 * Activity Sampling - Backend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.backend;

import de.muspellheim.activitysampling.backend.messagehandlers.ActivityLogQueryHandler;
import de.muspellheim.activitysampling.backend.messagehandlers.ChangeMainWindowBoundsCommandHandler;
import de.muspellheim.activitysampling.backend.messagehandlers.ChangePreferencesCommandHandler;
import de.muspellheim.activitysampling.backend.messagehandlers.ClockTickedNotificationHandler;
import de.muspellheim.activitysampling.backend.messagehandlers.LogActivityCommandHandler;
import de.muspellheim.activitysampling.backend.messagehandlers.MainWindowBoundsQueryHandler;
import de.muspellheim.activitysampling.backend.messagehandlers.PreferencesQueryHandler;
import de.muspellheim.activitysampling.backend.messagehandlers.ProgressPeriodCommandHandler;
import de.muspellheim.activitysampling.backend.messagehandlers.TimeReportQueryHandler;
import de.muspellheim.activitysampling.contract.MessageHandling;
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
import lombok.Getter;
import lombok.Setter;

public class MessageHandler implements MessageHandling {
  @Getter @Setter private Consumer<PeriodProgressedNotification> onPeriodProgressedNotification;
  @Getter @Setter private Consumer<PeriodEndedNotification> onPeriodEndedNotification;

  private final ChangeMainWindowBoundsCommandHandler changeMainWindowBoundsCommandHandler;
  private final ChangePreferencesCommandHandler changePreferencesCommandHandler;
  private final LogActivityCommandHandler logActivityCommandHandler;
  private final ProgressPeriodCommandHandler progressPeriodCommandHandler;
  private final ActivityLogQueryHandler activityLogQueryHandler;
  private final MainWindowBoundsQueryHandler mainWindowBoundsQueryHandler;
  private final PreferencesQueryHandler preferencesQueryHandler;
  private final TimeReportQueryHandler timeReportQueryHandler;
  private final ClockTickedNotificationHandler clockTickedNotificationHandler;

  public MessageHandler(EventStore eventStore, PreferencesRepository preferencesRepository) {
    // Build
    changeMainWindowBoundsCommandHandler =
        new ChangeMainWindowBoundsCommandHandler(preferencesRepository);
    changePreferencesCommandHandler = new ChangePreferencesCommandHandler(preferencesRepository);
    logActivityCommandHandler = new LogActivityCommandHandler(eventStore);
    progressPeriodCommandHandler = new ProgressPeriodCommandHandler(preferencesRepository);
    activityLogQueryHandler = new ActivityLogQueryHandler(eventStore);
    mainWindowBoundsQueryHandler = new MainWindowBoundsQueryHandler(preferencesRepository);
    preferencesQueryHandler = new PreferencesQueryHandler(preferencesRepository);
    timeReportQueryHandler = new TimeReportQueryHandler(eventStore);
    clockTickedNotificationHandler = new ClockTickedNotificationHandler();

    // Bind
    progressPeriodCommandHandler.setOnPeriodProgressedNotification(
        it -> onPeriodProgressedNotification.accept(it));
    progressPeriodCommandHandler.setOnPeriodEndedNotification(
        it -> onPeriodEndedNotification.accept(it));
    clockTickedNotificationHandler.setOnProgressPeriodCommand(progressPeriodCommandHandler::handle);
  }

  @Override
  public CommandStatus handle(ChangeMainWindowBoundsCommand command) {
    return changeMainWindowBoundsCommandHandler.handle(command);
  }

  @Override
  public CommandStatus handle(ChangePreferencesCommand command) {
    return changePreferencesCommandHandler.handle(command);
  }

  @Override
  public CommandStatus handle(LogActivityCommand command) {
    return logActivityCommandHandler.handle(command);
  }

  @Override
  public CommandStatus handle(ProgressPeriodCommand command) {
    return progressPeriodCommandHandler.handle(command);
  }

  @Override
  public ActivityLogQueryResult handle(ActivityLogQuery query) {
    return activityLogQueryHandler.handle(query);
  }

  @Override
  public MainWindowBoundsQueryResult handle(MainWindowBoundsQuery query) {
    return mainWindowBoundsQueryHandler.handle(query);
  }

  @Override
  public PreferencesQueryResult handle(PreferencesQuery query) {
    return preferencesQueryHandler.handle(query);
  }

  @Override
  public TimeReportQueryResult handle(TimeReportQuery query) {
    return timeReportQueryHandler.handle(query);
  }

  @Override
  public void handle(ClockTickedNotification notification) {
    clockTickedNotificationHandler.handle(notification);
  }
}
