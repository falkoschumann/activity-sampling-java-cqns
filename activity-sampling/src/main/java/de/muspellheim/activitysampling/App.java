/*
 * Activity Sampling - Application
 * Copyright (c) 2020 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling;

import de.muspellheim.activitysampling.backend.EventStore;
import de.muspellheim.activitysampling.backend.SettingsRepository;
import de.muspellheim.activitysampling.backend.adapters.CsvEventStore;
import de.muspellheim.activitysampling.backend.adapters.MemoryEventStore;
import de.muspellheim.activitysampling.backend.adapters.MemorySettingsRepository;
import de.muspellheim.activitysampling.backend.adapters.PreferencesSettingsRepository;
import de.muspellheim.activitysampling.backend.messagehandlers.ActivityLogQueryHandler;
import de.muspellheim.activitysampling.backend.messagehandlers.ChangeActivityLogFileCommandHandler;
import de.muspellheim.activitysampling.backend.messagehandlers.ChangePeriodDurationCommandHandler;
import de.muspellheim.activitysampling.backend.messagehandlers.LogActivityCommandHandler;
import de.muspellheim.activitysampling.backend.messagehandlers.SettingsQueryHandler;
import de.muspellheim.activitysampling.contract.messages.queries.ActivityLogQuery;
import de.muspellheim.activitysampling.contract.messages.queries.SettingsQuery;
import de.muspellheim.activitysampling.frontend.MainView;
import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {
  private EventStore eventStore;
  private SettingsRepository settingsRepository;

  public static void main(String[] args) {
    Application.launch(args);
  }

  @Override
  public void init() throws Exception {
    if (getParameters().getUnnamed().contains("--demo")) {
      System.out.println("Run in demo mode...");
      eventStore = new MemoryEventStore();
      eventStore.setOnRecorded(it -> System.out.println("Logged event: " + it));
      settingsRepository = new MemorySettingsRepository();
    } else {
      settingsRepository = new PreferencesSettingsRepository();
      var activityLogFile = settingsRepository.loadActivityLogFile();
      System.out.println("Save activity log in: " + activityLogFile.toAbsolutePath());
      eventStore = new CsvEventStore(activityLogFile.toString());
    }
  }

  @Override
  public void start(Stage primaryStage) {
    var logActivityCommandHandler = new LogActivityCommandHandler(eventStore);
    var changePeriodDurationCommandHandler =
        new ChangePeriodDurationCommandHandler(settingsRepository);
    var changeActivityLogFileCommandHandler =
        new ChangeActivityLogFileCommandHandler(settingsRepository, eventStore);
    var activityLogQueryHandler = new ActivityLogQueryHandler(eventStore);
    var settingsQueryHandler = new SettingsQueryHandler(settingsRepository);

    var frontend = MainView.create(primaryStage);

    // TODO Extrahiere Request-Handler?
    frontend
        .setOnLogActivityCommand(
            cmd -> {
              logActivityCommandHandler.handle(cmd);
              var result = activityLogQueryHandler.handle(new ActivityLogQuery());
              frontend.display(result);
            });

    frontend
        .setOnChangePeriodDurationCommand(
            cmd -> {
              changePeriodDurationCommandHandler.handle(cmd);
              var result = settingsQueryHandler.handle(new SettingsQuery());
              frontend.display(result);
            });

    frontend

        .setOnChangeActivityLogFileCommand(
            cmd -> {
              changeActivityLogFileCommandHandler.handle(cmd);
              var result = settingsQueryHandler.handle(new SettingsQuery());
              frontend.display(result);
            });

    frontend

        .setOnActivityLogQuery(
            cmd -> {
              var result = activityLogQueryHandler.handle(new ActivityLogQuery());
              frontend.display(result);
            });

    frontend

        .setOnSettingsQuery(
            cmd -> {
              var result = settingsQueryHandler.handle(new SettingsQuery());
              frontend.display(result);
            });

    frontend.run();
  }
}
