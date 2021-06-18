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
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.regex.Pattern;
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
  @FXML private Label remainingTimeLabel;
  @FXML private ProgressBar progressBar;
  @FXML private TextArea activityLogText;

  private final BooleanProperty activityFormDisabled = new SimpleBooleanProperty(true);

  private final SystemClock clock = new SystemClock();
  private final PeriodCheck periodCheck = new PeriodCheck();
  private TrayIconViewController trayIconViewController;
  private PreferencesViewController preferencesViewController;

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
    var menuItems =
        recentActivities.stream()
            .map(
                it -> {
                  var menuItem = new MenuItem(activityToString(it));
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
            tagsText.setText(tagListToString(lastActivity.tags()));
          }
        });
  }

  private void updateActivityLog(List<Activity> log) {
    activityLogText.setText(activityLogToString(log));
    activityLogText.setScrollTop(Double.MAX_VALUE);
  }

  static String activityLogToString(List<Activity> activities) {
    var dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL);
    var timeFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT);
    var logBuilder = new StringBuilder();
    for (int i = 0; i < activities.size(); i++) {
      var activity = activities.get(i);
      if (i == 0) {
        logBuilder.append(dateFormatter.format(activity.timestamp()));
        logBuilder.append("\n");
      } else {
        var lastActivity = activities.get(i - 1);
        if (!lastActivity.timestamp().toLocalDate().equals(activity.timestamp().toLocalDate())) {
          logBuilder.append(dateFormatter.format(activity.timestamp()));
          logBuilder.append("\n");
        }
      }

      logBuilder.append(timeFormatter.format(activity.timestamp()));
      logBuilder.append(" - ");
      logBuilder.append(activityToString(activity));
      logBuilder.append("\n");
    }
    return logBuilder.toString();
  }

  private void updateTrayIcon(List<Activity> recent) {
    trayIconViewController.display(
        recent.stream().map(MainViewController::activityToString).toList());
  }

  static String activityToString(Activity activity) {
    String string = activity.activity();
    if (!activity.tags().isEmpty()) {
      string = "[" + tagListToString(activity.tags()) + "] " + string;
    }
    return string;
  }

  static String tagListToString(List<String> tagList) {
    return String.join(", ", tagList);
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

  private void initPreferenceViewController() {
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
    activityText.disableProperty().bind(activityFormDisabled);
    tagsText.disableProperty().bind(activityFormDisabled);
    logButton
        .disableProperty()
        .bind(activityFormDisabled.or(activityText.textProperty().isEmpty()));

    periodCheck.setOnRemainingTimeChanged(
        it ->
            Platform.runLater(
                () -> {
                  remainingTimeLabel.setText(durationToString(it));

                  // TODO Move progress calculation to PeriodCheck
                  var remainingSeconds = (double) it.getSeconds();
                  var totalSeconds = (double) periodCheck.getPeriod().getSeconds();
                  var progress = 1 - remainingSeconds / totalSeconds;
                  progressBar.setProgress(progress);
                }));
    periodCheck.setOnPeriodEnded(
        it -> {
          timestamp = it;
          Platform.runLater(
              () -> {
                activityFormDisabled.set(false);
                activityText.requestFocus();
              });
          trayIconViewController.show();
        });

    clock.setOnTick(periodCheck::check);
  }

  static String durationToString(Duration object) {
    return String.format(
        "%1$02d:%2$02d:%3$02d",
        object.toHoursPart(), object.toMinutesPart(), object.toSecondsPart());
  }

  private void initTrayIcon() {
    trayIconViewController = new TrayIconViewController();

    trayIconViewController.setOnActivitySelected(it -> handleLogActivity(parseActivity(it)));
    Platform.runLater(() -> stage.setOnHiding(e -> trayIconViewController.hide()));
  }

  static Activity parseActivity(String string) {
    var pattern = Pattern.compile("(\\[(.+)])?\\s*(.+)");
    var matcher = pattern.matcher(string);
    String activity;
    List<String> tags = List.of();
    if (matcher.find()) {
      activity = matcher.group(3);
      var tagsString = matcher.group(2);
      if (tagsString != null) {
        tags = parseTagList(tagsString);
      }
    } else {
      activity = string;
    }
    return new Activity("", LocalDateTime.now(), Duration.ZERO, activity, tags);
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
    handleLogActivity(
        new Activity(
            "",
            LocalDateTime.now(),
            Duration.ZERO,
            activityText.getText(),
            parseTagList(tagsText.getText())));
  }

  static List<String> parseTagList(String string) {
    if (string.isBlank()) {
      return Collections.emptyList();
    }

    return List.of(string.split(",")).stream().map(String::strip).collect(Collectors.toList());
  }

  private void handleLogActivity(Activity activity) {
    activityFormDisabled.set(true);
    trayIconViewController.hide();

    onLogActivityCommand.accept(
        new LogActivityCommand(
            timestamp, periodCheck.getPeriod(), activity.activity(), activity.tags()));
  }

  static class PeriodCheck {
    @Getter private Duration period = Duration.ofMinutes(20);
    @Getter @Setter Consumer<Duration> onRemainingTimeChanged;
    @Getter @Setter Consumer<LocalDateTime> onPeriodEnded;

    private LocalDateTime start;

    public void setPeriod(Duration period) {
      this.period = period;
      start = null;
    }

    public void check(LocalDateTime timestamp) {
      if (start == null) {
        start = timestamp;
        onRemainingTimeChanged.accept(period);
        return;
      }

      var elapsed = Duration.between(start, timestamp);
      var remaining = period.minus(elapsed);
      if (remaining.toSeconds() <= 0) {
        onRemainingTimeChanged.accept(Duration.ZERO);
        onPeriodEnded.accept(timestamp);
        start = null;
      } else {
        onRemainingTimeChanged.accept(remaining);
      }
    }
  }
}
