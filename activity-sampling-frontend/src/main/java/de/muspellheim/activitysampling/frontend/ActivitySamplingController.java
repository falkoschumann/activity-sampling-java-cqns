/*
 * Activity Sampling - Frontend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.frontend;

import de.muspellheim.activitysampling.contract.messages.commands.ChangeActivityLogFileCommand;
import de.muspellheim.activitysampling.contract.messages.commands.ChangePeriodDurationCommand;
import de.muspellheim.activitysampling.contract.messages.commands.LogActivityCommand;
import de.muspellheim.activitysampling.contract.messages.queries.ActivityLogQuery;
import de.muspellheim.activitysampling.contract.messages.queries.ActivityLogQueryResult;
import de.muspellheim.activitysampling.contract.messages.queries.PreferencesQuery;
import de.muspellheim.activitysampling.contract.messages.queries.PreferencesQueryResult;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;

public class ActivitySamplingController {
  @Getter @Setter private Consumer<LogActivityCommand> onLogActivityCommand;
  @Getter @Setter private Consumer<ChangePeriodDurationCommand> onChangePeriodDurationCommand;
  @Getter @Setter private Consumer<ChangeActivityLogFileCommand> onChangeActivityLogFileCommand;
  @Getter @Setter private Consumer<PreferencesQuery> onPreferencesQuery;
  @Getter @Setter private Consumer<ActivityLogQuery> onActivityLogQuery;

  @FXML private Stage stage;
  @FXML private MenuBar menuBar;
  @FXML private SeparatorMenuItem quitSeparatorMenuItem;
  @FXML private MenuItem quitMenuItem;
  @FXML private TextField activityText;
  @FXML private TextField tagsText;
  @FXML private SplitMenuButton logButton;
  @FXML private Label remainingTimeLabel;
  @FXML private ProgressBar progressBar;
  @FXML private TextArea activityLogText;
  private TrayIconController trayIconViewController;

  private ActivitySamplingModel model;

  public static ActivitySamplingController create(Stage stage) {
    try {
      var location = ActivitySamplingController.class.getResource("MainView.fxml");
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
    var runningOnMac = System.getProperty("os.name").toLowerCase().contains("mac");
    if (runningOnMac) {
      menuBar.setUseSystemMenuBar(true);
      quitSeparatorMenuItem.setVisible(false);
      quitMenuItem.setVisible(false);
    }
    trayIconViewController = new TrayIconController();
    model = new ActivitySamplingModel();

    activityText
        .textProperty()
        .addListener(
            o -> {
              System.out.println("Update activity text");
              model.setActivity(activityText.getText());
              logButton.setDisable(model.isFormInvalid());
            });
    trayIconViewController.setOnActivitySelected(this::handleLogActivity);
    Platform.runLater(() -> stage.setOnHiding(e -> trayIconViewController.hide()));
  }

  public void run() {
    var systemClock = new Timer(true);
    systemClock.schedule(
        new TimerTask() {
          @Override
          public void run() {
            Platform.runLater(
                () -> {
                  var currentTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
                  if (model.progressPeriod(currentTime)) {
                    activityText.setDisable(false);
                    activityText.requestFocus();
                    tagsText.setDisable(false);
                    logButton.setDisable(model.isFormInvalid());
                    trayIconViewController.show();
                    // } else {
                    // activityText.setDisable(true);
                    // tagsText.setDisable(true);
                    // logButton.setDisable(true);
                  }

                  remainingTimeLabel.setText(model.getRemainingTimeAsString());
                  progressBar.setProgress(model.getPeriodProgress());
                });
          }
        },
        0,
        1000);

    onPreferencesQuery.accept(new PreferencesQuery());
    onActivityLogQuery.accept(new ActivityLogQuery());

    stage.show();
  }

  public void display(PreferencesQueryResult result) {
    model.setActivityLogFile(result.activityLogFile());
    model.setPeriodDuration(result.periodDuration());
    model.resetPeriod();
  }

  public void display(ActivityLogQueryResult result) {
    model.setLog(result.log());
    model.setRecent(result.recent());
    model.setLast(result.last());

    activityText.setText(model.getActivity());
    tagsText.setText(model.getTags());
    logButton
        .getItems()
        .setAll(
            model.getRecentAsString().stream()
                .map(
                    it -> {
                      var menuItem = new MenuItem(it);
                      menuItem.setOnAction(e -> handleLogActivity(it));
                      return menuItem;
                    })
                .toList());

    activityLogText.setText(model.getLogAsString());
    activityLogText.setScrollTop(Double.MAX_VALUE);

    trayIconViewController.setRecent(model.getRecentAsString());
  }

  @FXML
  private void handleOpenPreferences() {
    var controller = PreferencesController.create(stage);
    controller.setPeriodDuration(model.getPeriodDuration());
    controller.setActivityLogFile(model.getActivityLogFile());

    controller
        .periodDurationProperty()
        .addListener(
            observable ->
                onChangePeriodDurationCommand.accept(
                    new ChangePeriodDurationCommand(controller.getPeriodDuration())));
    controller
        .activityLogFileProperty()
        .addListener(
            observable ->
                onChangeActivityLogFileCommand.accept(
                    new ChangeActivityLogFileCommand(controller.getActivityLogFile())));

    controller.run();
  }

  @FXML
  private void handleQuit() {
    Platform.exit();
  }

  @FXML
  private void handleOpenAbout() {
    var controller = AboutController.create(stage);
    controller.run();
  }

  @FXML
  private void handleLogActivity() {
    model.setActivity(activityText.getText());
    model.setTags(tagsText.getText());
    logActivity();
  }

  private void handleLogActivity(String activity) {
    model.setActivity(activity);
    logActivity();
  }

  private void logActivity() {
    activityText.setDisable(true);
    tagsText.setDisable(true);
    logButton.setDisable(true);
    trayIconViewController.hide();
    onLogActivityCommand.accept(
        new LogActivityCommand(
            model.getPeriodEnd(),
            model.getPeriodDuration(),
            model.getActivity(),
            model.getTagsAsList()));
  }
}
