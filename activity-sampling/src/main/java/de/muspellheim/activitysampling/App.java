/*
 * Activity Sampling - Application
 * Copyright (c) 2020 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling;

import de.muspellheim.activitysampling.backend.EventStore;
import de.muspellheim.activitysampling.backend.PreferencesStore;
import de.muspellheim.activitysampling.backend.adapters.CsvEventStore;
import de.muspellheim.activitysampling.backend.adapters.MemoryEventStore;
import de.muspellheim.activitysampling.backend.adapters.MemoryPreferencesStore;
import de.muspellheim.activitysampling.backend.adapters.PreferencesPreferencesStore;
import de.muspellheim.activitysampling.backend.messagehandlers.ActivityLogQueryHandler;
import de.muspellheim.activitysampling.backend.messagehandlers.ChangeActivityLogFileCommandHandler;
import de.muspellheim.activitysampling.backend.messagehandlers.ChangePeriodDurationCommandHandler;
import de.muspellheim.activitysampling.backend.messagehandlers.LogActivityCommandHandler;
import de.muspellheim.activitysampling.backend.messagehandlers.PreferencesQueryHandler;
import de.muspellheim.activitysampling.contract.messages.queries.ActivityLogQuery;
import de.muspellheim.activitysampling.contract.messages.queries.PreferencesQuery;
import de.muspellheim.activitysampling.frontend.MainViewController;
import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {
  private EventStore eventStore;
  private PreferencesStore preferencesStore;

  public static void main(String[] args) {
    Application.launch(args);
  }

  @Override
  public void init() {
    if (getParameters().getUnnamed().contains("--demo")) {
      System.out.println("Run in demo mode...");
      System.setProperty("demoMode", "true");
      eventStore = new MemoryEventStore();
      eventStore.addRecordedObserver(it -> System.out.println("Logged event: " + it));
      preferencesStore = new MemoryPreferencesStore();
    } else {
      preferencesStore = new PreferencesPreferencesStore();
      var activityLogFile = preferencesStore.loadActivityLogFile();
      System.out.println("Save activity log in: " + activityLogFile.toAbsolutePath());
      eventStore = new CsvEventStore(activityLogFile.toString());
    }
  }

  @Override
  public void start(Stage primaryStage) {
    //
    // Build
    //

    var logActivityCommandHandler = new LogActivityCommandHandler(eventStore);
    var changePeriodDurationCommandHandler =
        new ChangePeriodDurationCommandHandler(preferencesStore);
    var changeActivityLogFileCommandHandler =
        new ChangeActivityLogFileCommandHandler(preferencesStore, eventStore);
    var activityLogQueryHandler = new ActivityLogQueryHandler(eventStore);
    var preferencesQueryHandler = new PreferencesQueryHandler(preferencesStore);

    var frontend = MainViewController.create(primaryStage);

    //
    // Bind
    //

    frontend.setOnLogActivityCommand(
        cmd -> {
          logActivityCommandHandler.handle(cmd);
          var result = activityLogQueryHandler.handle(new ActivityLogQuery());
          frontend.display(result);
        });
    frontend.setOnPreferencesQuery(
        qry -> {
          var result = preferencesQueryHandler.handle(new PreferencesQuery());
          frontend.display(result);
        });
    frontend.setOnActivityLogQuery(
        qry -> {
          var result = activityLogQueryHandler.handle(qry);
          frontend.display(result);
        });

    frontend.setOnChangePeriodDurationCommand(
        cmd -> {
          changePeriodDurationCommandHandler.handle(cmd);
          var result = preferencesQueryHandler.handle(new PreferencesQuery());
          frontend.display(result);
          frontend.display(result);
        });
    frontend.setOnChangeActivityLogFileCommand(
        cmd -> {
          changeActivityLogFileCommandHandler.handle(cmd);
          var preferencesQueryResult = preferencesQueryHandler.handle(new PreferencesQuery());
          frontend.display(preferencesQueryResult);
          var activityLogQueryResult = activityLogQueryHandler.handle(new ActivityLogQuery());
          frontend.display(activityLogQueryResult);
        });
    frontend.setOnPreferencesQuery(
        qry -> {
          var result = preferencesQueryHandler.handle(new PreferencesQuery());
          frontend.display(result);
        });

    //
    // Run
    //

    frontend.run();
  }
}
