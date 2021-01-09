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
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
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

  @FXML private MenuBar menuBar;
  @FXML private TextField activity;
  @FXML private TextField tags;
  @FXML private SplitMenuButton log;
  @FXML private Label progressText;
  @FXML private ProgressBar progressBar;
  @FXML private TextArea activityLog;

  private final ReadOnlyBooleanWrapper formDisabled = new ReadOnlyBooleanWrapper(true);

  private final SystemClock clock = new SystemClock();
  private final PeriodCheck periodCheck = new PeriodCheck();
  private final AppTrayIcon trayIcon = new AppTrayIcon();

  private Duration period;
  private LocalDateTime timestamp;

  public static ActivitySamplingViewController create(Stage stage, boolean useSystemMenuBar) {
    var factory = new ViewControllerFactory(ActivitySamplingViewController.class);

    var scene = new Scene(factory.getView());
    stage.setScene(scene);
    stage.setTitle("Activity Sampling");
    stage.setMinWidth(240);
    stage.setMinHeight(420);

    ActivitySamplingViewController controller = factory.getController();
    controller.menuBar.setUseSystemMenuBar(useSystemMenuBar);
    return controller;
  }

  public final ReadOnlyBooleanProperty formDisabledProperty() {
    return formDisabled.getReadOnlyProperty();
  }

  public final boolean isFormDisabled() {
    return formDisabled.get();
  }

  private Stage getWindow() {
    return (Stage) activity.getScene().getWindow();
  }

  public void run() {
    getWindow().show();
    onPreferencesQuery.accept(new PreferencesQuery());
    onActivityLogQuery.accept(new ActivityLogQuery());
    clock.run();
  }

  public void display(PreferencesQueryResult result) {
    periodCheck.setPeriod(result.getPeriodDuration());
  }

  public void display(ActivityLogQueryResult result) {
    updateForm(result.getRecent());
    updateActivityLog(result.getLog());
    updateTrayIcon(result.getRecent());
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
            activity.setText(lastActivity.getActivity());
            tags.setText(String.join(", ", lastActivity.getTags()));
          }
        });
  }

  private void updateActivityLog(List<Activity> log) {
    var dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL);
    var timeFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT);
    var stringConverter = new ActivityStringConverter();
    var logBuilder = new StringBuilder();
    for (int i = 0; i < log.size(); i++) {
      Activity activity = log.get(i);
      if (i == 0) {
        logBuilder.append(dateFormatter.format(activity.getTimestamp()));
        logBuilder.append("\n");
      } else {
        Activity lastActivity = log.get(i - 1);
        if (!lastActivity
            .getTimestamp()
            .toLocalDate()
            .equals(activity.getTimestamp().toLocalDate())) {
          logBuilder.append(dateFormatter.format(activity.getTimestamp()));
          logBuilder.append("\n");
        }
      }

      logBuilder.append(timeFormatter.format(activity.getTimestamp()));
      logBuilder.append(" - ");
      logBuilder.append(stringConverter.toString(activity));
      logBuilder.append("\n");
    }
    activityLog.setText(logBuilder.toString());
    Platform.runLater(() -> activityLog.setScrollTop(Double.MAX_VALUE));
  }

  private void updateTrayIcon(List<Activity> recent) {
    trayIcon.display(recent);
  }

  @FXML
  private void initialize() {
    initializePeriodProgress();
    initializeTrayIcon();
  }

  private void initializePeriodProgress() {
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

    clock.setOnTick(it -> periodCheck.check(it));
  }

  private void initializeTrayIcon() {
    trayIcon.setOnActivitySelected(it -> logActivity(it));
    Platform.runLater(() -> getWindow().setOnHiding(e -> trayIcon.hide()));
  }

  @FXML
  private void handlePreferences() {
    onOpenPreferences.run();
  }

  @FXML
  private void handleExit() {
    Platform.exit();
  }

  @FXML
  private void handleAbout() {
    onOpenAbout.run();
  }

  @FXML
  private void handleLogActivity() {
    var a =
        new Activity(
            "",
            LocalDateTime.now(),
            Duration.ZERO,
            activity.getText(),
            List.of(tags.getText().split(",")));
    logActivity(a);
  }

  private void logActivity(Activity activity) {
    formDisabled.set(true);
    trayIcon.hide();

    var command =
        new LogActivityCommand(timestamp, period, activity.getActivity(), activity.getTags());
    onLogActivityCommand.accept(command);
  }
}
