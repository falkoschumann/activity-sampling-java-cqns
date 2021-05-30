/*
 * Activity Sampling - Frontend
 * Copyright (c) 2020 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.frontend;

import de.muspellheim.activitysampling.contract.data.Activity;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javafx.util.StringConverter;

public class ActivityStringConverter extends StringConverter<Activity> {
  @Override
  public String toString(Activity object) {
    String string = object.activity();
    if (!object.tags().isEmpty()) {
      string = "[" + String.join(", ", object.tags()) + "] " + string;
    }
    return string;
  }

  @Override
  public Activity fromString(String string) {
    var pattern = Pattern.compile("(\\[(.+)])?\\s*(.+)");
    var matcher = pattern.matcher(string);
    String activity;
    List<String> tags = List.of();
    if (matcher.find()) {
      activity = matcher.group(3);
      var tagsString = matcher.group(2);
      if (tagsString != null) {
        tags =
            List.of(tagsString.split(",")).stream().map(String::strip).collect(Collectors.toList());
      }
    } else {
      activity = string;
    }

    return new Activity("", LocalDateTime.now(), Duration.ZERO, activity, tags);
  }
}
