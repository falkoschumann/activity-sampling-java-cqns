/*
 * Activity Sampling - Application
 * Copyright (c) 2020 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling;

import de.muspellheim.activitysampling.backend.Clock;
import de.muspellheim.activitysampling.backend.PeriodCheck;
import de.muspellheim.activitysampling.frontend.ActivitySamplingView;
import de.muspellheim.activitysampling.frontend.AppTrayIcon;
import java.time.Duration;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
  private Clock clock;
  private PeriodCheck periodCheck;

  private Stage stage;
  private ActivitySamplingView activitySamplingView;
  private AppTrayIcon trayIconController;

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
    activitySamplingView = new ActivitySamplingView();
    trayIconController = new AppTrayIcon();
    clock = new Clock();
    periodCheck = new PeriodCheck();
    periodCheck.setPeriod(Duration.ofMinutes(1));

    var scene = new Scene(activitySamplingView);
    stage.setScene(scene);
  }

  private void bind() {
    activitySamplingView.setOnLogActivityCommand(c -> System.out.println(c));

    clock.setOnTick(n -> periodCheck.handle(n));

    periodCheck.setOnPeriodStartedNotification(n -> activitySamplingView.display(n));
    periodCheck.setOnPeriodProgressedNotification(n -> activitySamplingView.display(n));
    periodCheck.setOnPeriodEndedNotification(
        n -> {
          activitySamplingView.display(n);
          trayIconController.display(n);
        });
  }

  private void run() {
    stage.show();
    clock.run();
  }
}
