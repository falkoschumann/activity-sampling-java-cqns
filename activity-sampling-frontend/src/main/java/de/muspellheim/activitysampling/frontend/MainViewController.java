/*
 * Activity Sampling - Frontend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.frontend;

import de.muspellheim.activitysampling.contract.data.Activity;
import de.muspellheim.activitysampling.contract.messages.commands.ChangeActivityLogFileCommand;
import de.muspellheim.activitysampling.contract.messages.commands.ChangePeriodDurationCommand;
import de.muspellheim.activitysampling.contract.messages.commands.LogActivityCommand;
import de.muspellheim.activitysampling.contract.messages.queries.ActivityLogQuery;
import de.muspellheim.activitysampling.contract.messages.queries.ActivityLogQueryResult;
import de.muspellheim.activitysampling.contract.messages.queries.RecentActivitiesQuery;
import de.muspellheim.activitysampling.contract.messages.queries.RecentActivitiesQueryResult;
import de.muspellheim.activitysampling.contract.messages.queries.SettingsQuery;
import de.muspellheim.activitysampling.contract.messages.queries.SettingsQueryResult;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
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
import lombok.SneakyThrows;

public class MainViewController {
  @Getter @Setter private Consumer<LogActivityCommand> onLogActivityCommand;
  @Getter @Setter private Consumer<ChangePeriodDurationCommand> onChangePeriodDurationCommand;
  @Getter @Setter private Consumer<ChangeActivityLogFileCommand> onChangeActivityLogFileCommand;
  @Getter @Setter private Consumer<ActivityLogQuery> onActivityLogQuery;
  @Getter @Setter private Consumer<RecentActivitiesQuery> onRecentActivitiesQuery;
  @Getter @Setter private Consumer<SettingsQuery> onSettingsQuery;

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

  private final BooleanProperty activityFormDisabled =
      new SimpleBooleanProperty(true) {
        @Override
        protected void invalidated() {
          if (!getValue()) {
            activityText.requestFocus();
          }
        }
      };

  private final SystemClock clock = new SystemClock();
  private final PeriodCheck periodCheck = new PeriodCheck();
  private final TrayIconController trayIconController = new TrayIconController();
  private PreferencesViewController preferencesViewController;

  private LocalDateTime timestamp;

  @SneakyThrows
  public static MainViewController create(Stage stage) {
    var location = MainViewController.class.getResource("MainView.fxml");
    var resources = ResourceBundle.getBundle("ActivitySampling");
    var loader = new FXMLLoader(location, resources);
    loader.setRoot(stage);
    loader.load();
    return loader.getController();
  }

  @FXML
  private void initialize() {
    initMacOS();
    bindActivityForm();
    bindPreferenceViewController();
    bindTrayIconController();
  }

  private void initMacOS() {
    if (System.getProperty("os.name").toLowerCase().contains("mac")) {
      menuBar.setUseSystemMenuBar(true);
      quitSeparatorMenuItem.setVisible(false);
      quitMenuItem.setVisible(false);
    }
  }

  private void bindActivityForm() {
    activityText.disableProperty().bind(activityFormDisabled);
    tagsText.disableProperty().bind(activityFormDisabled);
    logButton
        .disableProperty()
        .bind(activityFormDisabled.or(activityText.textProperty().isEmpty()));

    clock.setOnTick(periodCheck::check);
    periodCheck.setOnRemainingTimeChanged(this::handleRemainingTimeChanged);
    periodCheck.setOnPeriodEnded(this::handlePeriodEnded);
  }

  private void bindPreferenceViewController() {
    preferencesViewController = PreferencesViewController.create(stage);
    preferencesViewController
        .periodDurationProperty()
        .addListener(
            observable ->
                onChangePeriodDurationCommand.accept(
                    new ChangePeriodDurationCommand(
                        preferencesViewController.getPeriodDuration())));
    preferencesViewController
        .activityLogFileProperty()
        .addListener(
            observable ->
                onChangeActivityLogFileCommand.accept(
                    new ChangeActivityLogFileCommand(
                        preferencesViewController.getActivityLogFile())));
  }

  private void bindTrayIconController() {
    stage.setOnCloseRequest(e -> trayIconController.hide());
    trayIconController.setOnLogActivitySelected(this::logActivity);
  }

  private void handleRemainingTimeChanged(Duration remaining) {
    Platform.runLater(
        () -> {
          var durationStringConverter = new DurationStringConverter();
          remainingTimeLabel.setText(durationStringConverter.toString(remaining));

          var remainingSeconds = (double) remaining.getSeconds();
          var totalSeconds = (double) periodCheck.getPeriod().getSeconds();
          var progress = 1 - remainingSeconds / totalSeconds;
          progressBar.setProgress(progress);
        });
  }

  private void handlePeriodEnded(LocalDateTime timestamp) {
    this.timestamp = timestamp;
    Platform.runLater(
        () -> {
          activityFormDisabled.set(false);
          activityText.requestFocus();
        });
    trayIconController.show();
  }

  public void run() {
    stage.show();
    onSettingsQuery.accept(new SettingsQuery());
    onActivityLogQuery.accept(new ActivityLogQuery());
    onRecentActivitiesQuery.accept(new RecentActivitiesQuery());
    clock.run();
  }

  public void display(ActivityLogQueryResult result) {
    System.out.println(result);
    Platform.runLater(
        () -> {
          var activityLogRenderer = new ActivityLogRenderer();
          var log = activityLogRenderer.render(result.activities());
          activityLogText.setText(log);
          activityLogText.setScrollTop(Double.MAX_VALUE);
        });
  }

  public void display(RecentActivitiesQueryResult result) {
    System.out.println(result);
    Platform.runLater(
        () -> {
          var menuItems =
              result.activities().stream()
                  .map(this::createActivityMenuItem)
                  .collect(Collectors.toList());
          logButton.getItems().setAll(menuItems);

          if (!result.activities().isEmpty()) {
            var lastActivity = result.activities().get(0);
            activityText.setText(lastActivity.activity());

            var tagsStringConverter = new TagsStringConverter();
            tagsText.setText(tagsStringConverter.toString(lastActivity.tags()));
          }
        });
    trayIconController.setRecentActivities(result.activities());
  }

  private MenuItem createActivityMenuItem(Activity it) {
    var menuItem = new MenuItem(new ActivityStringConverter().toString(it));
    menuItem.setOnAction(e -> handleLogActivity(it.activity(), it.tags()));
    return menuItem;
  }

  public void display(SettingsQueryResult result) {
    System.out.println(result);
    Platform.runLater(
        () -> {
          periodCheck.setPeriod(result.periodDuration());
          preferencesViewController.setPeriodDuration(result.periodDuration());
          preferencesViewController.setActivityLogFile(result.activityLogFile());
        });
  }

  @FXML
  private void openPreferences() {
    preferencesViewController.run();
  }

  @FXML
  private void quit() {
    Platform.exit();
  }

  @FXML
  private void openInfo() {
    var infoView = InfoView.create(stage);
    infoView.run();
  }

  @FXML
  private void logActivity() {
    var tags = new TagsStringConverter().fromString(tagsText.getText());
    handleLogActivity(activityText.getText(), tags);
  }

  private void logActivity(Activity activity) {
    handleLogActivity(activity.activity(), activity.tags());
  }

  private void handleLogActivity(String activity, List<String> tags) {
    onLogActivityCommand.accept(
        new LogActivityCommand(timestamp, periodCheck.getPeriod(), activity, tags));
    activityFormDisabled.set(true);
    trayIconController.hide();
  }
}
