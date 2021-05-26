/*
 * Activity Sampling - Frontend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.frontend;

import java.net.URL;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.time.Duration;
import java.util.ResourceBundle;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import lombok.SneakyThrows;

public class PreferencesViewController implements Initializable {
  private final ObjectProperty<Duration> periodDuration = new SimpleObjectProperty<>();
  private final ObjectProperty<Path> activityLogFile = new SimpleObjectProperty<>();

  @FXML private Stage stage;
  @FXML private ChoiceBox<Duration> periodDurationChoice;
  @FXML private TextField activityLogFileText;

  private ResourceBundle resources;

  @SneakyThrows
  public static PreferencesViewController create(Stage owner) {
    var location = PreferencesViewController.class.getResource("PreferencesView.fxml");
    var resources = ResourceBundle.getBundle("ActivitySampling");
    var loader = new FXMLLoader(location, resources);
    loader.load();
    var controller = (PreferencesViewController) loader.getController();
    controller.stage.initOwner(owner);
    return controller;
  }

  public final ObjectProperty<Duration> periodDurationProperty() {
    return periodDuration;
  }

  public final Duration getPeriodDuration() {
    return periodDuration.get();
  }

  public final void setPeriodDuration(Duration value) {
    periodDuration.set(value);
  }

  public final ObjectProperty<Path> activityLogFileProperty() {
    return activityLogFile;
  }

  public final Path getActivityLogFile() {
    return activityLogFile.get();
  }

  public final void setActivityLogFile(Path value) {
    activityLogFile.set(value);
  }

  public void run() {
    stage.show();
  }

  public void initialize(URL location, ResourceBundle resources) {
    this.resources = resources;

    periodDurationChoice.valueProperty().bindBidirectional(periodDuration);
    periodDurationChoice.setConverter(new PeriodStringConverter());
    periodDurationChoice
        .getItems()
        .setAll(
            Duration.ofMinutes(1),
            Duration.ofMinutes(5),
            Duration.ofMinutes(10),
            Duration.ofMinutes(15),
            Duration.ofMinutes(20),
            Duration.ofMinutes(30),
            Duration.ofHours(1));

    activityLogFileText.textProperty().bind(activityLogFile.asString());
  }

  @FXML
  private void changeActivityLogFile() {
    var chooser = new FileChooser();
    chooser.setTitle("Choose Activity Log File");
    chooser.setInitialFileName("activity-log.csv");
    chooser
        .getExtensionFilters()
        .addAll(
            new ExtensionFilter("Comma-Separated Values (CSV) File", "*.csv"),
            new ExtensionFilter("All Files", "*.*"));
    var file = chooser.showSaveDialog(stage);
    if (file != null) {
      activityLogFile.set(file.toPath());
    }
  }

  private class PeriodStringConverter extends StringConverter<Duration> {
    @Override
    public String toString(Duration object) {
      if (object.toHoursPart() == 0) {
        return MessageFormat.format(resources.getString("preferences.minutes"), object.toMinutes());
      } else {
        return resources.getString("preferences.hour");
      }
    }

    @Override
    public Duration fromString(String string) {
      throw new UnsupportedOperationException();
    }
  }
}
