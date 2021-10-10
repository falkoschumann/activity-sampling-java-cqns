/*
 * Activity Sampling - Application
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling;

import de.muspellheim.activitysampling.backend.EventStore;
import de.muspellheim.activitysampling.backend.PreferencesRepository;
import de.muspellheim.activitysampling.backend.messagehandlers.ActivityLogQueryHandler;
import de.muspellheim.activitysampling.backend.messagehandlers.ChangeMainWindowBoundsCommandHandler;
import de.muspellheim.activitysampling.backend.messagehandlers.ChangePreferencesCommandHandler;
import de.muspellheim.activitysampling.backend.messagehandlers.ClockTickedNotificationHandler;
import de.muspellheim.activitysampling.backend.messagehandlers.LogActivityCommandHandler;
import de.muspellheim.activitysampling.backend.messagehandlers.MainWindowBoundsQueryHandler;
import de.muspellheim.activitysampling.backend.messagehandlers.PreferencesQueryHandler;
import de.muspellheim.activitysampling.backend.messagehandlers.ProgressPeriodCommandHandler;
import de.muspellheim.activitysampling.backend.messagehandlers.TimeReportQueryHandler;
import de.muspellheim.activitysampling.contract.messages.commands.ChangeMainWindowBoundsCommand;
import de.muspellheim.activitysampling.contract.messages.commands.ChangePreferencesCommand;
import de.muspellheim.activitysampling.contract.messages.commands.LogActivityCommand;
import de.muspellheim.activitysampling.contract.messages.commands.ProgressPeriodCommand;
import de.muspellheim.activitysampling.contract.messages.notification.ClockTickedNotification;
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

class RequestHandler {
  @Getter @Setter private Consumer<PeriodProgressedNotification> onPeriodProgressedNotification;

  private final ChangeMainWindowBoundsCommandHandler changeMainWindowBoundsCommandHandler;
  private final LogActivityCommandHandler logActivityCommandHandler;
  private final ChangePreferencesCommandHandler changePreferencesCommandHandler;
  private final ProgressPeriodCommandHandler progressPeriodCommandHandler;

  private final ActivityLogQueryHandler activityLogQueryHandler;
  private final PreferencesQueryHandler preferencesQueryHandler;
  private final MainWindowBoundsQueryHandler mainWindowBoundsQueryHandler;
  private final TimeReportQueryHandler timesheetQueryHandler;

  private final ClockTickedNotificationHandler clockTickedNotificationHandler;

  RequestHandler(EventStore eventStore, PreferencesRepository preferencesRepository) {
    changeMainWindowBoundsCommandHandler =
        new ChangeMainWindowBoundsCommandHandler(preferencesRepository);
    logActivityCommandHandler = new LogActivityCommandHandler(eventStore);
    changePreferencesCommandHandler = new ChangePreferencesCommandHandler(preferencesRepository);
    progressPeriodCommandHandler =
        new ProgressPeriodCommandHandler(preferencesRepository.loadPeriodDuration());
    progressPeriodCommandHandler.setOnPeriodProgressedNotification(
        n -> onPeriodProgressedNotification.accept(n));

    activityLogQueryHandler = new ActivityLogQueryHandler(eventStore);
    preferencesQueryHandler = new PreferencesQueryHandler(preferencesRepository);
    mainWindowBoundsQueryHandler = new MainWindowBoundsQueryHandler(preferencesRepository);
    timesheetQueryHandler = new TimeReportQueryHandler(eventStore);

    clockTickedNotificationHandler = new ClockTickedNotificationHandler();
    clockTickedNotificationHandler.setOnProgressPeriodCommand(this::handle);
  }

  void handle(ChangeMainWindowBoundsCommand command) {
    changeMainWindowBoundsCommandHandler.handle(command);
  }

  PreferencesQueryResult handle(ChangePreferencesCommand command) {
    progressPeriodCommandHandler.setDuration(command.periodDuration());
    changePreferencesCommandHandler.handle(command);
    return preferencesQueryHandler.handle(new PreferencesQuery());
  }

  ActivityLogQueryResult handle(LogActivityCommand command) {
    logActivityCommandHandler.handle(command);
    return handle(new ActivityLogQuery());
  }

  private void handle(ProgressPeriodCommand command) {
    progressPeriodCommandHandler.handle(command);
  }

  ActivityLogQueryResult handle(ActivityLogQuery query) {
    return activityLogQueryHandler.handle(query);
  }

  MainWindowBoundsQueryResult handle(MainWindowBoundsQuery query) {
    return mainWindowBoundsQueryHandler.handle(query);
  }

  PreferencesQueryResult handle(PreferencesQuery query) {
    return preferencesQueryHandler.handle(query);
  }

  TimeReportQueryResult handle(TimeReportQuery query) {
    return timesheetQueryHandler.handle(query);
  }

  void handle(ClockTickedNotification notification) {
    clockTickedNotificationHandler.handle(notification);
  }
}
