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
import de.muspellheim.activitysampling.frontend.AboutViewController;
import de.muspellheim.activitysampling.frontend.ActivitySamplingViewController;
import de.muspellheim.activitysampling.frontend.PreferencesViewController;
import java.io.InputStream;
import java.util.Properties;
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
  public void init() throws Exception {
    if (getParameters().getUnnamed().contains("--demo")) {
      System.out.println("Run in demo mode...");
      eventStore = new MemoryEventStore();
      eventStore.addRecordedObserver(it -> System.out.println("Logged event: " + it));
      preferencesStore = new MemoryPreferencesStore();
    } else {
      preferencesStore = new PreferencesPreferencesStore();
      var activityLogFile = preferencesStore.loadActivityLogFile();
      System.out.println("Save activity log in: " + activityLogFile.toAbsolutePath());
      eventStore = new CsvEventStore(activityLogFile.toString());
    }

    if (getParameters().getUnnamed().contains("--noSystemMenuBar")) {
      useSystemMenuBar = false;
    }

    var properties = new Properties();
    try (InputStream in = getClass().getResourceAsStream("/app.properties")) {
      properties.load(in);
    }
    System.getProperties().putAll(properties);
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

    var activitySamplingViewController =
        ActivitySamplingViewController.create(primaryStage, useSystemMenuBar);

    var preferencesStage = new Stage();
    preferencesStage.initOwner(primaryStage);
    var preferencesViewController = PreferencesViewController.create(preferencesStage);

    var aboutStage = new Stage();
    aboutStage.initOwner(primaryStage);
    var aboutViewController = AboutViewController.create(aboutStage);
    aboutViewController.initVersion(System.getProperty("app.version"));
    aboutViewController.initCopyright(System.getProperty("app.copyright"));

    //
    // Bind
    //

    activitySamplingViewController.setOnOpenPreferences(
        () -> {
          preferencesStage.show();
          preferencesViewController.run();
        });
    activitySamplingViewController.setOnOpenAbout(aboutStage::show);
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
          var preferencesQueryResult = preferencesQueryHandler.handle(new PreferencesQuery());
          preferencesViewController.display(preferencesQueryResult);
          var activityLogQueryResult = activityLogQueryHandler.handle(new ActivityLogQuery());
          activitySamplingViewController.display(activityLogQueryResult);
        });
    preferencesViewController.setOnPreferencesQuery(
        qry -> {
          var result = preferencesQueryHandler.handle(new PreferencesQuery());
          preferencesViewController.display(result);
        });

    //
    // Run
    //

    activitySamplingViewController.run();
  }
}
