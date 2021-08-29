/*
 * Activity Sampling - Application
 * Copyright (c) 2020 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling;

import de.muspellheim.activitysampling.backend.EventStore;
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
    if (getParameters().getUnnamed().contains("--demo")) {
      System.setProperty("demoMode", "true");
      preferencesRepository = new MemoryPreferencesRepository().addExamples();
      eventStore = new MemoryEventStore().addExamples();
    } else {
      preferencesRepository = new PrefsPreferencesRepository();
      eventStore = new CsvEventStore();
    }
  }

  @Override
  public void start(Stage primaryStage) {
    var systemClock = new SystemClock();
    var requestHandler = new RequestHandler(eventStore, preferencesRepository);
    var frontend = MainWindowController.create(primaryStage);

    systemClock.setOnClockTickedNotification(requestHandler::handle);
    requestHandler.setOnPeriodProgressedNotification(frontend::display);
    frontend.setOnChangeMainWindowBoundsCommand(requestHandler::handle);
    frontend.setOnChangePreferencesCommand(c -> frontend.display(requestHandler.handle(c)));
    frontend.setOnLogActivityCommand(c -> frontend.display(requestHandler.handle(c)));
    frontend.setOnActivityLogQuery(q -> frontend.display(requestHandler.handle(q)));
    frontend.setOnMainWindowBoundsQuery(q -> frontend.display(requestHandler.handle(q)));
    frontend.setOnPreferencesQuery(q -> frontend.display(requestHandler.handle(q)));
    frontend.setOnTimeReportQuery(q -> frontend.display(requestHandler.handle(q)));

    frontend.run();
    systemClock.run();
  }
}
