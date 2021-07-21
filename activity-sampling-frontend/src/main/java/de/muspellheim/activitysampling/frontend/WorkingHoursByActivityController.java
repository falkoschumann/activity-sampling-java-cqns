/*
 * Activity Sampling - Frontend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.frontend;

import de.muspellheim.activitysampling.contract.data.WorkingHours;
import de.muspellheim.activitysampling.contract.messages.queries.WorkingHoursByActivityQuery;
import de.muspellheim.activitysampling.contract.messages.queries.WorkingHoursByActivityQueryResult;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.Duration;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;

public class WorkingHoursByActivityController {
  @Getter @Setter Consumer<WorkingHoursByActivityQuery> onWorkingHoursByActivityQuery;

  @FXML private Stage stage;
  @FXML private TableView<WorkingHours> workingHoursTable;
  @FXML private TableColumn<WorkingHours, String> activityColumn;
  @FXML private TableColumn<WorkingHours, List<String>> tagsColumn;
  @FXML private TableColumn<WorkingHours, Duration> workingHoursColumn;

  static WorkingHoursByActivityController create(Stage owner) {
    try {
      var location = PreferencesController.class.getResource("WorkingHoursByActivityView.fxml");
      var resources = ResourceBundle.getBundle("ActivitySampling");
      var loader = new FXMLLoader(location, resources);
      loader.load();

      var controller = (WorkingHoursByActivityController) loader.getController();
      controller.stage.initOwner(owner);
      return controller;
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  @FXML
  private void initialize() {
    activityColumn.setCellValueFactory(it -> new ReadOnlyStringWrapper(it.getValue().activity()));
    tagsColumn.setCellValueFactory(it -> new ReadOnlyObjectWrapper<>(it.getValue().tags()));
    workingHoursColumn.setCellValueFactory(
        it -> new ReadOnlyObjectWrapper<>(it.getValue().workingHours()));

    Stages.hookCloseHandler(stage);
  }

  public void display(WorkingHoursByActivityQueryResult result) {
    workingHoursTable.getItems().setAll(result.workingHours());
  }

  void run() {
    stage.show();
    onWorkingHoursByActivityQuery.accept(new WorkingHoursByActivityQuery());
  }
}
