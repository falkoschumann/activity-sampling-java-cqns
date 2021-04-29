/*
 * Activity Sampling - Frontend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.frontend;

import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainView {
  @FXML private Region spacer;
  @FXML private VBox activityForm;
  @FXML private TextField activityText;
  @FXML private TextField tagsText;
  @FXML private SplitMenuButton logButton;
  @FXML private Label progressText;
  @FXML private ProgressBar progressBar;
  @FXML private TextArea activityLog;

  private final AppTrayIcon trayIcon = new AppTrayIcon();
  private final SystemClock clock = new SystemClock();

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
    viewModel.reloadActivityLog();

    clock.run();
  }

  private Stage getWindow() {
    return (Stage) activityText.getScene().getWindow();
  }

  @FXML
  private void initialize() {
    HBox.setHgrow(spacer, Priority.ALWAYS);

    activityForm.disableProperty().bind(viewModel.formDisabledProperty());
    activityForm.disableProperty().addListener(observable -> activityText.requestFocus());
    activityText.textProperty().bindBidirectional(viewModel.activityProperty());
    tagsText.textProperty().bindBidirectional(viewModel.tagsProperty());
    viewModel
        .getRecentActivities()
        .addListener((InvalidationListener) observable -> updateLogButton());

    progressText.textProperty().bind(viewModel.remainingTimeProperty());
    progressBar.progressProperty().bind(viewModel.progressProperty());

    activityLog.textProperty().bind(viewModel.activityLogProperty());
    activityLog
        .textProperty()
        .addListener(
            observable -> Platform.runLater(() -> activityLog.setScrollTop(Double.MAX_VALUE)));

    Platform.runLater(() -> getWindow().setOnHiding(e -> trayIcon.hide()));

    clock.setOnTick(it -> Platform.runLater(() -> viewModel.clockTicked(it)));
  }

  private void updateLogButton() {
    var menuItems =
        viewModel.getRecentActivities().stream()
            .map(
                it -> {
                  var menuItem = new MenuItem(it);
                  menuItem.setOnAction(e -> viewModel.logActivity(it));
                  return menuItem;
                })
            .collect(Collectors.toList());
    Platform.runLater(() -> logButton.getItems().setAll(menuItems));
  }

  @FXML
  private void openPreferences() {
    var preferencesView = SettingsView.create(getWindow());
    preferencesView.run();
  }

  @FXML
  private void openInfo() {
    var infoView = InfoView.create(getWindow());
    infoView.run();
  }

  @FXML
  private void logActivity() {
    viewModel.logActivity();
  }
}