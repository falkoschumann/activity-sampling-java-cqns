/*
 * Activity Sampling - Frontend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.frontend;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.Getter;

public class StageController<V extends Parent> {
  @Getter private final Stage stage;
  @Getter private final V view;

  public StageController(V view) {
    this(new Stage(), view);
  }

  public StageController(Stage stage, V view) {
    this.stage = stage;
    this.view = view;

    var scene = new Scene(view);
    stage.setScene(scene);
  }

  public void show() {
    stage.show();
  }
}
