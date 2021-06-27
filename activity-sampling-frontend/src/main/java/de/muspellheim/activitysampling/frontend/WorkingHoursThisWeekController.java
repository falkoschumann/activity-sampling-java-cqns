/*
 * Activity Sampling - Frontend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.frontend;

import de.muspellheim.activitysampling.contract.data.Activity;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;

public class WorkingHoursThisWeekController {
  @Getter @Setter Runnable onQuery;

  @FXML private Stage stage;
  @FXML private TextField calendarWeekText;
  @FXML private TreeTableView<Activity> activitiesTable;
  @FXML private TreeTableColumn<Activity, String> timestampColumn;
  @FXML private TreeTableColumn<Activity, Duration> periodColumn;
  @FXML private TreeTableColumn<Activity, String> activityColumn;
  @FXML private TreeTableColumn<Activity, String> tagsColumn;
  @FXML private TextField totalWorkingHoursText;

  static WorkingHoursThisWeekController create(Stage owner) {
    try {
      var location = PreferencesController.class.getResource("WorkingHoursThisWeekView.fxml");
      var resources = ResourceBundle.getBundle("ActivitySampling");
      var loader = new FXMLLoader(location, resources);
      loader.load();

      var controller = (WorkingHoursThisWeekController) loader.getController();
      controller.stage.initOwner(owner);
      return controller;
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  @FXML
  private void initialize() {
    activitiesTable.setRoot(new TreeItem<>(Activity.NULL));
    timestampColumn.setCellValueFactory(
        it ->
            new ReadOnlyObjectWrapper<>(
                it.getValue() instanceof WeekdayTreeItem
                    ? it.getValue().getValue().timestamp().toLocalDate().toString()
                    : it.getValue().getValue().timestamp().toLocalTime().toString()));
    periodColumn.setCellValueFactory(
        it -> new ReadOnlyObjectWrapper<>(it.getValue().getValue().period()));
    activityColumn.setCellValueFactory(
        it -> new ReadOnlyStringWrapper(it.getValue().getValue().activity()));
    tagsColumn.setCellValueFactory(
        it ->
            new ReadOnlyObjectWrapper<>(
                it.getValue() instanceof WeekdayTreeItem
                    ? ""
                    : it.getValue().getValue().tags().toString()));

    stage.addEventHandler(
        KeyEvent.KEY_RELEASED,
        e -> {
          if (e.isMetaDown() && KeyCode.W.equals(e.getCode())) {
            stage.hide();
          }
        });
  }

  private final IntegerProperty calendarWeek =
      new SimpleIntegerProperty() {
        @Override
        protected void invalidated() {
          calendarWeekText.setText(getValue().toString());
        }
      };

  final int getCalendarWeek() {
    return calendarWeek.get();
  }

  final void setCalendarWeek(int value) {
    this.calendarWeek.set(value);
  }

  final IntegerProperty calendarWeekProperty() {
    return calendarWeek;
  }

  private final ObjectProperty<Duration> totalWorkingHours =
      new SimpleObjectProperty<>() {
        @Override
        protected void invalidated() {
          totalWorkingHoursText.setText(getValue().toString());
        }
      };

  final Duration getTotalWorkingHours() {
    return totalWorkingHours.get();
  }

  final void setTotalWorkingHours(Duration value) {
    this.totalWorkingHours.set(value);
  }

  final ObjectProperty<Duration> totalWorkingHoursProperty() {
    return totalWorkingHours;
  }

  private final ObjectProperty<List<Activity>> activities =
      new SimpleObjectProperty<>() {
        @Override
        protected void invalidated() {
          var weekdays = new ArrayList<TreeItem<Activity>>();
          TreeItem<Activity> currentDay = null;
          for (var it : getValue()) {
            if (currentDay == null
                || !currentDay
                    .getValue()
                    .timestamp()
                    .toLocalDate()
                    .equals(it.timestamp().toLocalDate())) {
              currentDay =
                  new WeekdayTreeItem(
                      new Activity("", it.timestamp(), Duration.ZERO, "", List.of()));
              weekdays.add(currentDay);
            }
            currentDay.getChildren().add(new ActivityTreeItem(it));
            currentDay.setValue(
                new Activity(
                    "",
                    currentDay.getValue().timestamp(),
                    currentDay.getValue().period().plus(it.period()),
                    "",
                    List.of()));
          }
          activitiesTable.getRoot().getChildren().setAll(weekdays);
        }
      };

  final List<Activity> getActivities() {
    return activities.get();
  }

  final void setActivities(List<Activity> value) {
    this.activities.set(value);
  }

  final ObjectProperty<List<Activity>> activitiesProperty() {
    return activities;
  }

  void run() {
    stage.show();
    onQuery.run();
  }

  private static class WeekdayTreeItem extends TreeItem<Activity> {
    public WeekdayTreeItem(Activity value) {
      super(value);
    }
  }

  private static class ActivityTreeItem extends TreeItem<Activity> {
    public ActivityTreeItem(Activity value) {
      super(value);
    }
  }
}
