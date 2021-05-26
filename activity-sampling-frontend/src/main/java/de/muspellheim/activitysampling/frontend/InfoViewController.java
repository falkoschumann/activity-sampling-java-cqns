/*
 * Activity Sampling - Frontend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.frontend;

import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.SneakyThrows;

public class InfoViewController {
  @FXML private Stage stage;
  @FXML private Label version;
  @FXML private Label copyright;

  @SneakyThrows
  public static InfoViewController create(Stage owner) {
    var location = InfoViewController.class.getResource("InfoView.fxml");
    var resources = ResourceBundle.getBundle("ActivitySampling");
    var loader = new FXMLLoader(location, resources);
    loader.load();
    var controller = (InfoViewController) loader.getController();
    controller.stage.initOwner(owner);
    controller.stage.initStyle(StageStyle.UTILITY);
    return controller;
  }

  @FXML
  private void initialize() {
    version.setText(System.getProperty("app.version"));
    copyright.setText(System.getProperty("app.copyright"));
  }

  public void run() {
    stage.show();
  }
}
