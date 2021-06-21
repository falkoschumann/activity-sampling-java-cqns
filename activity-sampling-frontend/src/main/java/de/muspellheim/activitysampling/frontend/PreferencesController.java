/*
 * Activity Sampling - Frontend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.frontend;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.time.Duration;
import java.util.List;
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

public class PreferencesController implements Initializable {
  private final ObjectProperty<Duration> periodDuration = new SimpleObjectProperty<>();
  private final ObjectProperty<Path> activityLogFile = new SimpleObjectProperty<>();

  @FXML private Stage stage;
  @FXML private ChoiceBox<Duration> periodDurationChoice;
  @FXML private TextField activityLogText;

  private ResourceBundle resources;

  public static PreferencesController create(Stage owner) {
    try {
      var location = PreferencesController.class.getResource("PreferencesView.fxml");
      var resources = ResourceBundle.getBundle("ActivitySampling");
      var loader = new FXMLLoader(location, resources);
      loader.load();

      var controller = (PreferencesController) loader.getController();
      controller.stage.initOwner(owner);
      return controller;
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
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

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    this.resources = resources;
    initPeriodDuration();
    initActivityLog();
  }

  private void initPeriodDuration() {
    periodDurationChoice.setConverter(
        new StringConverter<>() {
          @Override
          public String toString(Duration object) {
            if (object.toHoursPart() == 0) {
              return MessageFormat.format(
                  resources.getString("preferencesView.periodDurationChoice.item.minutes"),
                  object.toMinutes());
            } else {
              return resources.getString("preferencesView.periodDurationChoice.item.hour");
            }
          }

          @Override
          public Duration fromString(String string) {
            throw new UnsupportedOperationException();
          }
        });
    periodDurationChoice.setValue(Duration.ofMinutes(20));
    periodDurationChoice
        .getItems()
        .setAll(
            Boolean.parseBoolean(System.getProperty("demoMode"))
                ? List.of(
                    Duration.ofMinutes(2),
                    Duration.ofMinutes(15),
                    Duration.ofMinutes(20),
                    Duration.ofMinutes(30),
                    Duration.ofHours(1))
                : List.of(
                    Duration.ofMinutes(15),
                    Duration.ofMinutes(20),
                    Duration.ofMinutes(30),
                    Duration.ofHours(1)));

    periodDurationChoice.valueProperty().bindBidirectional(periodDuration);
  }

  private void initActivityLog() {
    activityLogText.textProperty().bind(activityLogFileProperty().asString());
  }

  @FXML
  private void handleChangeActivityLog() {
    var chooser = new FileChooser();
    chooser.setTitle(resources.getString("activityLogChooser.title"));
    var activityLog = Paths.get(activityLogText.getText());
    chooser.setInitialDirectory(activityLog.getParent().toAbsolutePath().toFile());
    chooser.setInitialFileName(activityLog.getFileName().toString());
    chooser
        .getExtensionFilters()
        .addAll(
            new ExtensionFilter(
                resources.getString("activityLogChooser.csvFileExtensionFilter"), "*.csv"),
            new ExtensionFilter(
                resources.getString("activityLogChooser.allFilesExtensionFilter"), "*.*"));

    var file = chooser.showSaveDialog(stage);
    if (file != null) {
      setActivityLogFile(file.toPath());
    }
  }
}
