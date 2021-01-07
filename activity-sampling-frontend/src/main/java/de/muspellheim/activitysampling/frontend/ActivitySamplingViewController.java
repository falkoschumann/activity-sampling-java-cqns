/*
 * Activity Sampling - Frontend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.frontend;

import javafx.stage.Stage;

public class ActivitySamplingViewController extends StageController<ActivitySamplingView> {
  private final SystemClock clock;

  public ActivitySamplingViewController(Stage stage, ActivitySamplingView view) {
    super(stage, view);

    stage.setTitle("Activity Sampling");
    stage.setMinWidth(240);
    stage.setMinHeight(420);

    view.setOnOpenPreferences(() -> handleOpenPreferences());

    var periodCheck = new PeriodCheck();
    periodCheck.setOnPeriodStarted(it -> view.periodStarted(it));
    periodCheck.setOnPeriodProgressed(it -> view.periodProgressed(it));
    periodCheck.setOnPeriodEnded(it -> view.periodEnded(it));

    clock = new SystemClock();
    clock.setOnTick(it -> periodCheck.check(it));
  }

  public void run() {
    getView().run();
    clock.run();
  }

  private void handleOpenPreferences() {
    var preferencesViewController = new PreferencesViewController(new PreferencesView());
    preferencesViewController.getStage().initOwner(getStage());
    preferencesViewController.show();
  }
}
