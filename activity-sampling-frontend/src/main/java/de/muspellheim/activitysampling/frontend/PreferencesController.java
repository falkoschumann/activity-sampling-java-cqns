/*
 * Activity Sampling - Frontend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.frontend;

import de.muspellheim.activitysampling.contract.messages.commands.ChangeActivityLogFileCommand;
import de.muspellheim.activitysampling.contract.messages.commands.ChangePeriodDurationCommand;
import de.muspellheim.activitysampling.contract.messages.queries.PreferencesQueryResult;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.time.Duration;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import lombok.Getter;
import lombok.Setter;

public class PreferencesController implements Initializable {
  @Getter @Setter private Consumer<ChangePeriodDurationCommand> onChangePeriodDurationCommand;
  @Getter @Setter private Consumer<ChangeActivityLogFileCommand> onChangeActivityLogFileCommand;

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

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    this.resources = resources;
    periodDurationChoice.setConverter(new PeriodDurationStringConverter());
    periodDurationChoice.setValue(Duration.ofMinutes(20));
    periodDurationChoice
        .getItems()
        .setAll(
            Boolean.parseBoolean(System.getProperty("demoMode"))
                ? List.of(
                    Duration.ofMinutes(1),
                    Duration.ofMinutes(15),
                    Duration.ofMinutes(20),
                    Duration.ofMinutes(30),
                    Duration.ofHours(1))
                : List.of(
                    Duration.ofMinutes(15),
                    Duration.ofMinutes(20),
                    Duration.ofMinutes(30),
                    Duration.ofHours(1)));

    Stages.hookCloseHandler(stage);
  }

  public void display(PreferencesQueryResult result) {
    periodDurationChoice.setValue(result.periodDuration());
    activityLogText.setText(result.activityLogFile());
  }

  public void run() {
    stage.show();
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
      onChangeActivityLogFileCommand.accept(
          new ChangeActivityLogFileCommand(file.getAbsolutePath()));
    }
  }

  private class PeriodDurationStringConverter extends StringConverter<Duration> {
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
  }
}
