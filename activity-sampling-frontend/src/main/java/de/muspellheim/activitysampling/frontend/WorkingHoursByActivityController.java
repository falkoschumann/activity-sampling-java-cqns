/*
 * Activity Sampling - Frontend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.frontend;

import de.muspellheim.activitysampling.contract.messages.queries.WorkingHoursByActivityQueryResult.WorkingHours;
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
import javafx.stage.Stage;

public class WorkingHoursByActivityController {
  @FXML private Stage stage;
  @FXML private TableView<WorkingHours> workingHoursTable;
  @FXML private TableColumn<WorkingHours, String> activityColumn;
  @FXML private TableColumn<WorkingHours, List<String>> tagsColumn;
  @FXML private TableColumn<WorkingHours, Duration> workingHoursColumn;

  // TODO Ersetze Table mit TreeTable und Gruppiere nach Tags

  public static WorkingHoursByActivityController create(Stage owner) {
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
  }

  private final ObjectProperty<List<WorkingHours>> workingHours =
      new SimpleObjectProperty<>() {
        @Override
        protected void invalidated() {
          workingHoursTable.getItems().setAll(getValue());
        }
      };

  public final List<WorkingHours> getWorkingHours() {
    return workingHours.get();
  }

  public final void setWorkingHours(List<WorkingHours> workingHours) {
    this.workingHours.set(workingHours);
  }

  public final ObjectProperty<List<WorkingHours>> workingHoursProperty() {
    return workingHours;
  }

  public void run() {
    stage.show();
  }
}
