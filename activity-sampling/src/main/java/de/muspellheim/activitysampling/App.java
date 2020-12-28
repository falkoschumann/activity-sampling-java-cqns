/*
 * Activity Sampling - Application
 * Copyright (c) 2020 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling;

import de.muspellheim.activitysampling.backend.CsvEventStore;
import de.muspellheim.activitysampling.backend.EventStore;
import de.muspellheim.activitysampling.backend.SystemClock;
import de.muspellheim.activitysampling.backend.messagehandlers.ClockTickedNotificationHandler;
import de.muspellheim.activitysampling.backend.messagehandlers.LogActivityCommandHandler;
import de.muspellheim.activitysampling.frontend.ActivitySamplingView;
import de.muspellheim.activitysampling.frontend.AppTrayIcon;
import java.time.Duration;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
  private LogActivityCommandHandler logActivityCommandHandler;
  private ClockTickedNotificationHandler clockTickedNotificationHandler;

  private SystemClock clock;
  private ActivitySamplingView activitySamplingView;
  private AppTrayIcon trayIconController;
  private Stage stage;

  public static void main(String[] args) {
    Application.launch(args);
  }

  @Override
  public void start(Stage primaryStage) {
    stage = primaryStage;
    build();
    bind();
    run();
  }

  @Override
  public void stop() {
    trayIconController.dispose();
  }

  private void build() {
    EventStore eventStore = new CsvEventStore("activity-log.csv");
    logActivityCommandHandler = new LogActivityCommandHandler(eventStore);
    clockTickedNotificationHandler = new ClockTickedNotificationHandler();
    clockTickedNotificationHandler.setPeriod(Duration.ofMinutes(1));

    clock = new SystemClock();
    activitySamplingView = new ActivitySamplingView();
    trayIconController = new AppTrayIcon();

    var scene = new Scene(activitySamplingView);
    stage.setScene(scene);
  }

  private void bind() {
    clockTickedNotificationHandler.setOnPeriodStartedNotification(
        n -> activitySamplingView.display(n));
    clockTickedNotificationHandler.setOnPeriodProgressedNotification(
        n -> activitySamplingView.display(n));
    clockTickedNotificationHandler.setOnPeriodEndedNotification(
        n -> {
          activitySamplingView.display(n);
          trayIconController.display(n);
          logActivityCommandHandler.handle(n);
        });

    clock.setOnTick(n -> clockTickedNotificationHandler.handle(n));
    activitySamplingView.setOnLogActivityCommand(c -> logActivityCommandHandler.handle(c));
  }

  private void run() {
    stage.show();
    clock.run();
  }
}
