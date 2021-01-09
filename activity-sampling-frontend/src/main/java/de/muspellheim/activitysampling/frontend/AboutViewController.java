/*
 * Activity Sampling - Frontend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.frontend;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class AboutViewController {
  @FXML private Label version;
  @FXML private Label copyright;

  public static AboutViewController create(Stage stage) {
    var factory = new ViewControllerFactory(AboutViewController.class);
    var scene = new Scene(factory.getView());
    stage.setScene(scene);
    stage.initModality(Modality.APPLICATION_MODAL);
    stage.initStyle(StageStyle.UTILITY);
    stage.setResizable(false);
    stage.setTitle("About Activity Sampling");
    return factory.getController();
  }

  @FXML
  private void initialize() {
    var appVersion = System.getProperty("app.version");
    version.setText("Version " + appVersion);

    var appCopyrightYear = System.getProperty("app.copyrightYear");
    copyright.setText("Copyright © " + appCopyrightYear + " Falko Schumann");
  }
}
