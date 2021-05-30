/*
 * Activity Sampling - Frontend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.frontend;

import de.muspellheim.activitysampling.contract.data.Activity;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;

public class ActivityLogRenderer {
  String render(List<Activity> activities) {
    var stringConverter = new ActivityStringConverter();
    var dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL);
    var timeFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT);
    var logBuilder = new StringBuilder();
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
      logBuilder.append(stringConverter.toString(activity));
      logBuilder.append("\n");
    }
    return logBuilder.toString();
  }
}
