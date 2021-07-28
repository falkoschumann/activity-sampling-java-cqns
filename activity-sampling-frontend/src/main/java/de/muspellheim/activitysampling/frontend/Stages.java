/*
 * Activity Sampling - Frontend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.frontend;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Window;

public class Stages {
  private Stages() {}

  public static void hookCloseHandler(Window window) {
    hookCloseHandler(window, window::hide);
  }

  public static void hookCloseHandler(Window window, Runnable handler) {
    window.addEventHandler(
        KeyEvent.KEY_RELEASED,
        e -> {
          if (e.isShortcutDown() && KeyCode.W == e.getCode()) {
            e.consume();
            handler.run();
          }
        });
  }
}
