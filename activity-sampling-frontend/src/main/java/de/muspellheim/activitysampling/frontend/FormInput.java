/*
 * Activity Sampling - Frontend
 * Copyright (c) 2020 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.frontend;

import javafx.beans.property.StringProperty;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

class FormInput extends VBox {
  private final TextField text;

  FormInput(String labelText, String promptText) {
    var label = new Label(labelText);

    text = new TextField();
    text.setPromptText(promptText);

    setSpacing(Views.GAP);
    getChildren().setAll(label, text);
  }

  final StringProperty valueProperty() {
    return text.textProperty();
  }

  final String getValue() {
    return text.getText();
  }

  final void setValue(String value) {
    text.setText(value);
  }
}
