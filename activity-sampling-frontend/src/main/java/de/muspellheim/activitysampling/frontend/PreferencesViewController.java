/*
 * Activity Sampling - Frontend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.frontend;

import de.muspellheim.activitysampling.contract.messages.commands.ChangeActivityLogFileCommand;
import de.muspellheim.activitysampling.contract.messages.commands.ChangePeriodDurationCommand;
import de.muspellheim.activitysampling.contract.messages.queries.PreferencesQuery;
import de.muspellheim.activitysampling.contract.messages.queries.PreferencesQueryResult;
import java.time.Duration;
import java.util.function.Consumer;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.StringConverter;
import lombok.Getter;
import lombok.Setter;

public class PreferencesViewController {
  @Getter @Setter private Consumer<ChangePeriodDurationCommand> onChangePeriodDurationCommand;
  @Getter @Setter private Consumer<ChangeActivityLogFileCommand> onChangeActivityLogFileCommand;
  @Getter @Setter private Consumer<PreferencesQuery> onPreferencesQuery;

  @FXML private ChoiceBox<Duration> periodDuration;
  @FXML private TextField activityLogFile;

  public static PreferencesViewController create(Stage stage) {
    var factory = new ViewControllerFactory(PreferencesViewController.class);
    var scene = new Scene(factory.getView());
    stage.setScene(scene);
    stage.setTitle("Preferences");
    stage.setMinWidth(400);
    stage.setMinHeight(120);
    return factory.getController();
  }

  private Window getWindow() {
    return periodDuration.getScene().getWindow();
  }

  public void run() {
    onPreferencesQuery.accept(new PreferencesQuery());
  }

  public void display(PreferencesQueryResult result) {
    periodDuration.setValue(result.periodDuration());
    activityLogFile.setText(result.activityLogFile().toString());
  }

  @FXML
  private void initialize() {
    initializePeriodDuration();
  }

  private void initializePeriodDuration() {
    periodDuration.setConverter(
        new StringConverter<>() {
          @Override
          public String toString(Duration object) {
            if (object.toHoursPart() == 1) {
              return "1 hour";
            } else {
              return object.toMinutes() + " minutes";
            }
          }

          @Override
          public Duration fromString(String string) {
            throw new UnsupportedOperationException();
          }
        });
    periodDuration.setValue(Duration.ofMinutes(20));
    periodDuration
        .getItems()
        .setAll(
            Duration.ofMinutes(15),
            Duration.ofMinutes(20),
            Duration.ofMinutes(30),
            Duration.ofHours(1));
    periodDuration
        .valueProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              var command = new ChangePeriodDurationCommand(newValue);
              onChangePeriodDurationCommand.accept(command);
            });
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
    if (file == null) {
      return;
    }

    var command = new ChangeActivityLogFileCommand(file.toPath());
    onChangeActivityLogFileCommand.accept(command);
  }
}
