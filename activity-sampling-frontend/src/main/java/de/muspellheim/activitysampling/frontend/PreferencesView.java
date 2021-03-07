/*
 * Activity Sampling - Frontend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.frontend;

import java.time.Duration;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.util.StringConverter;

public class PreferencesView {
  @FXML private ChoiceBox<Duration> periodDuration;
  @FXML private TextField activityLogFile;

  private final PreferencesViewModel viewModel = ViewModelFactory.getPreferencesViewModel();

  public static PreferencesView create(Stage owner) {
    var factory = new ViewControllerFactory(PreferencesView.class);

    Stage stage = new Stage();
    stage.initOwner(owner);
    stage.setTitle("Preferences");
    stage.setScene(new Scene(factory.getView()));
    stage.setMinWidth(400);
    stage.setMinHeight(120);

    return factory.getController();
  }

  public void run() {
    getWindow().show();
    viewModel.loadPreferences();
  }

  private Stage getWindow() {
    return (Stage) periodDuration.getScene().getWindow();
  }

  @FXML
  private void initialize() {
    periodDuration.setConverter(new PeriodStringConverter());
    periodDuration
        .getItems()
        .setAll(
            Duration.ofMinutes(15),
            Duration.ofMinutes(20),
            Duration.ofMinutes(30),
            Duration.ofHours(1));
    periodDuration.valueProperty().bindBidirectional(viewModel.periodDuration());

    activityLogFile.textProperty().bind(viewModel.activityLogFile().asString());
  }

  @FXML
  private void handleChangeActivityLogFile() {
    var chooser = new FileChooser();
    chooser.setTitle("Choose Activity Log File");
    chooser.setInitialFileName("activity-log.csv");
    chooser
        .getExtensionFilters()
        .addAll(
            new ExtensionFilter("Comma-Separated Values (CSV) File", "*.csv"),
            new ExtensionFilter("All Files", "*.*"));
    var file = chooser.showSaveDialog(getWindow());
    if (file != null) {
      viewModel.changeActivityLogFile(file.toPath());
    }
  }

  private static class PeriodStringConverter extends StringConverter<Duration> {
    @Override
    public String toString(Duration object) {
      if (object.toHoursPart() == 0) {
        return object.toMinutes() + " minutes";
      } else {
        return "1 hour";
      }
    }

    @Override
    public Duration fromString(String string) {
      throw new UnsupportedOperationException();
    }
  }
}
