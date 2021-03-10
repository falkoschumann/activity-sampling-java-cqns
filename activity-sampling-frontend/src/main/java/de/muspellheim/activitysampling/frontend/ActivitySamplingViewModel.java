/*
 * Activity Sampling - Frontend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.frontend;

import de.muspellheim.activitysampling.contract.MessageHandling;
import de.muspellheim.activitysampling.contract.messages.commands.ChangeActivityLogFileCommand;
import de.muspellheim.activitysampling.contract.messages.commands.ChangePeriodDurationCommand;
import de.muspellheim.activitysampling.contract.messages.queries.ActivityLogQuery;
import de.muspellheim.activitysampling.contract.messages.queries.PreferencesQuery;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.function.Function;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ActivitySamplingViewModel {
  private final ReadOnlyBooleanWrapper formDisabled = new ReadOnlyBooleanWrapper(true);
  private final StringProperty activity = new SimpleStringProperty();
  private final StringProperty tags = new SimpleStringProperty();
  private final StringProperty activityLog = new SimpleStringProperty();
  private final StringProperty remainingTime = new SimpleStringProperty();
  private final DoubleProperty progress = new SimpleDoubleProperty();
  private final ObjectProperty<Duration> periodDuration =
      new SimpleObjectProperty<>() {
        @Override
        protected void invalidated() {
          var command = new ChangePeriodDurationCommand(getValue());
          messageHandling.handle(command);
          start = null;
        }
      };
  private final StringProperty activityLogFile =
      new SimpleStringProperty() {
        @Override
        protected void invalidated() {
          var command = new ChangeActivityLogFileCommand(Paths.get(getValue()));
          messageHandling.handle(command);
        }
      };

  private final MessageHandling messageHandling;

  private LocalDateTime start;
  private LocalDateTime timestamp;

  public ActivitySamplingViewModel(MessageHandling messageHandling) {
    this.messageHandling = messageHandling;
  }

  public ReadOnlyBooleanWrapper formDisabledProperty() {
    return formDisabled;
  }

  public StringProperty activityProperty() {
    return activity;
  }

  public StringProperty tagsProperty() {
    return tags;
  }

  public StringProperty activityLogProperty() {
    return activityLog;
  }

  public StringProperty remainingTimeProperty() {
    return remainingTime;
  }

  public DoubleProperty progressProperty() {
    return progress;
  }

  public ObjectProperty<Duration> periodDuration() {
    return periodDuration;
  }

  public StringProperty activityLogFile() {
    return activityLogFile;
  }

  public void loadPreferences() {
    var result = messageHandling.handle(new PreferencesQuery());
    periodDuration.setValue(result.getPeriodDuration());
    activityLogFile.setValue(result.getActivityLogFile().toString());
  }

  public void loadActivityLog() {
    var result = messageHandling.handle(new ActivityLogQuery());
  }

  public void clockTicked(LocalDateTime timestamp) {
    Function<Duration, String> stringConverter =
        (duration) ->
            String.format("%1$02d:%2$02d", duration.toMinutesPart(), duration.toSecondsPart());

    if (start == null) {
      start = timestamp;
      formDisabled.set(true);
      remainingTime.set(stringConverter.apply(periodDuration.get()));
      progress.set(0.0);
      return;
    }

    var elapsedTime = Duration.between(start, timestamp);
    var remainingTime = periodDuration.get().minus(elapsedTime);
    if (remainingTime.toSeconds() <= 0) {
      formDisabled.set(false);
      this.remainingTime.set(stringConverter.apply(Duration.ZERO));
      progress.set(1.0);
      start = null;
    } else {
      this.remainingTime.set(stringConverter.apply(remainingTime));
      progress.set((double) remainingTime.toSeconds() / periodDuration.get().toSeconds());
    }
  }

  public void logActivity() {
    /*
    var a =
      new Activity(
        "",
        LocalDateTime.now(),
        Duration.ZERO,
        activity.getValue(),
        List.of(tags.getValue().split(",")));
    logActivity(a);
    */
  }

  /*

  private class SystemClockTask extends TimerTask {
    @Override
    public void run() {
      var timestamp = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
      onTick.accept(timestamp);
    }
  }

  // TODO Clean up following code

  void check(LocalDateTime timestamp) {
    if (start == null) {
      start = timestamp;
      onPeriodStarted.accept(period);
      return;
    }

    var elapsedTime = Duration.between(start, timestamp);
    var remainingTime = period.minus(elapsedTime);
    if (remainingTime.toSeconds() <= 0) {
      onPeriodEnded.accept(timestamp);
      start = null;
    } else {
      onPeriodProgressed.accept(elapsedTime);
    }
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
        logButton.getItems().setAll(menuItems);

        if (!recentActivities.isEmpty()) {
          var lastActivity = recentActivities.get(0);
          activityText.setText(lastActivity.getActivity());
          tagsText.setText(String.join(", ", lastActivity.getTags()));
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

  private void logActivity(Activity activity) {
    formDisabled.set(true);
    trayIcon.hide();

    var command =
      new LogActivityCommand(timestamp, period, activity.getActivity(), activity.getTags());
    onLogActivityCommand.accept(command);
  }
  */
}
