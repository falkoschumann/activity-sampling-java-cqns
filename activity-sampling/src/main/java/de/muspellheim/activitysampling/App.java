/*
 * Activity Sampling - Application
 * Copyright (c) 2020 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling;

import de.muspellheim.activitysampling.backend.EventStore;
import de.muspellheim.activitysampling.backend.adapters.CsvEventStore;
import de.muspellheim.activitysampling.backend.adapters.MemoryEventStore;
import de.muspellheim.activitysampling.backend.messagehandlers.ActivityLogQueryHandler;
import de.muspellheim.activitysampling.backend.messagehandlers.LogActivityCommandHandler;
import de.muspellheim.activitysampling.contract.messages.queries.ActivityLogQuery;
import de.muspellheim.activitysampling.frontend.ActivitySamplingView;
import java.nio.file.Paths;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
  private EventStore eventStore;

  public static void main(String[] args) {
    Application.launch(args);
  }

  @Override
  public void init() {
    if (getParameters().getUnnamed().contains("--demo")) {
      System.out.println("Run in demo mode...");
      eventStore = new MemoryEventStore();
      eventStore.setOnRecorded(it -> System.out.println("Logged event: " + it));
    } else {
      var userHome = System.getProperty("user.home");
      var file = Paths.get(userHome, "activity-log.csv");
      System.out.println("Save activity log in: " + file.toAbsolutePath());
      eventStore = new CsvEventStore(file);
    }
  }

  @Override
  public void start(Stage stage) {
    var logActivityCommandHandler = new LogActivityCommandHandler(eventStore);
    var activityLogQueryHandler = new ActivityLogQueryHandler(eventStore);

    var frontend = new ActivitySamplingView();
    frontend.setOnLogActivityCommand(
        it -> {
          logActivityCommandHandler.handle(it);
          var result = activityLogQueryHandler.handle(new ActivityLogQuery());
          frontend.display(result);
        });

    var scene = new Scene(frontend);
    stage.setScene(scene);
    stage.setTitle("Activity Sampling");
    stage.setMinWidth(240);
    stage.setMinHeight(420);
    stage.show();

    frontend.run();
  }
}
