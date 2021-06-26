/*
 * Activity Sampling - Frontend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.frontend;

import de.muspellheim.activitysampling.contract.messages.queries.WorkingHoursByNumberQueryResult.WorkingHoursCategory;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.stage.Stage;

public class WorkingHoursByNumberController {
  @FXML private Stage stage;
  @FXML private BarChart<String, Number> chart;
  @FXML private CategoryAxis xAxis;
  @FXML private NumberAxis yAxis;

  public static WorkingHoursByNumberController create(Stage owner) {
    try {
      var location = PreferencesController.class.getResource("WorkingHoursByNumberView.fxml");
      var resources = ResourceBundle.getBundle("ActivitySampling");
      var loader = new FXMLLoader(location, resources);
      loader.load();

      var controller = (WorkingHoursByNumberController) loader.getController();
      controller.stage.initOwner(owner);
      return controller;
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  @FXML
  private void initialize() {
    chart.setTitle("Working Hours by Number");
    xAxis.setLabel("Working hours");
    yAxis.setLabel("Number");
    /*
    XYChart.Series series = new XYChart.Series();
    series.setName("Working Hours");
    series.getData().add(new XYChart.Data("1", 25601.34));
    series.getData().add(new XYChart.Data("2", 20148.82));
    series.getData().add(new XYChart.Data("3", 10000));
    series.getData().add(new XYChart.Data("4", 35407.15));
    series.getData().add(new XYChart.Data("5", 12000));
    chart.setData(FXCollections.observableArrayList(series));
    */
  }

  private final ObjectProperty<List<WorkingHoursCategory>> workingHours =
      new SimpleObjectProperty<>() {
        @Override
        protected void invalidated() {
          XYChart.Series series = new XYChart.Series();
          series.setName("Working Hours");
          System.out.println("Anzahl Datensätze: " + getValue().size());
          getValue()
              .forEach(
                  it -> series.getData().add(new Data(it.workingHours().toString(), it.number())));
          chart.setData(FXCollections.observableArrayList(series));
        }
      };

  public final List<WorkingHoursCategory> getWorkingHours() {
    return workingHours.get();
  }

  public final void setWorkingHours(List<WorkingHoursCategory> workingHours) {
    this.workingHours.set(workingHours);
  }

  public final ObjectProperty<List<WorkingHoursCategory>> workingHoursProperty() {
    return workingHours;
  }

  public void run() {
    stage.show();
  }
}
