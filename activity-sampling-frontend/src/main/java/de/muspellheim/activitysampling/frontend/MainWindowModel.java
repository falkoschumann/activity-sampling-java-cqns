/*
 * Activity Sampling - Frontend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.frontend;

import de.muspellheim.activitysampling.contract.data.Activity;
import de.muspellheim.activitysampling.contract.data.ActivityTemplate;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.Getter;

class MainWindowModel {
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

  private final ObjectProperty<List<String>> recentTags = new SimpleObjectProperty<>(List.of());

  final List<String> getRecentTags() {
    return recentTags.get();
  }

  final void setRecentTags(List<String> value) {
    recentTags.set(value);
  }

  final ObjectProperty<List<String>> recentTagsProperty() {
    return recentTags;
  }

  private final BooleanProperty formDisabled = new SimpleBooleanProperty(true);

  final boolean isFormDisabled() {
    return formDisabled.get();
  }

  final void setFormDisabled(boolean value) {
    formDisabled.set(value);
  }

  final BooleanProperty formDisabledProperty() {
    return formDisabled;
  }

  final BooleanBinding tagNotAddable =
      formDisabledProperty().or(recentTagsProperty().isEqualTo(List.of()));

  final BooleanBinding tagNotAddableBinding() {
    return tagNotAddable;
  }

  private final ObjectProperty<List<ActivityTemplate>> recent =
      new SimpleObjectProperty<>(List.of());

  final List<ActivityTemplate> getRecent() {
    return recent.get();
  }

  final void setRecent(List<ActivityTemplate> value) {
    recent.set(value);
  }

  final ObjectProperty<List<ActivityTemplate>> recentProperty() {
    return recent;
  }

  final BooleanBinding formUnsubmittable = formDisabledProperty().or(activityProperty().isEmpty());

  final BooleanBinding formUnsubmittableBinding() {
    return formUnsubmittable;
  }

  private final ObjectProperty<Duration> periodDuration =
      new SimpleObjectProperty<>(Duration.ZERO) {
        @Override
        protected void invalidated() {
          periodStart = null;
        }
      };

  final Duration getPeriodDuration() {
    return periodDuration.get();
  }

  final void setPeriodDuration(Duration value) {
    periodDuration.set(value);
  }

  final ObjectProperty<Duration> periodDurationProperty() {
    return periodDuration;
  }

  private LocalDateTime periodStart;
  @Getter private LocalDateTime periodEnd;

  private final ObjectProperty<Duration> remainingTime =
      new SimpleObjectProperty<>(Duration.ofMinutes(20));

  final Duration getRemainingTime() {
    return remainingTime.get();
  }

  final void setRemainingTime(Duration value) {
    remainingTime.set(value);
  }

  final ObjectProperty<Duration> remainingTimeProperty() {
    return remainingTime;
  }

  final DoubleBinding periodProgress =
      Bindings.createDoubleBinding(
          () -> {
            var remainingSeconds = (double) getRemainingTime().getSeconds();
            var totalSeconds = (double) getPeriodDuration().getSeconds();
            return 1 - remainingSeconds / totalSeconds;
          },
          remainingTimeProperty());

  final DoubleBinding periodProgressBinding() {
    return periodProgress;
  }

  void addTag(String tag) {
    var tags = new LinkedHashSet<>(getTags());
    tags.add(tag);
    setTags(List.copyOf(tags));
  }

  void progressPeriod(LocalDateTime timestamp) {
    if (periodStart == null) {
      periodStart = timestamp;
      setRemainingTime(getPeriodDuration());
      return;
    }

    var elapsed = Duration.between(periodStart, timestamp);
    var remaining = getPeriodDuration().minus(elapsed);
    if (remaining.toSeconds() <= 0) {
      setRemainingTime(Duration.ZERO);
      periodEnd = timestamp;
      periodStart = null;
      setFormDisabled(false);
    } else {
      setRemainingTime(remaining);
    }
  }

  private final ObjectProperty<List<Activity>> log = new SimpleObjectProperty<>(List.of());

  final List<Activity> getLog() {
    return log.get();
  }

  final void setLog(List<Activity> value) {
    log.set(value);
  }

  final ObjectProperty<List<Activity>> logProperty() {
    return log;
  }
}
