/*
 * Activity Sampling - Frontend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.frontend;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class PreferencesView extends VBox {
  public PreferencesView() {
    var periodDurationSpinner = new Spinner<>(1, 1440, 20);
    var periodDurationContainer = new HBox(Views.GAP, periodDurationSpinner, new Label("minutes"));
    periodDurationContainer.setAlignment(Pos.CENTER_LEFT);
    var periodDurationInput = new HFormInput<>("Period duration:", periodDurationContainer);

    var activityLogField = new TextField();
    activityLogField.setEditable(false);
    HBox.setHgrow(activityLogField, Priority.ALWAYS);
    var changeButton = new Button("Change");
    var activityLogContainer = new HBox(Views.GAP, activityLogField, changeButton);
    activityLogContainer.setAlignment(Pos.CENTER_LEFT);
    HBox.setHgrow(activityLogContainer, Priority.ALWAYS);
    var activityLogInput = new HFormInput<>("Activity log:", activityLogContainer);

    setStyle("-fx-font-family: Verdana;");
    setPrefSize(640, 360);
    setPadding(new Insets(Views.MARGIN));
    setSpacing(Views.UNRELATED_GAP);
    getChildren().setAll(periodDurationInput, activityLogInput);
  }
}
