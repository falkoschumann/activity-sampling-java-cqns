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
import de.muspellheim.activitysampling.frontend.MainView;
import de.muspellheim.activitysampling.frontend.ViewModelFactory;
import java.io.InputStream;
import java.util.Properties;
import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {
  private EventStore eventStore;
  private PreferencesRepository preferencesRepository;

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
  }

  @Override
  public void start(Stage primaryStage) throws Exception {
    var backend = new MessageHandler(eventStore, preferencesRepository);
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
