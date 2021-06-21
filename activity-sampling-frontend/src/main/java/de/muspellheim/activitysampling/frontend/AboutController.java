/*
 * Activity Sampling - Frontend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.frontend;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class AboutController {
  @FXML private Stage stage;

  public static AboutController create(Stage owner) {
    try {
      var location = AboutController.class.getResource("AboutView.fxml");
      var resources = ResourceBundle.getBundle("ActivitySampling");
      var loader = new FXMLLoader(location, resources);
      loader.load();

      var controller = (AboutController) loader.getController();
      controller.stage.initOwner(owner);
      controller.stage.initStyle(StageStyle.UTILITY);
      return controller;
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public void run() {
    stage.show();
  }
}
