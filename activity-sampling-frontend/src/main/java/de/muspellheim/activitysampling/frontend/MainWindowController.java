/*
 * Activity Sampling - Frontend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.frontend;

import de.muspellheim.activitysampling.contract.MessageHandling;
import de.muspellheim.activitysampling.contract.data.ActivityTemplate;
import de.muspellheim.activitysampling.contract.data.Bounds;
import de.muspellheim.activitysampling.contract.messages.commands.ChangeMainWindowBoundsCommand;
import de.muspellheim.activitysampling.contract.messages.commands.LogActivityCommand;
import de.muspellheim.activitysampling.contract.messages.notification.PeriodEndedNotification;
import de.muspellheim.activitysampling.contract.messages.notification.PeriodProgressedNotification;
import de.muspellheim.activitysampling.contract.messages.queries.ActivityLogQuery;
import de.muspellheim.activitysampling.contract.messages.queries.ActivityLogQueryResult;
import de.muspellheim.activitysampling.contract.messages.queries.MainWindowBoundsQuery;
import de.muspellheim.activitysampling.contract.messages.queries.MainWindowBoundsQueryResult;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class MainWindowController {
  @FXML private Stage stage;
  @FXML private MenuBar menuBar;
  @FXML private ComboBox<String> clientCombo;
  @FXML private ComboBox<String> projectCombo;
  @FXML private ComboBox<String> taskCombo;
  @FXML private TextField notesText;
  @FXML private SplitMenuButton logButton;
  @FXML private Label remainingTimeLabel;
  @FXML private ProgressBar progressBar;
  @FXML private TextArea logText;

  private TrayIconController trayIconViewController;

  private MessageHandling messageHandling;

  private Duration period;
  private LocalDateTime timestamp;

  public static MainWindowController create(Stage stage, MessageHandling messageHandling) {
    try {
      var location = MainWindowController.class.getResource("MainWindowView.fxml");
      var resources = ResourceBundle.getBundle("ActivitySampling");
      var loader = new FXMLLoader(location, resources);
      loader.setRoot(stage);
      loader.load();

      var controller = (MainWindowController) loader.getController();
      controller.messageHandling = messageHandling;
      return controller;
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  @FXML
  private void initialize() {
    // Build
    menuBar.setUseSystemMenuBar(true);
    // new AutoCompleteComboBoxListener<>(clientCombo);
    // new AutoCompleteComboBoxListener<>(projectCombo);
    // new AutoCompleteComboBoxListener<>(taskCombo);

    trayIconViewController = new TrayIconController();

    // Bind
    trayIconViewController.setOnActivitySelected(this::logActivity);
    Platform.runLater(() -> stage.setOnHiding(e -> trayIconViewController.hide()));
  }

  public void run() {
    messageHandling.setOnPeriodProgressedNotification(this::display);
    messageHandling.setOnPeriodEndedNotification(this::display);

    display(messageHandling.handle(new MainWindowBoundsQuery()));
    display(messageHandling.handle(new ActivityLogQuery()));
  }

  private void display(ActivityLogQueryResult result) {
    clientCombo.getItems().setAll(result.recentClients());
    projectCombo.getItems().setAll(result.recentProjects());
    // TODO Aktualisiere AutoCompleteComboBoxListener.data
    System.out.println("Recent tasks: " + result.recentTasks());
    taskCombo.getItems().setAll(result.recentTasks());
    logText.setText(result.log());
    Platform.runLater(() -> logText.positionCaret(result.log().length()));

    if (result.last() != null) {
      clientCombo.setValue(result.last().client());
      projectCombo.setValue(result.last().project());
      taskCombo.setValue(result.last().task());
      notesText.setText(result.last().notes());
    }

    var stringConverter = new ActivityTemplateStringConverter();
    var menuItems =
        result.recent().stream()
            .map(
                it -> {
                  var menuItem = new MenuItem(stringConverter.toString(it));
                  menuItem.setOnAction(e -> logActivity(it));
                  return menuItem;
                })
            .toList();
    logButton.getItems().setAll(menuItems);

    trayIconViewController.setRecent(result.recent());
  }

  private void display(MainWindowBoundsQueryResult result) {
    if (!Bounds.NULL.equals(result.bounds())) {
      var x = result.bounds().x();
      var y = result.bounds().y();
      var width = result.bounds().width();
      var height = result.bounds().height();
      if (!Screen.getScreensForRectangle(x, y, width, height).isEmpty()) {
        stage.setX(x);
        stage.setY(y);
        stage.setWidth(width);
        stage.setHeight(height);
      }
    }
    stage.show();
  }

  private void display(PeriodProgressedNotification notification) {
    Platform.runLater(
        () -> {
          remainingTimeLabel.setText(
              DateTimeFormatter.ofPattern("HH:mm:ss").format(notification.remaining()));
          progressBar.setProgress(notification.progress());
        });
  }

  private void display(PeriodEndedNotification notification) {
    Platform.runLater(
        () -> {
          remainingTimeLabel.setText("00:00:00");
          progressBar.setProgress(1.0);
          timestamp = notification.timestamp();
          period = notification.period();
          clientCombo.setDisable(false);
          projectCombo.setDisable(false);
          taskCombo.setDisable(false);
          notesText.setDisable(false);
          logButton.setDisable(false);
          trayIconViewController.show();
        });
  }

  @FXML
  private void handleOpenPreferences() {
    var controller = PreferencesController.create(stage, messageHandling);
    controller.run();
  }

  @FXML
  private void handleClose() {
    messageHandling.handle(
        new ChangeMainWindowBoundsCommand(
            new Bounds(stage.getX(), stage.getY(), stage.getWidth(), stage.getHeight())));
    stage.close();
  }

  @FXML
  private void handleOpenTimeReport() {
    var controller = TimeReportController.create(stage, messageHandling);
    controller.run();
  }

  @FXML
  private void handleOpenAbout() {
    var controller = AboutController.create(stage);
    controller.run();
  }

  @FXML
  private void handleLogActivity() {
    messageHandling.handle(
        new LogActivityCommand(
            timestamp,
            period,
            clientCombo.getValue(),
            projectCombo.getValue(),
            taskCombo.getValue(),
            notesText.getText()));
    clientCombo.setDisable(true);
    projectCombo.setDisable(true);
    taskCombo.setDisable(true);
    notesText.setDisable(true);
    logButton.setDisable(true);
    trayIconViewController.hide();

    var result = messageHandling.handle(new ActivityLogQuery());
    display(result);
  }

  private void logActivity(ActivityTemplate activity) {
    clientCombo.setValue(activity.client());
    projectCombo.setValue(activity.project());
    taskCombo.setValue(activity.task());
    notesText.setText(activity.notes());
    handleLogActivity();
  }
}
