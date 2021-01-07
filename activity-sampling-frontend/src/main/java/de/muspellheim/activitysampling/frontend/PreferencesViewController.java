/*
 * Activity Sampling - Frontend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.frontend;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class PreferencesViewController {
  @FXML private Spinner<Integer> periodDuration;
  @FXML private TextField activityLogFile;

  public static PreferencesViewController create(Stage stage) {
    var factory = new ViewControllerFactory(PreferencesViewController.class);
    var scene = new Scene(factory.getView());
    stage.setScene(scene);
    stage.setTitle("Preferences");
    stage.setMinWidth(400);
    stage.setMinHeight(120);
    return factory.getController();
  }

  @FXML
  private void handleChange() {
    // TODO Handle change
  }
}
