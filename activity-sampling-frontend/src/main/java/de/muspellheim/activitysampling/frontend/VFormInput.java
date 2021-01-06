/*
 * Activity Sampling - Frontend
 * Copyright (c) 2020 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.frontend;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

class VFormInput<T extends Node> extends VBox {
  private final T control;

  VFormInput(String labelText, T control) {
    this.control = control;

    var label = new Label(labelText);
    label.setLabelFor(control);

    setSpacing(Views.GAP);
    getChildren().setAll(label, control);
  }

  final T getControl() {
    return control;
  }
}
