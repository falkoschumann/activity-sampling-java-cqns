/*
 * Activity Sampling - Application
 * Copyright (c) 2020 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling;

import de.muspellheim.activitysampling.backend.EventStore;
import de.muspellheim.activitysampling.backend.MessageHandler;
import de.muspellheim.activitysampling.backend.SettingsRepository;
import de.muspellheim.activitysampling.backend.adapters.CsvEventStore;
import de.muspellheim.activitysampling.backend.adapters.MemoryEventStore;
import de.muspellheim.activitysampling.backend.adapters.MemorySettingsRepository;
import de.muspellheim.activitysampling.backend.adapters.PreferencesSettingsRepository;
import de.muspellheim.activitysampling.contract.MessageHandling;
import de.muspellheim.activitysampling.frontend.MainView;
import de.muspellheim.activitysampling.frontend.ViewModelFactory;
import java.io.InputStream;
import java.util.Properties;
import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {
  private MessageHandling backend;

  public static void main(String[] args) {
    Application.launch(args);
  }

  @Override
  public void init() throws Exception {
    EventStore eventStore;
    SettingsRepository settingsRepository;
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
    backend = new MessageHandler(eventStore, settingsRepository);
  }

  @Override
  public void start(Stage primaryStage) throws Exception {
    ViewModelFactory.initMessageHandling(backend);

    var url = getClass().getResource("/app.png");
    ViewModelFactory.initIconUrl(url);

    var properties = new Properties();
    try (InputStream in = getClass().getResourceAsStream("/app.properties")) {
      properties.load(in);
    }
    ViewModelFactory.initAppProperties(properties);

    var frontend = MainView.create(primaryStage);
    frontend.run();
  }
}
