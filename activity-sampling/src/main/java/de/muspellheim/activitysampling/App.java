/*
 * Activity Sampling - Application
 * Copyright (c) 2020 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling;

import de.muspellheim.activitysampling.backend.EventStore;
import de.muspellheim.activitysampling.backend.MessageHandler;
import de.muspellheim.activitysampling.backend.PreferencesRepository;
import de.muspellheim.activitysampling.backend.adapters.CsvEventStore;
import de.muspellheim.activitysampling.backend.adapters.MemoryEventStore;
import de.muspellheim.activitysampling.backend.adapters.MemoryPreferencesRepository;
import de.muspellheim.activitysampling.backend.adapters.PreferencesPreferencesRepository;
import de.muspellheim.activitysampling.contract.messages.queries.ActivityLogQuery;
import de.muspellheim.activitysampling.contract.messages.queries.PreferencesQuery;
import de.muspellheim.activitysampling.frontend.ActivitySamplingViewController;
import de.muspellheim.activitysampling.frontend.InfoView;
import de.muspellheim.activitysampling.frontend.PreferencesView;
import de.muspellheim.activitysampling.frontend.ViewModelFactory;
import java.io.InputStream;
import java.util.Properties;
import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {
  private EventStore eventStore;
  private PreferencesRepository preferencesRepository;
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
      preferencesRepository = new MemoryPreferencesRepository();
    } else {
      preferencesRepository = new PreferencesPreferencesRepository();
      var activityLogFile = preferencesRepository.loadActivityLogFile();
      System.out.println("Save activity log in: " + activityLogFile.toAbsolutePath());
      eventStore = new CsvEventStore(activityLogFile.toString());
    }

    if (getParameters().getUnnamed().contains("--noSystemMenuBar")) {
      useSystemMenuBar = false;
    }
  }

  @Override
  public void start(Stage primaryStage) throws Exception {
    //
    // Build
    //

    var backend = new MessageHandler(eventStore, preferencesRepository);
    ViewModelFactory.initMessageHandling(backend);

    var url = getClass().getResource("/app.png");
    var properties = new Properties();
    try (InputStream in = getClass().getResourceAsStream("/app.properties")) {
      properties.load(in);
    }
    ViewModelFactory.initAppProperties(properties);
    ViewModelFactory.initIconUrl(url);

    var activitySamplingViewController =
        ActivitySamplingViewController.create(primaryStage, useSystemMenuBar);
    var preferencesView = PreferencesView.create(primaryStage);
    var infoView = InfoView.create(primaryStage);

    //
    // Bind
    //

    activitySamplingViewController.setOnOpenPreferences(() -> preferencesView.run());
    activitySamplingViewController.setOnOpenAbout(() -> infoView.run());
    activitySamplingViewController.setOnLogActivityCommand(
        cmd -> {
          backend.handle(cmd);
          var result = backend.handle(new ActivityLogQuery());
          activitySamplingViewController.display(result);
        });
    activitySamplingViewController.setOnPreferencesQuery(
        qry -> {
          var result = backend.handle(new PreferencesQuery());
          activitySamplingViewController.display(result);
        });
    activitySamplingViewController.setOnActivityLogQuery(
        qry -> {
          var result = backend.handle(qry);
          activitySamplingViewController.display(result);
        });

    //
    // Run
    //

    activitySamplingViewController.run();
  }
}
