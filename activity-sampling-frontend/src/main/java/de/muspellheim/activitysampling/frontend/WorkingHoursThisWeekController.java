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
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.SortedSet;
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

  private SortedSet<String> tags;
  private Set<String> selectedTags;

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

    Stages.hookCloseHandler(stage);
  }

  public void display(WorkingHoursThisWeekQueryResult result) {
    calendarWeekText.setText(String.valueOf(result.calendarWeek()));
    totalWorkingHoursText.setText(result.totalWorkingHours().toString());

    tags = result.tags();
    if (selectedTags == null) {
      selectedTags = result.tags();
    }

    var weekdays = new ArrayList<TreeItem<Activity>>();
    TreeItem<Activity> currentDay = null;
    for (var it : result.activities()) {
      if (currentDay == null
          || !currentDay
              .getValue()
              .timestamp()
              .toLocalDate()
              .equals(it.timestamp().toLocalDate())) {
        currentDay =
            new WeekdayTreeItem(new Activity("", it.timestamp(), Duration.ZERO, "", List.of()));
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

  void run() {
    stage.show();
    onWorkingHoursThisWeekQuery.accept(new WorkingHoursThisWeekQuery(selectedTags));
  }

  @FXML
  private void handleSelectTags() {
    var controller = TagsController.create(stage);
    controller.initTags(tags, selectedTags);
    controller.run();
    selectedTags = controller.getSelectedTags();
    onWorkingHoursThisWeekQuery.accept(new WorkingHoursThisWeekQuery(selectedTags));
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
