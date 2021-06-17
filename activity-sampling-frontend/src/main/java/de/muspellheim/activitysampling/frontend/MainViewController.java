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
import de.muspellheim.activitysampling.contract.messages.queries.PreferencesQuery;
import de.muspellheim.activitysampling.contract.messages.queries.PreferencesQueryResult;
import java.io.IOException;
import java.io.UncheckedIOException;
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

public class MainViewController {
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
  @FXML private Label progressLabel;
  @FXML private ProgressBar progressBar;
  @FXML private TextArea activityLogText;

  private final BooleanProperty formDisabled = new SimpleBooleanProperty(true){
    @Override
    protected void invalidated() {
      if (!getValue()) {
        activityText.requestFocus();
      }
    }
  };

  private final SystemClock clock = new SystemClock();
  private final PeriodCheck periodCheck = new PeriodCheck();
  private final AppTrayIcon trayIcon = new AppTrayIcon();
  private PreferencesViewController preferencesViewController;

  private Duration period;
  private LocalDateTime timestamp;

  public static MainViewController create(Stage stage) {
    try {
      var location = MainViewController.class.getResource("MainView.fxml");
      var resources = ResourceBundle.getBundle("ActivitySampling");
      var loader = new FXMLLoader(location, resources);
      loader.setRoot(stage);
      loader.load();

      return loader.getController();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public void run() {
    stage.show();
    onPreferencesQuery.accept(new PreferencesQuery());
    onActivityLogQuery.accept(new ActivityLogQuery());
    clock.run();
  }

  public void display(PreferencesQueryResult result) {
    periodCheck.setPeriod(result.periodDuration());
    preferencesViewController.setPeriodDuration(result.periodDuration());
    preferencesViewController.setActivityLogFile(result.activityLogFile());
  }

  public void display(ActivityLogQueryResult result) {
    updateForm(result.recent());
    updateActivityLog(result.log());
    updateTrayIcon(result.recent());
  }

  private void updateForm(List<Activity> recentActivities) {
    var activityStringConverter = new ActivityStringConverter();
    var menuItems =
        recentActivities.stream()
            .map(
                it -> {
                  var menuItem = new MenuItem(activityStringConverter.toString(it));
                  menuItem.setOnAction(e -> handleLogActivity(it));
                  return menuItem;
                })
            .collect(Collectors.toList());
    Platform.runLater(
        () -> {
          logButton.getItems().setAll(menuItems);

          if (!recentActivities.isEmpty()) {
            var lastActivity = recentActivities.get(0);
            activityText.setText(lastActivity.activity());
            tagsText.setText(String.join(", ", lastActivity.tags()));
          }
        });
  }

  private void updateActivityLog(List<Activity> log) {
    var activityLogRenderer = new ActivityLogRenderer();
    var logText = activityLogRenderer.render(log);
    activityLogText.setText(logText);
    activityLogText.setScrollTop(Double.MAX_VALUE);
  }

  private void updateTrayIcon(List<Activity> recent) {
    trayIcon.display(recent);
  }

  @FXML
  private void initialize() {
    initMacOS();
    initPreferenceViewController();
    initActivityForm();
    initTrayIcon();
  }

  private void initMacOS() {
    if (System.getProperty("os.name").toLowerCase().contains("mac")) {
      menuBar.setUseSystemMenuBar(true);
      quitSeparatorMenuItem.setVisible(false);
      quitMenuItem.setVisible(false);
    }
  }

  private void initPreferenceViewController(){
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

  private void initActivityForm() {
    activityText.disableProperty().bind(formDisabled);
    tagsText.disableProperty().bind(formDisabled);
    logButton.disableProperty().bind(formDisabled.or(activityText.textProperty().isEmpty()));

    var durationStringConverter = new DurationStringConverter();
    periodCheck.setOnPeriodStarted(
        it -> {
          period = it;
          Platform.runLater(
              () -> {
                progressLabel.setText(durationStringConverter.toString(period));
                progressBar.setProgress(0.0);
              });
        });
    periodCheck.setOnPeriodProgressed(
        it ->
            Platform.runLater(
                () -> {
                  var remainingTime = period.minus(it);
                  progressLabel.setText(durationStringConverter.toString(remainingTime));
                  var progress = (double) it.getSeconds() / period.getSeconds();
                  progressBar.setProgress(progress);
                }));
    periodCheck.setOnPeriodEnded(
        it -> {
          timestamp = it;
          Platform.runLater(
              () -> {
                formDisabled.set(false);
                progressLabel.setText(durationStringConverter.toString(Duration.ZERO));
                progressBar.setProgress(1.0);
              });
          trayIcon.show();
        });

    clock.setOnTick(periodCheck::check);
  }

  private void initTrayIcon() {
    trayIcon.setOnActivitySelected(this::handleLogActivity);
    Platform.runLater(() -> stage.setOnHiding(e -> trayIcon.hide()));
  }

  @FXML
  private void handleOpenPreferences() {
    preferencesViewController.run();
  }

  @FXML
  private void handleQuit() {
    Platform.exit();
  }

  @FXML
  private void handleOpenAbout() {
    var aboutViewController = AboutViewController.create(stage);
    aboutViewController.run();
  }

  @FXML
  private void handleLogActivity() {
    var tagList = new TagsStringConverter().fromString(tagsText.getText());
    var a = new Activity("", LocalDateTime.now(), Duration.ZERO, activityText.getText(), tagList);
    handleLogActivity(a);
  }

  private void handleLogActivity(Activity activity) {
    formDisabled.set(true);
    trayIcon.hide();

    var command = new LogActivityCommand(timestamp, period, activity.activity(), activity.tags());
    onLogActivityCommand.accept(command);
  }
}
