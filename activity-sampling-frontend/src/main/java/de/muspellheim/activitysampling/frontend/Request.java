/*
 * Activity Sampling - Frontend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.frontend;

import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;

public abstract class Request<T> extends Task<T> {
  private final ResourceBundle resources = ResourceBundle.getBundle("ActivitySampling");

  public static void runAsync(Runnable handler) {
    runAsync(
        new Request<Void>() {
          @Override
          protected Void call() {
            handler.run();
            return null;
          }
        });
  }

  public static <T> void runAsync(Supplier<T> handler, Consumer<T> display) {
    runAsync(
        new Request<T>() {
          @Override
          protected T call() {
            return handler.get();
          }

          @Override
          protected void succeeded() {
            Optional.ofNullable(getValue()).ifPresent(display);
          }
        });
  }

  public static <T> void runAsync(Request<T> request) {
    CompletableFuture.runAsync(request);
  }

  @Override
  protected void failed() {
    getException().printStackTrace();

    var alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle(resources.getString("request.failed.title"));
    alert.setHeaderText(resources.getString("request.failed.headerText"));
    alert.setContentText(getException().getMessage());
    alert.show();
  }
}
