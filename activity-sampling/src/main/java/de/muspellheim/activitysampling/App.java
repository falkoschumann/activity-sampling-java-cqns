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
import de.muspellheim.activitysampling.backend.adapters.PrefsPreferencesRepository;
import de.muspellheim.activitysampling.backend.adapters.SystemClock;
import de.muspellheim.activitysampling.frontend.MainWindowController;
import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {
  private PreferencesRepository preferencesRepository;
  private EventStore eventStore;

  public static void main(String[] args) {
    Application.launch(args);
  }

  @Override
  public void init() {
    if (startAsDemo()) {
      initDemo();
    } else {
      initDefault();
    }
  }

  private boolean startAsDemo() {
    return getParameters().getUnnamed().contains("--demo");
  }

  private void initDemo() {
    System.setProperty("activitysampling.demo", "true");
    eventStore = new MemoryEventStore().addExamples();
    preferencesRepository = new MemoryPreferencesRepository().addExamples();
  }

  private void initDefault() {
    eventStore = new CsvEventStore();
    preferencesRepository = new PrefsPreferencesRepository();
  }

  @Override
  public void start(Stage primaryStage) {
    var systemClock = new SystemClock();
    var backend = new MessageHandler(eventStore, preferencesRepository);
    var frontend = MainWindowController.create(primaryStage, backend);

    systemClock.setOnClockTickedNotification(backend::handle);

    frontend.run();
    systemClock.run();
  }
}
