/*
 * Activity Sampling - Frontend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.frontend;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class Stages {
  private Stages() {}

  public static void hookCloseHandler(Stage stage) {
    hookCloseHandler(stage, stage::close);
  }

  public static void hookCloseHandler(Stage stage, Runnable handler) {
    stage.addEventHandler(
        KeyEvent.KEY_RELEASED,
        e -> {
          if (e.isShortcutDown() && KeyCode.W == e.getCode()) {
            e.consume();
            handler.run();
          }
        });
  }
}
