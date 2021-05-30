/*
 * Activity Sampling - Frontend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.frontend;

import java.io.IOException;
import java.io.UncheckedIOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class AboutViewController {
  @FXML private Stage stage;
  @FXML private Label version;
  @FXML private Label copyright;

  public static AboutViewController create(Stage owner) {
    try {
      var location = AboutViewController.class.getResource("AboutView.fxml");
      var loader = new FXMLLoader(location);
      loader.load();
      AboutViewController controller = loader.getController();
      controller.stage.initOwner(owner);
      controller.stage.initModality(Modality.APPLICATION_MODAL);
      controller.stage.initStyle(StageStyle.UTILITY);
      return controller;
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  @FXML
  private void initialize() {
    version.setText(System.getProperty("app.version"));
    copyright.setText(System.getProperty("app.copyright").replace("(c)", "©"));
  }

  public void run() {
    stage.show();
  }
}
