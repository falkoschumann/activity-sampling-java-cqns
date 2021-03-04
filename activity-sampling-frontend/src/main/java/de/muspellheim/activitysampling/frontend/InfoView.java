/*
 * Activity Sampling - Frontend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.frontend;

import java.net.URL;
import java.util.Properties;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class InfoView {
  @FXML private ImageView icon;
  @FXML private Label title;
  @FXML private Label version;
  @FXML private Label copyright;

  private final InfoViewModel viewModel = new InfoViewModel();

  public static InfoView create(Stage owner, URL appIconUrl, Properties appProperties) {
    var factory = new ViewControllerFactory(InfoView.class);

    var stage = new Stage();
    stage.initOwner(owner);
    stage.initStyle(StageStyle.UTILITY);
    stage.setTitle("Info");
    stage.setScene(new Scene(factory.getView()));
    stage.setResizable(false);

    InfoView controller = factory.getController();
    controller.viewModel.initIconUrl(appIconUrl);
    controller.viewModel.initProperties(appProperties);
    return controller;
  }

  @FXML
  private void initialize() {
    viewModel
        .iconUrlProperty()
        .addListener(
            observable -> icon.setImage(new Image(viewModel.iconUrlProperty().get(), true)));
    title.textProperty().bind(viewModel.titleProperty());
    version.textProperty().bind(Bindings.concat("Version ", viewModel.versionProperty()));
    copyright.textProperty().bind(viewModel.copyrightProperty());
  }

  public void run() {
    getWindow().show();
  }

  private Stage getWindow() {
    return (Stage) icon.getScene().getWindow();
  }
}
