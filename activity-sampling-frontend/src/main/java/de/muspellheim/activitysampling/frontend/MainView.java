/*
 * Activity Sampling - Frontend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.frontend;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class MainView {
  @FXML private TextField activityText;
  @FXML private TextField tagsText;
  @FXML private SplitMenuButton logButton;
  @FXML private Label progressText;
  @FXML private ProgressBar progressBar;
  @FXML private TextArea activityLog;

  private final AppTrayIcon trayIcon = new AppTrayIcon();

  private final ActivitySamplingViewModel viewModel =
      ViewModelFactory.getActivitySamplingViewModel();

  public static MainView create(Stage stage) {
    var factory = new ViewControllerFactory(MainView.class);

    var scene = new Scene(factory.getView());
    stage.setScene(scene);
    stage.setTitle("Activity Sampling");
    stage.setMinWidth(240);
    stage.setMinHeight(420);

    return factory.getController();
  }

  public void run() {
    getWindow().show();
    viewModel.loadPreferences();
    viewModel.loadActivityLog();
  }

  private Stage getWindow() {
    return (Stage) activityText.getScene().getWindow();
  }

  @FXML
  private void initialize() {
    activityText.disableProperty().bind(viewModel.formDisabledProperty());
    activityText.textProperty().bindBidirectional(viewModel.activityProperty());
    tagsText.disableProperty().bind(viewModel.formDisabledProperty());
    tagsText.textProperty().bindBidirectional(viewModel.tagsProperty());
    logButton.disableProperty().bind(viewModel.formDisabledProperty());

    progressText.textProperty().bind(viewModel.remainingTimeProperty());
    progressBar.progressProperty().bind(viewModel.progressProperty());

    activityLog.textProperty().bind(viewModel.activityLogFile());
  }

  @FXML
  private void handlePreferences() {
    var preferencesView = PreferencesView.create(getWindow());
    preferencesView.run();
  }

  @FXML
  private void handleExit() {
    Platform.exit();
  }

  @FXML
  private void handleAbout() {
    var infoView = InfoView.create(getWindow());
    infoView.run();
  }

  @FXML
  private void handleLogActivity() {
    viewModel.logActivity();
  }
}
