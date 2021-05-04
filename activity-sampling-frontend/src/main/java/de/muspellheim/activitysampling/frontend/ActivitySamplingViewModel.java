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
import de.muspellheim.activitysampling.contract.messages.queries.SettingsQuery;
import de.muspellheim.activitysampling.contract.messages.queries.SettingsQueryResult;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ActivitySamplingViewModel {
  private final ReadOnlyBooleanWrapper formDisabled = new ReadOnlyBooleanWrapper(true);
  private final StringProperty activity = new SimpleStringProperty("");
  private final StringProperty tags = new SimpleStringProperty("");
  private final ObservableList<String> recentActivities = FXCollections.observableArrayList();

  private final StringProperty remainingTime = new SimpleStringProperty("20:00");
  private final DoubleProperty progress = new SimpleDoubleProperty();
  private final ObjectProperty<Duration> periodDuration =
      new SimpleObjectProperty<>() {
        @Override
        protected void invalidated() {
          var command = new ChangePeriodDurationCommand(getValue());
          onChangePeriodDurationCommand.accept(command);
          startTime = null;
        }
      };

  private final StringProperty activityLog = new SimpleStringProperty("");
  private final StringProperty activityLogFile =
      new SimpleStringProperty() {
        @Override
        protected void invalidated() {
          var command = new ChangeActivityLogFileCommand(Paths.get(getValue()));
          onChangeActivityLogFileCommand.accept(command);
        }
      };

  private LocalDateTime startTime;
  private LocalDateTime endTime;

  public ReadOnlyBooleanWrapper formDisabledProperty() {
    return formDisabled;
  }

  public StringProperty activityProperty() {
    return activity;
  }

  public StringProperty tagsProperty() {
    return tags;
  }

  public ObservableList<String> getRecentActivities() {
    return recentActivities;
  }

  public StringProperty remainingTimeProperty() {
    return remainingTime;
  }

  public DoubleProperty progressProperty() {
    return progress;
  }

  public ObjectProperty<Duration> periodDurationProperty() {
    return periodDuration;
  }

  public StringProperty activityLogProperty() {
    return activityLog;
  }

  public StringProperty activityLogFileProperty() {
    return activityLogFile;
  }

  public void display(ActivityLogQueryResult result) {
    updateRecentActivities(result.recent());
    updateActivityLog(result.activities());
  }

  public void display(SettingsQueryResult result) {
    periodDuration.setValue(result.periodDuration());
    activityLogFile.setValue(result.activityLogFile().toString());
  }

  public void loadPreferences() {
    onSettingsQuery.accept(new SettingsQuery());
  }

  public void loadActivityLog() {
    onActivityLogQuery.accept(new ActivityLogQuery());
  }

  private void updateRecentActivities(List<Activity> recent) {
    var stringConverter = new ActivityStringConverter();
    var activities = recent.stream().map(stringConverter::toString).collect(Collectors.toList());
    recentActivities.setAll(activities);
    if (!recent.isEmpty()) {
      var lastActivity = recent.get(0);
      activity.set(lastActivity.activity());
      tags.set(String.join(", ", lastActivity.tags()));
    }
  }

  private void updateActivityLog(List<Activity> log) {
    var stringConverter = new ActivityStringConverter();
    var dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL);
    var timeFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT);
    var logBuilder = new StringBuilder();
    for (int i = 0; i < log.size(); i++) {
      var activity = log.get(i);
      if (i == 0) {
        logBuilder.append(dateFormatter.format(activity.timestamp()));
        logBuilder.append("\n");
      } else {
        var lastActivity = log.get(i - 1);
        if (!lastActivity.timestamp().toLocalDate().equals(activity.timestamp().toLocalDate())) {
          logBuilder.append(dateFormatter.format(activity.timestamp()));
          logBuilder.append("\n");
        }
      }

      logBuilder.append(timeFormatter.format(activity.timestamp()));
      logBuilder.append(" - ");
      logBuilder.append(stringConverter.toString(activity));
      logBuilder.append("\n");
    }
    activityLog.set(logBuilder.toString());
  }

  public void clockTicked(LocalDateTime timestamp) {
    Function<Duration, String> stringConverter =
        (duration) ->
            String.format("%1$02d:%2$02d", duration.toMinutesPart(), duration.toSecondsPart());

    if (startTime == null) {
      startTime = timestamp;
      remainingTime.set(stringConverter.apply(periodDuration.get()));
      progress.set(0.0);
      return;
    }

    var elapsedTime = Duration.between(startTime, timestamp);
    var remainingTime = periodDuration.get().minus(elapsedTime);
    if (remainingTime.toSeconds() <= 0) {
      endTime = timestamp;
      formDisabled.set(false);
      this.remainingTime.set(stringConverter.apply(Duration.ZERO));
      progress.set(1.0);
      startTime = null;
    } else {
      this.remainingTime.set(stringConverter.apply(remainingTime));
      progress.set(1.0 - (double) remainingTime.toSeconds() / periodDuration.get().toSeconds());
    }
  }

  public void logActivity() {
    logActivity("[" + tags.get() + "] " + activity.get());
  }

  public void logActivity(String s) {
    var stringConverter = new ActivityStringConverter();
    var activity = stringConverter.fromString(s);
    var command =
        new LogActivityCommand(endTime, periodDuration.get(), activity.activity(), activity.tags());
    onLogActivityCommand.accept(command);
    formDisabled.set(true);
  }
}
