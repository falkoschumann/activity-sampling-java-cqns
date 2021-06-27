/*
 * Activity Sampling - Frontend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.frontend;

import de.muspellheim.activitysampling.contract.data.WorkingHours;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.Duration;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;

public class WorkingHoursByActivityController {
  @Getter @Setter Runnable onQuery;

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

    stage.addEventHandler(
        KeyEvent.KEY_RELEASED,
        e -> {
          if (e.isMetaDown() && KeyCode.W.equals(e.getCode())) {
            stage.hide();
          }
        });
  }

  private final ObjectProperty<List<WorkingHours>> workingHours =
      new SimpleObjectProperty<>() {
        @Override
        protected void invalidated() {
          workingHoursTable.getItems().setAll(getValue());
        }
      };

  final List<WorkingHours> getWorkingHours() {
    return workingHours.get();
  }

  final void setWorkingHours(List<WorkingHours> workingHours) {
    this.workingHours.set(workingHours);
  }

  final ObjectProperty<List<WorkingHours>> workingHoursProperty() {
    return workingHours;
  }

  void run() {
    stage.show();
    onQuery.run();
  }
}
