/*
 * Activity Sampling - Application
 * Copyright (c) 2020 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling;

import de.muspellheim.activitysampling.backend.EventStore;
import de.muspellheim.activitysampling.backend.adapters.CsvEventStore;
import de.muspellheim.activitysampling.backend.adapters.MemoryEventStore;
import de.muspellheim.activitysampling.backend.messagehandlers.ClockTickedNotificationHandler;
import de.muspellheim.activitysampling.backend.messagehandlers.LogActivityCommandHandler;
import de.muspellheim.activitysampling.contract.messages.commands.CommandStatus;
import de.muspellheim.activitysampling.contract.messages.commands.Failure;
import de.muspellheim.activitysampling.contract.messages.commands.LogActivityCommand;
import de.muspellheim.activitysampling.frontend.ActivitySamplingView;
import de.muspellheim.activitysampling.frontend.SystemClock;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.function.Consumer;
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
    if (getParameters().getUnnamed().contains("-demo")) {
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
    var clockTickedNotificationHandler = new ClockTickedNotificationHandler(Duration.ofMinutes(1));

    var clock = new SystemClock();
    var view = new ActivitySamplingView();

    clockTickedNotificationHandler.setOnPeriodStartedNotification(it -> view.display(it));
    clockTickedNotificationHandler.setOnPeriodProgressedNotification(it -> view.display(it));
    clockTickedNotificationHandler.setOnPeriodEndedNotification(
        it -> {
          view.display(it);
          logActivityCommandHandler.handle(it);
        });

    clock.setOnTick(it -> clockTickedNotificationHandler.handle(it));
    Consumer<LogActivityCommand> handleLogActivityCommandConsumer =
        it -> {
          var status = logActivityCommandHandler.handle(it);
          handleCommandStatus(status);
        };
    view.setOnLogActivityCommand(handleLogActivityCommandConsumer);

    var scene = new Scene(view);
    stage.setScene(scene);
    stage.setTitle("Activity Sampling");
    stage.setMinWidth(240);
    stage.setMinHeight(420);
    stage.show();

    clock.run();
  }

  private boolean handleCommandStatus(CommandStatus status) {
    if (status instanceof Failure) {
      System.err.println(status);
      return false;
    }

    return true;
  }
}
