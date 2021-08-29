/*
 * Activity Sampling - Frontend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.frontend;

import de.muspellheim.activitysampling.contract.messages.queries.TimeReportQuery;
import de.muspellheim.activitysampling.contract.messages.queries.TimeReportQueryResult;
import de.muspellheim.activitysampling.contract.messages.queries.TimeReportQueryResult.ClientEntry;
import de.muspellheim.activitysampling.contract.messages.queries.TimeReportQueryResult.ProjectEntry;
import de.muspellheim.activitysampling.contract.messages.queries.TimeReportQueryResult.TaskEntry;
import de.muspellheim.activitysampling.contract.messages.queries.TimeReportQueryResult.TimesheetEntry;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import lombok.Getter;
import lombok.Setter;

public class TimeReportController implements Initializable {
  @Getter @Setter private Consumer<TimeReportQuery> onTimesheetQuery;

  @FXML Stage stage;
  @FXML DatePicker startDate;
  @FXML DatePicker endDate;
  @FXML ChoiceBox<Report> reportChoice;
  @FXML TextField totalHours;

  @FXML TableView<ClientEntry> clientsTable;
  @FXML TableColumn<ClientEntry, String> clientsClientColumn;
  @FXML TableColumn<ClientEntry, Duration> clientsHoursColumn;

  @FXML TableView<ProjectEntry> projectsTable;
  @FXML TableColumn<ProjectEntry, String> projectsClientColumn;
  @FXML TableColumn<ProjectEntry, String> projectsProjectColumn;
  @FXML TableColumn<ProjectEntry, Duration> projectsHoursColumn;

  @FXML TableView<TaskEntry> tasksTable;
  @FXML TableColumn<TaskEntry, String> tasksClientColumn;
  @FXML TableColumn<TaskEntry, String> tasksProjectColumn;
  @FXML TableColumn<TaskEntry, String> tasksTaskColumn;
  @FXML TableColumn<TaskEntry, Duration> tasksHoursColumn;

  @FXML TableView<TimesheetEntry> timesheetTable;
  @FXML TableColumn<TimesheetEntry, LocalDate> timesheetDateColumn;
  @FXML TableColumn<TimesheetEntry, String> timesheetClientColumn;
  @FXML TableColumn<TimesheetEntry, String> timesheetProjectColumn;
  @FXML TableColumn<TimesheetEntry, String> timesheetTaskColumn;
  @FXML TableColumn<TimesheetEntry, String> timesheetNotesColumn;
  @FXML TableColumn<TimesheetEntry, Duration> timesheetHoursColumn;
  @FXML TableColumn<TimesheetEntry, String> timesheetFirstNameColumn;
  @FXML TableColumn<TimesheetEntry, String> timesheetLastNameColumn;

  static TimeReportController create(Stage owner) {
    try {
      var location = PreferencesController.class.getResource("TimeReportView.fxml");
      var resources = ResourceBundle.getBundle("ActivitySampling");
      var loader = new FXMLLoader(location, resources);
      loader.load();

      var controller = (TimeReportController) loader.getController();
      controller.stage.initOwner(owner);
      return controller;
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    reportChoice.getItems().setAll(Report.values());
    reportChoice.setValue(Report.TIMESHEET);
    reportChoice.setConverter(
        new StringConverter<>() {
          @Override
          public String toString(Report object) {
            return switch (object) {
              case CLIENTS -> resources.getString("timeReportView.clients");
              case PROJECTS -> resources.getString("timeReportView.projects");
              case TASKS -> resources.getString("timeReportView.tasks");
              case TIMESHEET -> resources.getString("timeReportView.timesheet");
            };
          }

          @Override
          public Report fromString(String string) {
            throw new UnsupportedOperationException();
          }
        });
    clientsClientColumn.setCellValueFactory(new ComponentValueFactory<>("client"));
    clientsHoursColumn.setCellValueFactory(new ComponentValueFactory<>("hours"));
    projectsClientColumn.setCellValueFactory(new ComponentValueFactory<>("client"));
    projectsProjectColumn.setCellValueFactory(new ComponentValueFactory<>("project"));
    projectsHoursColumn.setCellValueFactory(new ComponentValueFactory<>("hours"));
    tasksClientColumn.setCellValueFactory(new ComponentValueFactory<>("client"));
    tasksProjectColumn.setCellValueFactory(new ComponentValueFactory<>("project"));
    tasksTaskColumn.setCellValueFactory(new ComponentValueFactory<>("task"));
    tasksHoursColumn.setCellValueFactory(new ComponentValueFactory<>("hours"));
    timesheetDateColumn.setCellValueFactory(new ComponentValueFactory<>("date"));
    timesheetClientColumn.setCellValueFactory(new ComponentValueFactory<>("client"));
    timesheetProjectColumn.setCellValueFactory(new ComponentValueFactory<>("project"));
    timesheetTaskColumn.setCellValueFactory(new ComponentValueFactory<>("task"));
    timesheetNotesColumn.setCellValueFactory(new ComponentValueFactory<>("notes"));
    timesheetHoursColumn.setCellValueFactory(new ComponentValueFactory<>("hours"));
    timesheetFirstNameColumn.setCellValueFactory(new ComponentValueFactory<>("firstName"));
    timesheetLastNameColumn.setCellValueFactory(new ComponentValueFactory<>("lastName"));

    reportChoice.valueProperty().addListener(o -> update());
  }

  public void run() {
    stage.show();
    onTimesheetQuery.accept(new TimeReportQuery(startDate.getValue(), endDate.getValue()));
  }

  public void display(TimeReportQueryResult result) {
    startDate.setValue(result.start());
    endDate.setValue(result.end());
    totalHours.setText(result.totalHours().toString());
    clientsTable.getItems().setAll(result.clients());
    projectsTable.getItems().setAll(result.projects());
    tasksTable.getItems().setAll(result.tasks());
    timesheetTable.getItems().setAll(result.timesheet());
    update();
  }

  private void update() {
    switch (reportChoice.getValue()) {
      case CLIENTS -> {
        clientsTable.setVisible(true);
        projectsTable.setVisible(false);
        tasksTable.setVisible(false);
        timesheetTable.setVisible(false);
      }
      case PROJECTS -> {
        clientsTable.setVisible(false);
        projectsTable.setVisible(true);
        tasksTable.setVisible(false);
        timesheetTable.setVisible(false);
      }
      case TASKS -> {
        clientsTable.setVisible(false);
        projectsTable.setVisible(false);
        tasksTable.setVisible(true);
        timesheetTable.setVisible(false);
      }
      case TIMESHEET -> {
        clientsTable.setVisible(false);
        projectsTable.setVisible(false);
        tasksTable.setVisible(false);
        timesheetTable.setVisible(true);
      }
    }
  }

  @FXML
  private void handleSearch() {
    onTimesheetQuery.accept(new TimeReportQuery(startDate.getValue(), endDate.getValue()));
  }

  private enum Report {
    CLIENTS,
    PROJECTS,
    TASKS,
    TIMESHEET
  }
}
