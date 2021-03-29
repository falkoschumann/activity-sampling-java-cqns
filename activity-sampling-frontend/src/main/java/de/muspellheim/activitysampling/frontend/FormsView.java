package de.muspellheim.activitysampling.frontend;

import javafx.scene.Scene;
import javafx.stage.Stage;

public class FormsView {
  private Stage stage;

  public static FormsView create(Stage stage) {
    var factory = new ViewControllerFactory(FormsView.class);

    var scene = new Scene(factory.getView());
    stage.setScene(scene);
    stage.setTitle("Forms");

    return factory.getController();
  }
}
