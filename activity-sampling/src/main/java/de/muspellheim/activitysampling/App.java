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
import de.muspellheim.activitysampling.frontend.ActivitySamplingViewController;
import java.nio.file.Paths;
import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {
  private EventStore eventStore;
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
    } else {
      var userHome = System.getProperty("user.home");
      var file = Paths.get(userHome, "activity-log.csv");
      System.out.println("Save activity log in: " + file.toAbsolutePath());
      eventStore = new CsvEventStore(file);
    }
    if (getParameters().getUnnamed().contains("--noSystemMenuBar")) {
      useSystemMenuBar = false;
    }
  }

  @Override
  public void start(Stage stage) {
    var logActivityCommandHandler = new LogActivityCommandHandler(eventStore);
    var activityLogQueryHandler = new ActivityLogQueryHandler(eventStore);

    ActivitySamplingView frontend = new ActivitySamplingView(useSystemMenuBar);
    frontend.setOnLogActivityCommand(
        it -> {
          logActivityCommandHandler.handle(it);
          var result = activityLogQueryHandler.handle(new ActivityLogQuery());
          frontend.display(result);
        });
    frontend.setOnActivityLogQuery(
        it -> {
          var result = activityLogQueryHandler.handle(it);
          frontend.display(result);
        });

    var frontendController = new ActivitySamplingViewController(stage, frontend);
    frontendController.show();

    frontend.run();
  }
}
