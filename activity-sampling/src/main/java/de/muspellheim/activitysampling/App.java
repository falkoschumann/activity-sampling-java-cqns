/*
 * Activity Sampling - Application
 * Copyright (c) 2020 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling;

import de.muspellheim.activitysampling.backend.EventStore;
import de.muspellheim.activitysampling.backend.EventStoreCsv;
import de.muspellheim.activitysampling.backend.EventStoreMemory;
import de.muspellheim.activitysampling.backend.messagehandlers.ClockTickedNotificationHandler;
import de.muspellheim.activitysampling.backend.messagehandlers.LogActivityCommandHandler;
import de.muspellheim.activitysampling.contract.messages.commands.CommandStatus;
import de.muspellheim.activitysampling.contract.messages.commands.Failure;
import de.muspellheim.activitysampling.contract.messages.commands.LogActivityCommand;
import de.muspellheim.activitysampling.frontend.ActivitySamplingView;
import de.muspellheim.activitysampling.frontend.AppTrayIcon;
import de.muspellheim.activitysampling.frontend.SystemClock;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.function.Consumer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
  private EventStore eventStore;
  private AppTrayIcon trayIcon;

  public static void main(String[] args) {
    Application.launch(args);
  }

  @Override
  public void init() {
    if (getParameters().getUnnamed().contains("-demo")) {
      System.out.println("Run in demo mode...");
      eventStore = new EventStoreMemory();
      eventStore.setOnRecorded(it -> System.out.println("Logged event: " + it));
    } else {
      var file = Paths.get(System.getProperty("user.home"), "activity-log.csv");
      eventStore = new EventStoreCsv(file);
    }
  }

  @Override
  public void start(Stage stage) {
    var logActivityCommandHandler = new LogActivityCommandHandler(eventStore);
    var clockTickedNotificationHandler = new ClockTickedNotificationHandler(Duration.ofMinutes(1));

    var clock = new SystemClock();
    var view = new ActivitySamplingView();
    trayIcon = new AppTrayIcon();

    clockTickedNotificationHandler.setOnPeriodStartedNotification(it -> view.display(it));
    clockTickedNotificationHandler.setOnPeriodProgressedNotification(it -> view.display(it));
    clockTickedNotificationHandler.setOnPeriodEndedNotification(
        it -> {
          view.display(it);
          trayIcon.display(it);
          logActivityCommandHandler.handle(it);
        });

    clock.setOnTick(it -> clockTickedNotificationHandler.handle(it));
    Consumer<LogActivityCommand> handleLogActivityCommandConsumer =
        it -> {
          var status = logActivityCommandHandler.handle(it);
          if (handleCommandStatus(status)) {
            trayIcon.setLastActivity(it);
            trayIcon.dispose();
          }
        };
    view.setOnLogActivityCommand(handleLogActivityCommandConsumer);
    trayIcon.setOnLogActivityCommand(handleLogActivityCommandConsumer);

    var scene = new Scene(view);
    stage.setScene(scene);
    stage.setTitle("Activity Sampling");
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

  @Override
  public void stop() {
    trayIcon.dispose();
  }
}
