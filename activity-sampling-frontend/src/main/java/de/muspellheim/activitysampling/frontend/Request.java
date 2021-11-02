/*
 * Activity Sampling - Frontend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.frontend;

import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;

public abstract class Request extends Task<Void> {
  private static final ResourceBundle RESOURCES = ResourceBundle.getBundle("ActivitySampling");

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

    var alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle(RESOURCES.getString("request.failed.title"));
    alert.setHeaderText(Request.RESOURCES.getString("request.failed.headerText"));
    alert.setContentText(getException().getLocalizedMessage());
    alert.show();
  }
}
