/*
 * Activity Sampling - Frontend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.frontend;

import de.muspellheim.activitysampling.contract.MessageHandling;
import de.muspellheim.activitysampling.contract.messages.queries.TimeReportQuery;
import de.muspellheim.activitysampling.contract.messages.queries.TimeReportQueryResult;
import de.muspellheim.activitysampling.contract.messages.queries.TimeReportQueryResult.ClientEntry;
import de.muspellheim.activitysampling.contract.messages.queries.TimeReportQueryResult.ProjectEntry;
import de.muspellheim.activitysampling.contract.messages.queries.TimeReportQueryResult.SummaryEntry;
import de.muspellheim.activitysampling.contract.messages.queries.TimeReportQueryResult.TaskEntry;
import de.muspellheim.activitysampling.contract.messages.queries.TimeReportQueryResult.TimesheetEntry;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.StringConverter;

public class TimeReportController {
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

  @FXML TableView<SummaryEntry> summaryTable;
  @FXML TableColumn<SummaryEntry, String> summaryClientColumn;
  @FXML TableColumn<SummaryEntry, String> summaryProjectColumn;
  @FXML TableColumn<SummaryEntry, String> summaryTaskColumn;
  @FXML TableColumn<SummaryEntry, Duration> summaryHoursColumn;

  @FXML private ResourceBundle resources;

  private MessageHandling messageHandling;

  static TimeReportController create(Stage owner, MessageHandling messageHandling) {
    try {
      var location = PreferencesController.class.getResource("TimeReportView.fxml");
      var resources = ResourceBundle.getBundle("ActivitySampling");
      var loader = new FXMLLoader(location, resources);
      loader.load();

      var controller = (TimeReportController) loader.getController();
      controller.stage.initOwner(owner);
      controller.messageHandling = messageHandling;
      return controller;
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  @FXML
  public void initialize() {
    // Build
    reportChoice.getItems().setAll(Report.values());
    reportChoice.setValue(Report.TIMESHEET);
    reportChoice.setConverter(new ReportStringConverter());
    clientsClientColumn.setCellValueFactory(new ComponentValueFactory<>("client"));
    clientsHoursColumn.setCellValueFactory(new ComponentValueFactory<>("hours"));
    projectsClientColumn.setCellValueFactory(new ComponentValueFactory<>("client"));
    projectsProjectColumn.setCellValueFactory(new ComponentValueFactory<>("project"));
    projectsHoursColumn.setCellValueFactory(new ComponentValueFactory<>("hours"));
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
    summaryClientColumn.setCellValueFactory(new ComponentValueFactory<>("client"));
    summaryProjectColumn.setCellValueFactory(new ComponentValueFactory<>("project"));
    summaryTaskColumn.setCellValueFactory(new ComponentValueFactory<>("task"));
    summaryHoursColumn.setCellValueFactory(new ComponentValueFactory<>("hours"));

    // Bind
    reportChoice.valueProperty().addListener(o -> update());
    Stages.hookWindowCloseHandler(stage);
  }

  public void run() {
    Request.runAsync(
        () -> messageHandling.handle(new TimeReportQuery(startDate.getValue(), endDate.getValue())),
        this::display);
    stage.show();
  }

  @FXML
  private void handleSearch() {
    Request.runAsync(
        () -> messageHandling.handle(new TimeReportQuery(startDate.getValue(), endDate.getValue())),
        this::display);
  }

  public void display(TimeReportQueryResult result) {
    startDate.setValue(result.start());
    endDate.setValue(result.end());
    totalHours.setText(result.totalHours().toString());
    clientsTable.getItems().setAll(result.clients());
    projectsTable.getItems().setAll(result.projects());
    tasksTable.getItems().setAll(result.tasks());
    timesheetTable.getItems().setAll(result.timesheet());
    summaryTable.getItems().setAll(result.summaries());
    update();
  }

  private void update() {
    switch (reportChoice.getValue()) {
      case CLIENTS -> {
        clientsTable.setVisible(true);
        projectsTable.setVisible(false);
        tasksTable.setVisible(false);
        timesheetTable.setVisible(false);
        summaryTable.setVisible(false);
      }
      case PROJECTS -> {
        clientsTable.setVisible(false);
        projectsTable.setVisible(true);
        tasksTable.setVisible(false);
        timesheetTable.setVisible(false);
        summaryTable.setVisible(false);
      }
      case TASKS -> {
        clientsTable.setVisible(false);
        projectsTable.setVisible(false);
        tasksTable.setVisible(true);
        timesheetTable.setVisible(false);
        summaryTable.setVisible(false);
      }
      case TIMESHEET -> {
        clientsTable.setVisible(false);
        projectsTable.setVisible(false);
        tasksTable.setVisible(false);
        timesheetTable.setVisible(true);
        summaryTable.setVisible(false);
      }
      case SUMMARY -> {
        clientsTable.setVisible(false);
        projectsTable.setVisible(false);
        tasksTable.setVisible(false);
        timesheetTable.setVisible(false);
        summaryTable.setVisible(true);
      }
    }
  }

  private enum Report {
    CLIENTS,
    PROJECTS,
    TASKS,
    TIMESHEET,
    SUMMARY
  }

  private class ReportStringConverter extends StringConverter<Report> {
    @Override
    public String toString(Report object) {
      return switch (object) {
        case CLIENTS -> resources.getString("timeReportView.clients");
        case PROJECTS -> resources.getString("timeReportView.projects");
        case TASKS -> resources.getString("timeReportView.tasks");
        case TIMESHEET -> resources.getString("timeReportView.timesheet");
        case SUMMARY -> resources.getString("timeReportView.summary");
      };
    }

    @Override
    public Report fromString(String string) {
      throw new UnsupportedOperationException();
    }
  }
}
