/*
 * Activity Sampling - Frontend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.frontend;

import de.muspellheim.activitysampling.contract.messages.commands.ChangePreferencesCommand;
import de.muspellheim.activitysampling.contract.messages.queries.PreferencesQueryResult;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.text.MessageFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import lombok.Getter;
import lombok.Setter;

public class PreferencesController implements Initializable {
  @Getter @Setter private Consumer<ChangePreferencesCommand> onChangePreferencesCommand;

  @FXML private Stage stage;
  @FXML private ChoiceBox<Duration> periodDurationChoice;

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
    var periods =
        new ArrayList<>(
            List.of(
                Duration.ofMinutes(15),
                Duration.ofMinutes(20),
                Duration.ofMinutes(30),
                Duration.ofHours(1)));
    if (Boolean.parseBoolean(System.getProperty("activitysampling.demo"))) {
      periods.add(0, Duration.ofMinutes(2));
    }
    periodDurationChoice.getItems().setAll(periods);

    Stages.hookWindowCloseHandler(stage, this::handleClose);
  }

  public void display(PreferencesQueryResult result) {
    periodDurationChoice.setValue(result.periodDuration());
  }

  public void run() {
    stage.show();
  }

  @FXML
  private void handleClose() {
    onChangePreferencesCommand.accept(
        new ChangePreferencesCommand(periodDurationChoice.getValue()));
    stage.close();
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
