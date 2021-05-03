/*
 * Activity Sampling - Frontend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.frontend;

import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.SneakyThrows;

public class MainView {
  @FXML private VBox activityForm;
  @FXML private TextField activityText;
  @FXML private TextField tagsText;
  @FXML private SplitMenuButton logButton;
  @FXML private Label progressLabel;
  @FXML private ProgressBar progressBar;
  @FXML private TextArea activityLogText;

  private final AppTrayIcon trayIcon = new AppTrayIcon();
  private final SystemClock clock = new SystemClock();

  @Getter private final ActivitySamplingViewModel viewModel = new ActivitySamplingViewModel();

  @SneakyThrows
  public static MainView create(Stage stage) {
    var location = MainView.class.getResource("MainView.fxml");
    var resources = ResourceBundle.getBundle("ActivitySampling");
    var loader = new FXMLLoader(location, resources);
    loader.setRoot(stage);
    loader.load();
    return loader.getController();
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
    activityForm.disableProperty().bind(viewModel.formDisabledProperty());
    activityForm.disableProperty().addListener(observable -> activityText.requestFocus());
    activityText.textProperty().bindBidirectional(viewModel.activityProperty());
    tagsText.textProperty().bindBidirectional(viewModel.tagsProperty());
    viewModel
        .getRecentActivities()
        .addListener((InvalidationListener) observable -> updateLogButton());

    progressLabel.textProperty().bind(viewModel.remainingTimeProperty());
    progressBar.progressProperty().bind(viewModel.progressProperty());

    activityLogText.textProperty().bind(viewModel.activityLogProperty());
    activityLogText
        .textProperty()
        .addListener(
            observable -> Platform.runLater(() -> activityLogText.setScrollTop(Double.MAX_VALUE)));

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
  private void openSettings() {
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
