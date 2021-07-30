/*
 * Activity Sampling - Frontend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.frontend;

import de.muspellheim.activitysampling.contract.data.Activity;
import de.muspellheim.activitysampling.contract.data.ActivityTemplate;
import de.muspellheim.activitysampling.contract.messages.commands.LogActivityCommand;
import de.muspellheim.activitysampling.contract.messages.queries.ActivityLogQueryResult;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.function.Consumer;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.Getter;
import lombok.Setter;

class MainWindowModel {
  @Getter @Setter private Consumer<LogActivityCommand> onLogActivityCommand;
  @Getter @Setter private Runnable onPeriodEnded;

  private final StringProperty activity = new SimpleStringProperty("");

  final String getActivity() {
    return activity.get();
  }

  final StringProperty activityProperty() {
    return activity;
  }

  private final ObjectProperty<List<String>> tags = new SimpleObjectProperty<>(List.of());

  final List<String> getTags() {
    return tags.get();
  }

  final ObjectProperty<List<String>> tagsProperty() {
    return tags;
  }

  private final ObjectProperty<List<String>> recentTags = new SimpleObjectProperty<>(List.of());

  final List<String> getRecentTags() {
    return recentTags.get();
  }

  final ObjectProperty<List<String>> recentTagsProperty() {
    return recentTags;
  }

  private final ObjectProperty<List<ActivityTemplate>> recentActivities =
      new SimpleObjectProperty<>(List.of());

  final List<ActivityTemplate> getRecentActivities() {
    return recentActivities.get();
  }

  final ObjectProperty<List<ActivityTemplate>> recentActivitiesProperty() {
    return recentActivities;
  }

  private final BooleanProperty formDisabled = new SimpleBooleanProperty(true);

  final boolean isFormDisabled() {
    return formDisabled.get();
  }

  final BooleanProperty formDisabledProperty() {
    return formDisabled;
  }

  final BooleanBinding addTagButtonDisabled = formDisabled.or(recentTags.isEqualTo(List.of()));

  final BooleanBinding addTagButtonDisabledBinding() {
    return addTagButtonDisabled;
  }

  final BooleanBinding logButtonDisabled = formDisabled.or(activity.isEmpty());

  final BooleanBinding logButtonDisabledBinding() {
    return logButtonDisabled;
  }

  private final ReadOnlyBooleanWrapper trayIconVisible = new ReadOnlyBooleanWrapper(false);

  final ReadOnlyBooleanProperty trayIconVisibleProperty() {
    return trayIconVisible.getReadOnlyProperty();
  }

  @Getter private Duration periodDuration = Duration.ZERO;

  final void setPeriodDuration(Duration value) {
    periodDuration = value;
    periodStart = null;
  }

  private LocalDateTime periodStart;
  @Getter private LocalDateTime periodEnd;

  private final ObjectProperty<Duration> remainingTime =
      new SimpleObjectProperty<>(Duration.ofMinutes(20));

  final Duration getRemainingTime() {
    return remainingTime.get();
  }

  final ObjectProperty<Duration> remainingTimeProperty() {
    return remainingTime;
  }

  final DoubleBinding periodProgress =
      Bindings.createDoubleBinding(
          () -> {
            var remainingSeconds = (double) remainingTime.get().getSeconds();
            var totalSeconds = (double) periodDuration.getSeconds();
            if (totalSeconds == 0) {
              return 0.0;
            }
            return 1 - remainingSeconds / totalSeconds;
          },
          remainingTime);

  final DoubleBinding periodProgressBinding() {
    return periodProgress;
  }

  void addTag(String tag) {
    var s = new LinkedHashSet<>(tags.get());
    s.add(tag);
    tags.set(List.copyOf(s));
  }

  void progressPeriod(LocalDateTime timestamp) {
    if (periodStart == null) {
      periodStart = timestamp;
      remainingTime.set(periodDuration);
      return;
    }

    var elapsed = Duration.between(periodStart, timestamp);
    var remaining = periodDuration.minus(elapsed);
    if (remaining.toSeconds() <= 0) {
      remainingTime.set(Duration.ZERO);
      periodEnd = timestamp;
      periodStart = null;
      formDisabled.set(false);
      trayIconVisible.set(true);
      onPeriodEnded.run();
    } else {
      remainingTime.set(remaining);
    }
  }

  private final ReadOnlyStringWrapper log = new ReadOnlyStringWrapper("");

  final String getLog() {
    return log.get();
  }

  final ReadOnlyStringProperty logProperty() {
    return log.getReadOnlyProperty();
  }

  void updateWith(ActivityLogQueryResult result) {
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

  void logActivity(ActivityTemplate template) {
    // TODO Schreibe Test
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
