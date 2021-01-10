/*
 * Activity Sampling - Frontend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.frontend;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class AboutViewController {
  @FXML private ImageView icon;
  @FXML private Label appName;
  @FXML private Label version;
  @FXML private Label copyright;

  public static AboutViewController create(Stage stage) {
    var factory = new ViewControllerFactory(AboutViewController.class);
    var scene = new Scene(factory.getView());
    stage.setScene(scene);
    stage.initModality(Modality.APPLICATION_MODAL);
    stage.initStyle(StageStyle.UTILITY);
    stage.setResizable(false);
    return factory.getController();
  }

  public void initAppIcon(String url) {
    icon.setImage(new Image(url, true));
  }

  public void initAppName(String name) {
    getWindow().setTitle("About " + name);
    appName.setText(name);
  }

  public void initVersion(String version) {
    this.version.setText("Version " + version);
  }

  public void initCopyright(String copyright) {
    this.copyright.setText("Copyright © " + copyright);
  }

  private Stage getWindow() {
    return (Stage) icon.getScene().getWindow();
  }
}
