/*
 * Activity Sampling - Frontend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.frontend;

import de.muspellheim.activitysampling.contract.data.Activity;
import de.muspellheim.activitysampling.contract.messages.queries.WorkingHoursThisWeekQuery;
import de.muspellheim.activitysampling.contract.messages.queries.WorkingHoursThisWeekQueryResult;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Consumer;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;

public class WorkingHoursThisWeekController {
  @Getter @Setter Consumer<WorkingHoursThisWeekQuery> onWorkingHoursThisWeekQuery;

  @FXML private Stage stage;
  @FXML private TextField calendarWeekText;
  @FXML private TreeTableView<Activity> activitiesTable;
  @FXML private TreeTableColumn<Activity, String> timestampColumn;
  @FXML private TreeTableColumn<Activity, Duration> periodColumn;
  @FXML private TreeTableColumn<Activity, String> activityColumn;
  @FXML private TreeTableColumn<Activity, String> tagsColumn;
  @FXML private TextField totalWorkingHoursText;

  // TODO Zeige an, wenn Stichworte gefiltert sind
  private TagsController tagsController;
  private final SortedMap<LocalDate, WeekdayTreeItem> weekdays = new TreeMap<>();

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
    activitiesTable.setRoot(new TreeItem<>(null));
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
    tagsController = TagsController.create(stage);

    tagsController.setOnSelectedTagsChanged(
        t -> onWorkingHoursThisWeekQuery.accept(new WorkingHoursThisWeekQuery(t)));
    Stages.hookCloseHandler(stage);
  }

  public void display(WorkingHoursThisWeekQueryResult result) {
    calendarWeekText.setText(String.valueOf(result.calendarWeek()));
    totalWorkingHoursText.setText(result.totalWorkingHours().toString());
    tagsController.setTags(result.tags());

    updateWeekdays(result.activities());
  }

  private void updateWeekdays(List<Activity> activities) {
    if (activities.isEmpty()) {
      weekdays.clear();
      activitiesTable.getRoot().getChildren().setAll(weekdays.values());
      return;
    }

    var lastWeekdays = Set.copyOf(weekdays.keySet());
    var firstDate = activities.get(0).timestamp().toLocalDate();
    lastWeekdays.forEach(
        d -> {
          if (d.isBefore(firstDate)) {
            weekdays.remove(d);
          }
        });
    var lastDate = activities.get(activities.size() - 1).timestamp().toLocalDate();
    lastWeekdays.forEach(
        d -> {
          if (d.isAfter(lastDate)) {
            weekdays.remove(d);
          }
        });

    WeekdayTreeItem currentWeekday = null;
    for (Activity it : activities) {
      var currentDate = it.timestamp().toLocalDate();
      if (currentWeekday == null
          || !currentWeekday.getValue().timestamp().toLocalDate().equals(currentDate)) {
        if (!weekdays.containsKey(currentDate)) {
          currentWeekday =
              new WeekdayTreeItem(new Activity("", it.timestamp(), Duration.ZERO, "", List.of()));
          weekdays.put(currentDate, currentWeekday);
        } else {
          currentWeekday = weekdays.get(currentDate);
          currentWeekday.setValue(new Activity("", it.timestamp(), Duration.ZERO, "", List.of()));
          currentWeekday.getChildren().clear();
        }
      }
      currentWeekday.getChildren().add(new ActivityTreeItem(it));
      currentWeekday.setValue(
          new Activity(
              "",
              currentWeekday.getValue().timestamp(),
              currentWeekday.getValue().period().plus(it.period()),
              "",
              List.of()));
    }
    activitiesTable.getRoot().getChildren().setAll(weekdays.values());
  }

  void run() {
    stage.show();
    onWorkingHoursThisWeekQuery.accept(new WorkingHoursThisWeekQuery());
  }

  @FXML
  private void handleSelectTags() {
    tagsController.run();
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
