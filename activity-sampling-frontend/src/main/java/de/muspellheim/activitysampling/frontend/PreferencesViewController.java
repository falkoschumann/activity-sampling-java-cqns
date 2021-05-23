/*
 * Activity Sampling - Frontend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.frontend;

import de.muspellheim.activitysampling.contract.messages.commands.ChangeActivityLogFileCommand;
import de.muspellheim.activitysampling.contract.messages.commands.ChangePeriodDurationCommand;
import de.muspellheim.activitysampling.contract.messages.queries.SettingsQueryResult;
import java.time.Duration;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

public class PreferencesViewController {
  @Getter @Setter private Consumer<ChangePeriodDurationCommand> onChangePeriodDurationCommand;
  @Getter @Setter private Consumer<ChangeActivityLogFileCommand> onChangeActivityLogFileCommand;

  @FXML private Stage stage;
  @FXML private ChoiceBox<Duration> periodDurationChoice;
  @FXML private TextField activityLogFileText;

  @SneakyThrows
  public static PreferencesViewController create(Stage owner) {
    var location = MainViewController.class.getResource("MainView.fxml");
    var resources = ResourceBundle.getBundle("ActivitySampling");
    var loader = new FXMLLoader(location, resources);
    loader.load();
    var controller = (PreferencesViewController) loader.getController();
    controller.stage.initOwner(owner);
    return controller;
  }

  public void run() {
    stage.show();
    // TODO viewModel.loadPreferences();
  }

  @FXML
  private void initialize() {
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
  }

  public void display(SettingsQueryResult result) {
    System.out.println(result);
    periodDurationChoice.setValue(result.periodDuration());
    activityLogFileText.setText(result.activityLogFile().toString());
  }

  @FXML
  private void handleChangePeriodDuration() {
    onChangePeriodDurationCommand.accept(
        new ChangePeriodDurationCommand(periodDurationChoice.getValue()));
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
    var file = chooser.showSaveDialog(stage);
    if (file != null) {
      onChangeActivityLogFileCommand.accept(new ChangeActivityLogFileCommand(file.toPath()));
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
