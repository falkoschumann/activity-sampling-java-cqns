/*
 * Activity Sampling - Frontend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.frontend;

import de.muspellheim.activitysampling.contract.data.Activity;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
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
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;

public class WorkingHoursTodayController {
  @Getter @Setter Runnable onQuery;

  @FXML private Stage stage;
  @FXML private TextField dateText;
  @FXML private TableView<Activity> activitiesTable;
  @FXML private TableColumn<Activity, LocalTime> timestampColumn;
  @FXML private TableColumn<Activity, Duration> periodColumn;
  @FXML private TableColumn<Activity, String> activityColumn;
  @FXML private TableColumn<Activity, List<String>> tagsColumn;
  @FXML private TextField totalWorkingHoursText;

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

    stage.addEventHandler(
        KeyEvent.KEY_RELEASED,
        e -> {
          if (e.isMetaDown() && KeyCode.W.equals(e.getCode())) {
            stage.hide();
          }
        });
  }

  private final ObjectProperty<LocalDate> date =
      new SimpleObjectProperty<>() {
        @Override
        protected void invalidated() {
          dateText.setText(getValue().toString());
        }
      };

  final LocalDate getDate() {
    return date.get();
  }

  final void setDate(LocalDate value) {
    this.date.set(value);
  }

  final ObjectProperty<LocalDate> dateProperty() {
    return date;
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
          activitiesTable.getItems().setAll(getValue());
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
}
