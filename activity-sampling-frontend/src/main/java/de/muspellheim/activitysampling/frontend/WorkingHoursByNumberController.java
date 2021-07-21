/*
 * Activity Sampling - Frontend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.frontend;

import de.muspellheim.activitysampling.contract.messages.queries.WorkingHoursByNumberQuery;
import de.muspellheim.activitysampling.contract.messages.queries.WorkingHoursByNumberQueryResult;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;

public class WorkingHoursByNumberController {
  @Getter @Setter Consumer<WorkingHoursByNumberQuery> onWorkingHoursByNumberQuery;

  @FXML private Stage stage;
  @FXML private BarChart<String, Number> chart;
  @FXML private CategoryAxis xAxis;
  @FXML private NumberAxis yAxis;

  private XYChart.Series<String, Number> series;

  static WorkingHoursByNumberController create(Stage owner) {
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
    series = new XYChart.Series<>();
    series.setName("Working Hours");
    chart.getData().setAll(List.of(series));
    chart.setTitle("Working Hours by Number");
    xAxis.setLabel("Working hours");
    yAxis.setLabel("Number");

    Stages.hookCloseHandler(stage);
  }

  public void display(WorkingHoursByNumberQueryResult result) {
    var data =
        result.catogories().stream()
            .map(it -> new Data<>(it.workingHours().toString(), (Number) it.number()))
            .toList();
    series.setData(FXCollections.observableArrayList(data));
  }

  void run() {
    stage.show();
    onWorkingHoursByNumberQuery.accept(new WorkingHoursByNumberQuery());
  }
}
