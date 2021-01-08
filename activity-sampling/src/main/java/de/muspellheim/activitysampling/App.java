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
import de.muspellheim.activitysampling.frontend.ActivitySamplingViewController;
import de.muspellheim.activitysampling.frontend.PreferencesViewController;
import java.nio.file.Paths;
import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {
  private EventStore eventStore;
  private PreferencesStore preferencesStore;
  private boolean useSystemMenuBar = true;

  public static void main(String[] args) {
    Application.launch(args);
  }

  @Override
  public void init() {
    if (getParameters().getUnnamed().contains("--demo")) {
      System.out.println("Run in demo mode...");
      eventStore = new MemoryEventStore();
      eventStore.setOnRecorded(it -> System.out.println("Logged event: " + it));

      preferencesStore = new MemoryPreferencesStore();
    } else {
      var userHome = System.getProperty("user.home");
      var file = Paths.get(userHome, "activity-log.csv");
      System.out.println("Save activity log in: " + file.toAbsolutePath());
      eventStore = new CsvEventStore(file);

      preferencesStore = new PreferencesPreferencesStore();
    }

    if (getParameters().getUnnamed().contains("--noSystemMenuBar")) {
      useSystemMenuBar = false;
    }
  }

  @Override
  public void start(Stage primaryStage) {
    // Build
    var logActivityCommandHandler = new LogActivityCommandHandler(eventStore);
    var changePeriodDurationCommandHandler =
        new ChangePeriodDurationCommandHandler(preferencesStore);
    var changeActivityLogFileCommandHandler =
        new ChangeActivityLogFileCommandHandler(preferencesStore);
    var activityLogQueryHandler = new ActivityLogQueryHandler(eventStore);
    var preferencesQueryHandler = new PreferencesQueryHandler(preferencesStore);
    var activitySamplingViewController =
        ActivitySamplingViewController.create(primaryStage, useSystemMenuBar);
    var preferencesStage = new Stage();
    preferencesStage.initOwner(primaryStage);
    var preferencesViewController = PreferencesViewController.create(preferencesStage);

    // Bind
    activitySamplingViewController.setOnOpenPreferences(
        () -> {
          preferencesStage.show();
          preferencesViewController.run();
        });
    activitySamplingViewController.setOnLogActivityCommand(
        cmd -> {
          logActivityCommandHandler.handle(cmd);
          var result = activityLogQueryHandler.handle(new ActivityLogQuery());
          activitySamplingViewController.display(result);
        });
    activitySamplingViewController.setOnPreferencesQuery(
        qry -> {
          var result = preferencesQueryHandler.handle(new PreferencesQuery());
          activitySamplingViewController.display(result);
        });
    activitySamplingViewController.setOnActivityLogQuery(
        qry -> {
          var result = activityLogQueryHandler.handle(qry);
          activitySamplingViewController.display(result);
        });
    preferencesViewController.setOnChangePeriodDurationCommand(
        cmd -> {
          changePeriodDurationCommandHandler.handle(cmd);
          var result = preferencesQueryHandler.handle(new PreferencesQuery());
          activitySamplingViewController.display(result);
          preferencesViewController.display(result);
        });
    preferencesViewController.setOnChangeActivityLogFileCommand(
        cmd -> {
          changeActivityLogFileCommandHandler.handle(cmd);
          var result = preferencesQueryHandler.handle(new PreferencesQuery());
          preferencesViewController.display(result);
          // TODO Handle change activity log file
        });
    preferencesViewController.setOnPreferencesQuery(
        qry -> {
          var result = preferencesQueryHandler.handle(new PreferencesQuery());
          preferencesViewController.display(result);
        });

    // Run
    activitySamplingViewController.run();
  }
}
