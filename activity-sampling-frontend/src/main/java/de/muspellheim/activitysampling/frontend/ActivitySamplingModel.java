/*
 * Activity Sampling - Frontend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.frontend;

import de.muspellheim.activitysampling.contract.data.Activity;
import java.nio.file.Path;
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
import lombok.Setter;

class ActivitySamplingModel {
  boolean isRunningOnMac() {
    return System.getProperty("os.name").toLowerCase().contains("mac");
  }

  private final ObjectProperty<Duration> periodDuration =
      new SimpleObjectProperty<>(Duration.ofMinutes(20)) {
        @Override
        protected void invalidated() {
          periodStart = null;
        }
      };

  final Duration getPeriodDuration() {
    return periodDuration.get();
  }

  final void setPeriodDuration(Duration periodDuration) {
    this.periodDuration.set(periodDuration);
  }

  final ObjectProperty<Duration> periodDurationProperty() {
    return periodDuration;
  }

  @Getter @Setter private Path activityLogFile;

  private final ObjectProperty<List<Activity>> log = new SimpleObjectProperty<>(List.of());

  final List<Activity> getLog() {
    return log.get();
  }

  final void setLog(List<Activity> log) {
    this.log.set(log);
  }

  final ObjectProperty<List<Activity>> logProperty() {
    return log;
  }

  private final ObjectProperty<List<Activity>> recent = new SimpleObjectProperty<>(List.of());

  final List<Activity> getRecent() {
    return recent.get();
  }

  final void setRecent(List<Activity> recent) {
    this.recent.set(recent);
  }

  final ObjectProperty<List<Activity>> recentProperty() {
    return recent;
  }

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

  private final ObjectProperty<List<String>> knownTags = new SimpleObjectProperty<>(List.of());

  final List<String> getKnownTags() {
    return knownTags.get();
  }

  final void setKnownTags(List<String> value) {
    knownTags.set(value);
  }

  final ObjectProperty<List<String>> knownTagsProperty() {
    return knownTags;
  }

  private final BooleanProperty formDisabled = new SimpleBooleanProperty(true);

  final boolean isFormDisabled() {
    return formDisabled.get();
  }

  final void setFormDisabled(boolean formDisabled) {
    this.formDisabled.set(formDisabled);
  }

  final BooleanProperty formDisabledProperty() {
    return formDisabled;
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

  final BooleanBinding tagNotAddable =
      formDisabledProperty().or(knownTagsProperty().isEqualTo(List.of()));

  final BooleanBinding tagNotAddableBinding() {
    return tagNotAddable;
  }

  final BooleanBinding formUnsubmittable = formDisabledProperty().or(activityProperty().isEmpty());

  final BooleanBinding formUnsubmittableBinding() {
    return formUnsubmittable;
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
}
