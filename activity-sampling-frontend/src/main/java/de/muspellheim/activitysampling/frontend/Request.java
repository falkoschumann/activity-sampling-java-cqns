/*
 * Activity Sampling - Frontend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.frontend;

import java.util.concurrent.CompletableFuture;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;

public abstract class Request extends Task<Void> {
  public static void runAsync(Runnable handler) {
    runAsync(
        new Request() {
          @Override
          protected Void call() {
            handler.run();
            return null;
          }
        });
  }

  public static void runAsync(Request request) {
    CompletableFuture.runAsync(request);
  }

  @Override
  protected void failed() {
    getException().printStackTrace();

    // TODO Übersetze Texte
    var alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle("Error");
    alert.setHeaderText("An unexpected Error occurred");
    alert.setContentText(getException().getMessage());
    alert.show();
  }
}
