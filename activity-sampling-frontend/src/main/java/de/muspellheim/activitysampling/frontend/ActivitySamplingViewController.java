/*
 * Activity Sampling - Frontend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.frontend;

import de.muspellheim.activitysampling.contract.data.Activity;
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

public class ActivitySamplingViewController {
  @Getter @Setter private Runnable onOpenPreferences;
  @Getter @Setter private Runnable onOpenAbout;
  @Getter @Setter private Consumer<LogActivityCommand> onLogActivityCommand;
  @Getter @Setter private Consumer<PreferencesQuery> onPreferencesQuery;
  @Getter @Setter private Consumer<ActivityLogQuery> onActivityLogQuery;

  @FXML private Stage stage;
  @FXML private MenuBar menuBar;
  @FXML private SeparatorMenuItem quitSeparator;
  @FXML private MenuItem quit;
  @FXML private TextField activity;
  @FXML private TextField tags;
  @FXML private SplitMenuButton log;
  @FXML private Label progressText;
  @FXML private ProgressBar progressBar;
  @FXML private TextArea activityLog;

  private final BooleanProperty formDisabled = new SimpleBooleanProperty(true);

  private final SystemClock clock = new SystemClock();
  private final PeriodCheck periodCheck = new PeriodCheck();
  private final AppTrayIcon trayIcon = new AppTrayIcon();

  private Duration period;
  private LocalDateTime timestamp;

  public static ActivitySamplingViewController create(Stage stage) {
    try {
      var location = ActivitySamplingViewController.class.getResource("ActivitySamplingView.fxml");
      var loader = new FXMLLoader(location);
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
                  menuItem.setOnAction(e -> logActivity(it));
                  return menuItem;
                })
            .collect(Collectors.toList());
    Platform.runLater(
        () -> {
          log.getItems().setAll(menuItems);

          if (!recentActivities.isEmpty()) {
            var lastActivity = recentActivities.get(0);
            activity.setText(lastActivity.activity());
            tags.setText(String.join(", ", lastActivity.tags()));
          }
        });
  }

  private void updateActivityLog(List<Activity> log) {
    var activityLogRenderer = new ActivityLogRenderer();
    var logText = activityLogRenderer.render(log);
    activityLog.setText(logText);
    activityLog.setScrollTop(Double.MAX_VALUE);
  }

  private void updateTrayIcon(List<Activity> recent) {
    trayIcon.display(recent);
  }

  @FXML
  private void initialize() {
    initMac();
    bindActivityForm();
    bindTrayIcon();
  }

  private void initMac() {
    if (System.getProperty("os.name").toLowerCase().contains("mac")) {
      menuBar.setUseSystemMenuBar(true);
      quitSeparator.setVisible(false);
      quit.setVisible(false);
    }
  }

  private void bindActivityForm() {
    activity.disableProperty().bind(formDisabled);
    tags.disableProperty().bind(formDisabled);
    log.disableProperty().bind(formDisabled.or(activity.textProperty().isEmpty()));

    var durationStringConverter = new DurationStringConverter();
    periodCheck.setOnPeriodStarted(
        it -> {
          period = it;
          Platform.runLater(
              () -> {
                progressText.setText(durationStringConverter.toString(period));
                progressBar.setProgress(0.0);
              });
        });
    periodCheck.setOnPeriodProgressed(
        it ->
            Platform.runLater(
                () -> {
                  var remainingTime = period.minus(it);
                  progressText.setText(durationStringConverter.toString(remainingTime));
                  var progress = (double) it.getSeconds() / period.getSeconds();
                  progressBar.setProgress(progress);
                }));
    periodCheck.setOnPeriodEnded(
        it -> {
          timestamp = it;
          Platform.runLater(
              () -> {
                formDisabled.set(false);
                progressText.setText(durationStringConverter.toString(Duration.ZERO));
                progressBar.setProgress(1.0);
              });
          trayIcon.show();
        });

    clock.setOnTick(periodCheck::check);
  }

  private void bindTrayIcon() {
    trayIcon.setOnActivitySelected(this::logActivity);
    Platform.runLater(() -> stage.setOnHiding(e -> trayIcon.hide()));
  }

  @FXML
  private void handlePreferences() {
    onOpenPreferences.run();
  }

  @FXML
  private void handleQuit() {
    Platform.exit();
  }

  @FXML
  private void handleAbout() {
    onOpenAbout.run();
  }

  @FXML
  private void handleLogActivity() {
    var tagList = new TagsStringConverter().fromString(tags.getText());
    var a = new Activity("", LocalDateTime.now(), Duration.ZERO, activity.getText(), tagList);
    logActivity(a);
  }

  private void logActivity(Activity activity) {
    formDisabled.set(true);
    trayIcon.hide();

    var command = new LogActivityCommand(timestamp, period, activity.activity(), activity.tags());
    onLogActivityCommand.accept(command);
  }
}
