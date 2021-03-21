/*
 * Activity Sampling - Frontend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.frontend;

import de.muspellheim.activitysampling.contract.MessageHandling;
import de.muspellheim.activitysampling.contract.data.Activity;
import de.muspellheim.activitysampling.contract.messages.commands.ChangeActivityLogFileCommand;
import de.muspellheim.activitysampling.contract.messages.commands.ChangePeriodDurationCommand;
import de.muspellheim.activitysampling.contract.messages.commands.LogActivityCommand;
import de.muspellheim.activitysampling.contract.messages.queries.ActivityLogQuery;
import de.muspellheim.activitysampling.contract.messages.queries.PreferencesQuery;
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
  private final ReadOnlyBooleanWrapper formDisabled =
      new ReadOnlyBooleanWrapper(true) {
        @Override
        protected void invalidated() {
          System.out.println("formDisabled=" + getValue());
        }
      };
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
          messageHandling.handle(command);
          startTime = null;
        }
      };

  private final StringProperty activityLog = new SimpleStringProperty("");
  private final StringProperty activityLogFile =
      new SimpleStringProperty() {
        @Override
        protected void invalidated() {
          var command = new ChangeActivityLogFileCommand(Paths.get(getValue()));
          messageHandling.handle(command);
        }
      };

  private final MessageHandling messageHandling;

  private LocalDateTime startTime;
  private LocalDateTime endTime;

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

  public void loadPreferences() {
    var result = messageHandling.handle(new PreferencesQuery());
    periodDuration.setValue(result.getPeriodDuration());
    activityLogFile.setValue(result.getActivityLogFile().toString());
  }

  public void reloadActivityLog() {
    var result = messageHandling.handle(new ActivityLogQuery());
    updateRecentActivities(result.getRecent());
    updateActivityLog(result.getLog());
  }

  private void updateRecentActivities(List<Activity> recent) {
    var stringConverter = new ActivityStringConverter();
    var activities = recent.stream().map(stringConverter::toString).collect(Collectors.toList());
    recentActivities.setAll(activities);
    if (!recent.isEmpty()) {
      var lastActivity = recent.get(0);
      activity.set(lastActivity.getActivity());
      tags.set(String.join(", ", lastActivity.getTags()));
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
        logBuilder.append(dateFormatter.format(activity.getTimestamp()));
        logBuilder.append("\n");
      } else {
        var lastActivity = log.get(i - 1);
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

  public void logActivity(String activity) {
    var stringConverter = new ActivityStringConverter();
    var a = stringConverter.fromString(activity);
    messageHandling.handle(
        new LogActivityCommand(endTime, periodDuration.get(), a.getActivity(), a.getTags()));
    formDisabled.set(true);
    reloadActivityLog();
  }
}
