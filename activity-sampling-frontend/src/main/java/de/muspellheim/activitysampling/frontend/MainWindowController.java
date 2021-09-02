/*
 * Activity Sampling - Frontend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.frontend;

import de.muspellheim.activitysampling.contract.data.ActivityTemplate;
import de.muspellheim.activitysampling.contract.data.Bounds;
import de.muspellheim.activitysampling.contract.messages.commands.ChangeMainWindowBoundsCommand;
import de.muspellheim.activitysampling.contract.messages.commands.ChangePreferencesCommand;
import de.muspellheim.activitysampling.contract.messages.commands.Failure;
import de.muspellheim.activitysampling.contract.messages.commands.LogActivityCommand;
import de.muspellheim.activitysampling.contract.messages.notification.PeriodProgressedNotification;
import de.muspellheim.activitysampling.contract.messages.queries.ActivityLogQuery;
import de.muspellheim.activitysampling.contract.messages.queries.ActivityLogQueryResult;
import de.muspellheim.activitysampling.contract.messages.queries.MainWindowBoundsQuery;
import de.muspellheim.activitysampling.contract.messages.queries.MainWindowBoundsQueryResult;
import de.muspellheim.activitysampling.contract.messages.queries.PreferencesQuery;
import de.muspellheim.activitysampling.contract.messages.queries.PreferencesQueryResult;
import de.muspellheim.activitysampling.contract.messages.queries.TimeReportQuery;
import de.muspellheim.activitysampling.contract.messages.queries.TimeReportQueryResult;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
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
import lombok.Getter;
import lombok.Setter;

public class MainWindowController {
  @Getter @Setter private Consumer<ChangeMainWindowBoundsCommand> onChangeMainWindowBoundsCommand;
  @Getter @Setter private Consumer<ChangePreferencesCommand> onChangePreferencesCommand;
  @Getter @Setter private Consumer<LogActivityCommand> onLogActivityCommand;
  @Getter @Setter private Consumer<ActivityLogQuery> onActivityLogQuery;
  @Getter @Setter private Consumer<MainWindowBoundsQuery> onMainWindowBoundsQuery;
  @Getter @Setter private Consumer<PreferencesQuery> onPreferencesQuery;
  @Getter @Setter private Consumer<TimeReportQuery> onTimeReportQuery;

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
  private PreferencesController preferencesController;
  private TimeReportController timeReportController;

  private Duration periodDuration;
  private LocalDateTime timestamp;

  public static MainWindowController create(Stage stage) {
    try {
      var location = MainWindowController.class.getResource("MainWindowView.fxml");
      var resources = ResourceBundle.getBundle("ActivitySampling");
      var loader = new FXMLLoader(location, resources);
      loader.setRoot(stage);
      loader.load();
      return loader.getController();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  @FXML
  private void initialize() {
    menuBar.setUseSystemMenuBar(true);
    trayIconViewController = new TrayIconController();
    preferencesController = PreferencesController.create(stage);
    timeReportController = TimeReportController.create(stage);

    trayIconViewController.setOnActivitySelected(this::logActivity);
    Platform.runLater(() -> stage.setOnHiding(e -> trayIconViewController.hide()));
  }

  public void run() {
    onMainWindowBoundsQuery.accept(new MainWindowBoundsQuery());
    onPreferencesQuery.accept(new PreferencesQuery());
    onActivityLogQuery.accept(new ActivityLogQuery());
  }

  public void display(ActivityLogQueryResult result) {
    Platform.runLater(
        () -> {
          clientCombo.getItems().setAll(result.recentClients());
          projectCombo.getItems().setAll(result.recentProjects());
          taskCombo.getItems().setAll(result.recentTasks());
          logText.setText(result.log());
          logText.positionCaret(logText.getText().length());

          if (result.last() != null) {
            clientCombo.setValue(result.last().client());
            projectCombo.setValue(result.last().project());
            taskCombo.setValue(result.last().task());
            notesText.setText(result.last().notes());
          }

          var menuItems =
              result.recent().stream()
                  .map(
                      it -> {
                        var menuItem = new MenuItem(it.toString());
                        menuItem.setOnAction(e -> logActivity(it));
                        return menuItem;
                      })
                  .toList();
          logButton.getItems().setAll(menuItems);

          trayIconViewController.setRecent(result.recent());
        });
  }

  public void display(MainWindowBoundsQueryResult result) {
    Platform.runLater(
        () -> {
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
        });
  }

  public void display(PreferencesQueryResult result) {
    Platform.runLater(
        () -> {
          periodDuration = result.periodDuration();
          preferencesController.display(result);
        });
  }

  public void display(TimeReportQueryResult result) {
    Platform.runLater(() -> timeReportController.display(result));
  }

  public void display(PeriodProgressedNotification notification) {
    Platform.runLater(
        () -> {
          remainingTimeLabel.setText(
              DateTimeFormatter.ofPattern("HH:mm:ss").format(notification.remaining()));
          progressBar.setProgress(notification.progress());
          if (notification.end() != null) {
            timestamp = notification.end();
            clientCombo.setDisable(false);
            projectCombo.setDisable(false);
            taskCombo.setDisable(false);
            notesText.setDisable(false);
            logButton.setDisable(false);
            trayIconViewController.show();
          }
        });
  }

  @Deprecated
  public void display(Failure failure) {
    // TODO Ersetze durch Exception: Exception für Ausnahmen, Failure für erwartete Fehler
    Platform.runLater(
        () -> {
          var index = failure.errorMessage().indexOf(": ");
          var header = index == -1 ? null : failure.errorMessage().substring(0, index);
          var content =
              index == -1 ? failure.errorMessage() : failure.errorMessage().substring(index + 1);

          var alert = new Alert(AlertType.ERROR);
          alert.initOwner(stage);
          alert.setHeaderText(header);
          alert.setContentText(content);
          alert.show();
        });
  }

  @FXML
  private void handleOpenPreferences() {
    preferencesController.setOnChangePreferencesCommand(onChangePreferencesCommand);
    preferencesController.run();
  }

  @FXML
  private void handleClose() {
    onChangeMainWindowBoundsCommand.accept(
        new ChangeMainWindowBoundsCommand(
            new Bounds(stage.getX(), stage.getY(), stage.getWidth(), stage.getHeight())));
    stage.close();
  }

  @FXML
  private void handleOpenTimeReport() {
    timeReportController.setOnTimesheetQuery(onTimeReportQuery);
    timeReportController.run();
  }

  @FXML
  private void handleOpenAbout() {
    var controller = AboutController.create(stage);
    controller.run();
  }

  @FXML
  private void handleLogActivity() {
    onLogActivityCommand.accept(
        new LogActivityCommand(
            timestamp,
            periodDuration,
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
  }

  private void logActivity(ActivityTemplate activity) {
    clientCombo.setValue(activity.client());
    projectCombo.setValue(activity.project());
    taskCombo.setValue(activity.task());
    notesText.setText(activity.notes());
    handleLogActivity();
  }
}
