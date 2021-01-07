/*
 * Activity Sampling - Frontend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.frontend;

import de.muspellheim.activitysampling.contract.data.Activity;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TextArea;
import javafx.scene.layout.StackPane;

class ActivityLog extends StackPane {
  private final ObservableList<Activity> activities = FXCollections.observableArrayList();

  private final TextArea text;

  ActivityLog() {
    text = new TextArea();
    text.setEditable(false);
    text.setFocusTraversable(false);

    getChildren().setAll(text);

    activities.addListener((InvalidationListener) l -> update());
  }

  public ObservableList<Activity> getActivities() {
    return activities;
  }

  private void update() {
    var dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL);
    var timeFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT);
    var stringConverter = new ActivityStringConverter();
    var logBuilder = new StringBuilder();
    for (int i = 0; i < activities.size(); i++) {
      Activity activity = activities.get(i);
      if (i == 0) {
        logBuilder.append(dateFormatter.format(activity.getTimestamp()));
        logBuilder.append("\n");
      } else {
        Activity lastActivity = activities.get(i - 1);
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
    var log = logBuilder.toString();
    text.setText(log);
  }
}
