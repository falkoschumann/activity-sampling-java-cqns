/*
 * Activity Sampling - Frontend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.frontend;

import de.muspellheim.activitysampling.contract.data.Activity;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;

class ActivitySamplingModel {
  @Getter @Setter private Duration periodDuration = Duration.ofMinutes(20);
  @Getter @Setter private Path activityLogFile;
  @Setter private List<Activity> log = List.of();
  @Setter private List<Activity> recent = List.of();
  @Getter private String activity = "";
  @Getter @Setter private String tags = "";
  private LocalDateTime periodStart;
  @Getter private LocalDateTime periodEnd;
  @Getter private Duration remainingTime = Duration.ofMinutes(20);

  List<String> getRecentAsString() {
    return recent.stream().map(ActivitySamplingModel::activityToString).toList();
  }

  void setLast(Activity value) {
    setActivity(value.activity());
    setTags(String.join(", ", value.tags()));
  }

  void setActivity(String value) {
    var pattern = Pattern.compile("(\\[(.+)])?\\s*(.+)");
    var matcher = pattern.matcher(value);
    if (matcher.find()) {
      activity = matcher.group(3);
      tags = Optional.ofNullable(matcher.group(2)).orElse("");
    } else {
      activity = value;
    }
  }

  List<String> getTagsAsList() {
    if (tags.isBlank()) {
      return Collections.emptyList();
    }

    return List.of(tags.split(",")).stream().map(String::strip).collect(Collectors.toList());
  }

  String getRemainingTimeAsString() {
    return String.format(
        "%1$02d:%2$02d:%3$02d",
        remainingTime.toHoursPart(), remainingTime.toMinutesPart(), remainingTime.toSecondsPart());
  }

  double getPeriodProgress() {
    var remainingSeconds = (double) remainingTime.getSeconds();
    var totalSeconds = (double) periodDuration.getSeconds();
    return 1 - remainingSeconds / totalSeconds;
  }

  public boolean isPeriodEnded() {
    return remainingTime.equals(Duration.ZERO);
  }

  String getLogAsString() {
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
      logBuilder.append(activityToString(activity));
      logBuilder.append("\n");
    }
    return logBuilder.toString();
  }

  void resetPeriod() {
    periodStart = null;
  }

  void clockTicked(LocalDateTime timestamp) {
    if (periodStart == null) {
      periodStart = timestamp;
      remainingTime = periodDuration;
      return;
    }

    var elapsed = Duration.between(periodStart, timestamp);
    var remaining = periodDuration.minus(elapsed);
    if (remaining.toSeconds() <= 0) {
      remainingTime = Duration.ZERO;
      periodEnd = timestamp;
      periodStart = null;
    } else {
      remainingTime = remaining;
    }
  }

  private static String activityToString(Activity activity) {
    String string = activity.activity();
    if (!activity.tags().isEmpty()) {
      string = "[" + String.join(", ", (activity.tags())) + "] " + string;
    }
    return string;
  }
}
