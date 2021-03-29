package de.muspellheim.activitysampling.frontend;

import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

public class HSpacer extends Region {
  public HSpacer() {
    HBox.setHgrow(this, Priority.ALWAYS);
  }
}
