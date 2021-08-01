/*
 * Activity Sampling - Frontend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.frontend;

import de.muspellheim.activitysampling.contract.data.Activity;
import de.muspellheim.activitysampling.contract.data.ActivityTemplate;
import de.muspellheim.activitysampling.contract.messages.commands.LogActivityCommand;
import de.muspellheim.activitysampling.contract.messages.queries.ActivityLogQueryResult;
import de.muspellheim.activitysampling.contract.messages.queries.PreferencesQueryResult;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.function.Consumer;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.Getter;
import lombok.Setter;

class MainWindowModel {
  @Getter @Setter private Consumer<LogActivityCommand> onLogActivityCommand;
  @Getter @Setter private Runnable onPeriodEnded;

  private Duration periodDuration = Duration.ZERO;
  private LocalDateTime periodStart;
  private LocalDateTime periodEnd;

  private final StringProperty activity = new SimpleStringProperty("");

  final String getActivity() {
    return activity.get();
  }

  final void setActivity(String value) {
    activity.set(value);
  }

  final StringProperty activityProperty() {
    return activity;
  }

  private final ReadOnlyObjectWrapper<List<ActivityTemplate>> recentActivities =
      new ReadOnlyObjectWrapper<>(List.of());

  final List<ActivityTemplate> getRecentActivities() {
    return recentActivities.get();
  }

  final ReadOnlyObjectProperty<List<ActivityTemplate>> recentActivitiesProperty() {
    return recentActivities.getReadOnlyProperty();
  }

  private final ObjectProperty<List<String>> tags = new SimpleObjectProperty<>(List.of());

  final List<String> getTags() {
    return tags.get();
  }

  final void setTags(List<String> value) {
    tags.set(value);
  }

  final ObjectProperty<List<String>> tagsProperty() {
    return tags;
  }

  private final ReadOnlyObjectWrapper<List<String>> recentTags =
      new ReadOnlyObjectWrapper<>(List.of());

  final List<String> getRecentTags() {
    return recentTags.get();
  }

  final ReadOnlyObjectProperty<List<String>> recentTagsProperty() {
    return recentTags.getReadOnlyProperty();
  }

  private final ReadOnlyBooleanWrapper formDisabled = new ReadOnlyBooleanWrapper(true);

  final boolean isFormDisabled() {
    return formDisabled.get();
  }

  final ReadOnlyBooleanProperty formDisabledProperty() {
    return formDisabled.getReadOnlyProperty();
  }

  private final BooleanBinding addTagButtonDisabled =
      formDisabled.or(recentTags.isEqualTo(List.of()));

  final boolean isAddTagButtonDisabled() {
    return addTagButtonDisabled.get();
  }

  final BooleanBinding addTagButtonDisabledBinding() {
    return addTagButtonDisabled;
  }

  private final BooleanBinding logButtonDisabled = formDisabled.or(activity.isEmpty());

  final boolean isLogButtonDisabled() {
    return logButtonDisabled.get();
  }

  final BooleanBinding logButtonDisabledBinding() {
    return logButtonDisabled;
  }

  private final ReadOnlyBooleanWrapper trayIconVisible = new ReadOnlyBooleanWrapper(false);

  final boolean isTrayIconVisible() {
    return trayIconVisible.get();
  }

  final ReadOnlyBooleanProperty trayIconVisibleProperty() {
    return trayIconVisible.getReadOnlyProperty();
  }

  private final ReadOnlyObjectWrapper<LocalTime> remainingTime =
      new ReadOnlyObjectWrapper<>(LocalTime.of(0, 20));

  final LocalTime getRemainingTime() {
    return remainingTime.get();
  }

  final ReadOnlyObjectProperty<LocalTime> remainingTimeProperty() {
    return remainingTime.getReadOnlyProperty();
  }

  private final DoubleBinding periodProgress =
      Bindings.createDoubleBinding(
          () -> {
            var remainingSeconds = (double) remainingTime.get().toSecondOfDay();
            var totalSeconds = (double) periodDuration.getSeconds();
            if (totalSeconds == 0) {
              return 0.0;
            }
            return 1 - remainingSeconds / totalSeconds;
          },
          remainingTime);

  final double getPeriodProgress() {
    return periodProgress.get();
  }

  final DoubleBinding periodProgressBinding() {
    return periodProgress;
  }

  private final ReadOnlyStringWrapper log = new ReadOnlyStringWrapper("");

  final String getLog() {
    return log.get();
  }

  final ReadOnlyStringProperty logProperty() {
    return log.getReadOnlyProperty();
  }

  void display(PreferencesQueryResult result) {
    periodDuration = result.periodDuration();
    periodStart = null;
  }

  void display(ActivityLogQueryResult result) {
    activity.set(result.last().activity());
    recentActivities.set(result.recent());
    tags.set(result.last().tags());
    recentTags.set(result.recentTags());
    updateLog(result.log());
  }

  private void updateLog(List<Activity> activities) {
    var dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL);
    var timeFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT);
    var logBuilder = new StringBuilder();
    var tagsConverter = new TagsStringConverter();
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
      String activityText = activity.activity();
      if (!activity.tags().isEmpty()) {
        activityText = "[" + tagsConverter.toString(activity.tags()) + "] " + activityText;
      }
      logBuilder.append(activityText);
      logBuilder.append("\n");
    }
    log.set(logBuilder.toString());
  }

  void progressPeriod(LocalDateTime timestamp) {
    if (periodStart == null) {
      periodStart = timestamp;
      remainingTime.set(LocalTime.ofSecondOfDay(periodDuration.getSeconds()));
      return;
    }

    var elapsed = Duration.between(periodStart, timestamp);
    var remaining = periodDuration.minus(elapsed);
    if (remaining.toSeconds() <= 0) {
      remainingTime.set(LocalTime.MIN);
      periodEnd = timestamp;
      periodStart = null;
      formDisabled.set(false);
      trayIconVisible.set(true);
      onPeriodEnded.run();
    } else {
      remainingTime.set(LocalTime.ofSecondOfDay(remaining.getSeconds()));
    }
  }

  void addTag(String tag) {
    var s = new LinkedHashSet<>(tags.get());
    s.add(tag);
    tags.set(List.copyOf(s));
  }

  void logActivity(ActivityTemplate template) {
    activity.set(template.activity());
    tags.set(template.tags());
    logActivity();
  }

  void logActivity() {
    formDisabled.set(true);
    trayIconVisible.set(false);
    var command = new LogActivityCommand(periodEnd, periodDuration, activity.get(), tags.get());
    onLogActivityCommand.accept(command);
  }

  void dispose() {
    trayIconVisible.set(false);
  }
}
