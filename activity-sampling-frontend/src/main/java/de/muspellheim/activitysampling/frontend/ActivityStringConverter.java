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
import javafx.util.StringConverter;

class ActivityStringConverter extends StringConverter<Activity> {
  @Override
  public String toString(Activity object) {
    String string = object.getActivity();
    if (!object.getTags().isEmpty()) {
      string = "[" + String.join(", ", object.getTags()) + "] " + string;
    }
    return string;
  }

  @Override
  public Activity fromString(String string) {
    System.out.println("String: " + string);
    var pattern = Pattern.compile("(\\[(.+)])?\\s*(.+)");
    var matcher = pattern.matcher(string);
    var result = matcher.toMatchResult();
    var groupCount = result.groupCount();
    System.out.println("Group count: " + groupCount);
    for (int i = 1; i < groupCount; i++) {
      System.out.println("Match " + i + ": " + result.group(i));
    }
    return new Activity("id", LocalDateTime.now(), Duration.ZERO, "activity", List.of("tags"));
  }
}
