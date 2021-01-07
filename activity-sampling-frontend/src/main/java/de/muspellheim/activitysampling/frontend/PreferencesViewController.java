/*
 * Activity Sampling - Frontend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.frontend;

public class PreferencesViewController extends StageController<PreferencesView> {
  public PreferencesViewController(PreferencesView view) {
    super(view);

    getStage().setTitle("Preferences");
    getStage().setMinWidth(400);
    getStage().setMinHeight(120);
  }
}
