/*
 * Activity Sampling - Frontend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.frontend;

import de.muspellheim.activitysampling.contract.data.Activity;
import de.muspellheim.activitysampling.contract.messages.queries.WorkingHoursTodayQuery;
import de.muspellheim.activitysampling.contract.messages.queries.WorkingHoursTodayQueryResult;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.Duration;
import java.time.LocalTime;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;

public class WorkingHoursTodayController {
  @Getter @Setter Consumer<WorkingHoursTodayQuery> onWorkingHoursTodayQuery;

  @FXML private Stage stage;
  @FXML private TextField dateText;
  @FXML private TableView<Activity> activitiesTable;
  @FXML private TableColumn<Activity, LocalTime> timestampColumn;
  @FXML private TableColumn<Activity, Duration> periodColumn;
  @FXML private TableColumn<Activity, String> activityColumn;
  @FXML private TableColumn<Activity, List<String>> tagsColumn;
  @FXML private TextField totalWorkingHoursText;

  // TODO Zeige an, wenn Stichworte gefiltert sind
  private TagsController tagsController;

  static WorkingHoursTodayController create(Stage owner) {
    try {
      var location = PreferencesController.class.getResource("WorkingHoursTodayView.fxml");
      var resources = ResourceBundle.getBundle("ActivitySampling");
      var loader = new FXMLLoader(location, resources);
      loader.load();

      var controller = (WorkingHoursTodayController) loader.getController();
      controller.stage.initOwner(owner);
      return controller;
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  @FXML
  private void initialize() {
    timestampColumn.setCellValueFactory(
        it -> new ReadOnlyObjectWrapper<>(it.getValue().timestamp().toLocalTime()));
    periodColumn.setCellValueFactory(it -> new ReadOnlyObjectWrapper<>(it.getValue().period()));
    activityColumn.setCellValueFactory(it -> new ReadOnlyStringWrapper(it.getValue().activity()));
    tagsColumn.setCellValueFactory(it -> new ReadOnlyObjectWrapper<>(it.getValue().tags()));
    tagsController = TagsController.create(stage);

    tagsController.setOnSelectedTagsChanged(
        t -> onWorkingHoursTodayQuery.accept(new WorkingHoursTodayQuery(t)));
    Stages.hookCloseHandler(stage);
  }

  public void display(WorkingHoursTodayQueryResult result) {
    dateText.setText(result.date().toString());
    totalWorkingHoursText.setText(result.totalWorkingHours().toString());
    activitiesTable.getItems().setAll(result.activities());
    tagsController.setTags(result.tags());
  }

  void run() {
    stage.show();
    // FIXME Hier müssen Stichworte zurückgesetzt werden
    onWorkingHoursTodayQuery.accept(new WorkingHoursTodayQuery());
  }

  @FXML
  private void handleSelectTags() {
    tagsController.run();
  }
}
