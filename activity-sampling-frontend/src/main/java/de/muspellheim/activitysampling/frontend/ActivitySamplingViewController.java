/*
 * Activity Sampling - Frontend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.frontend;

import javafx.stage.Stage;

public class ActivitySamplingViewController extends StageController<ActivitySamplingView> {
  public ActivitySamplingViewController(Stage stage, ActivitySamplingView view) {
    super(stage, view);

    stage.setTitle("Activity Sampling");
    stage.setMinWidth(240);
    stage.setMinHeight(420);

    view.setOnOpenPreferences(() -> handleOpenPreferences());
  }

  private void handleOpenPreferences() {
    var preferencesView = new PreferencesView();
    var preferencesViewController = new PreferencesViewController(preferencesView);
    preferencesViewController.getStage().initOwner(getStage());
    preferencesViewController.show();
  }
}
