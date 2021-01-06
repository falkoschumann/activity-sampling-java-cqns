/*
 * Activity Sampling - Frontend
 * Copyright (c) 2020 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.frontend;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

class HFormInput<T extends Node> extends HBox {
  private final T control;

  HFormInput(String labelText, T control) {
    this.control = control;

    var label = new Label(labelText);
    label.setPrefWidth(120);
    label.setLabelFor(control);

    setAlignment(Pos.CENTER_LEFT);
    setSpacing(Views.GAP);
    getChildren().setAll(label, control);
  }

  final T getControl() {
    return control;
  }
}
