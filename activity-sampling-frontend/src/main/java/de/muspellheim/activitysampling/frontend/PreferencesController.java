/*
 * Activity Sampling - Frontend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.frontend;

import de.muspellheim.activitysampling.contract.MessageHandling;
import de.muspellheim.activitysampling.contract.messages.commands.ChangePreferencesCommand;
import de.muspellheim.activitysampling.contract.messages.queries.PreferencesQuery;
import de.muspellheim.activitysampling.contract.messages.queries.PreferencesQueryResult;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.text.MessageFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ChoiceBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;

public class PreferencesController {
  @FXML private Stage stage;
  @FXML private ChoiceBox<Duration> periodChoice;
  @FXML private ResourceBundle resources;

  private MessageHandling messageHandling;

  public static PreferencesController create(Stage owner, MessageHandling messageHandling) {
    try {
      var location = PreferencesController.class.getResource("PreferencesView.fxml");
      var resources = ResourceBundle.getBundle("ActivitySampling");
      var loader = new FXMLLoader(location, resources);
      loader.load();

      var controller = (PreferencesController) loader.getController();
      controller.stage.initOwner(owner);
      controller.messageHandling = messageHandling;
      return controller;
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  @FXML
  private void initialize() {
    // Build
    periodChoice.setConverter(new PeriodStringConverter());
    periodChoice.setValue(Duration.ofMinutes(20));
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
    periodChoice.getItems().setAll(periods);

    // Bind
    Stages.hookWindowCloseHandler(stage, this::handleClose);
  }

  public void run() {
    var result = messageHandling.handle(new PreferencesQuery());
    display(result);
    stage.show();
  }

  @FXML
  private void handleClose() {
    messageHandling.handle(new ChangePreferencesCommand(periodChoice.getValue()));
    stage.close();
  }

  private void display(PreferencesQueryResult result) {
    periodChoice.setValue(result.period());
  }

  private class PeriodStringConverter extends StringConverter<Duration> {
    @Override
    public String toString(Duration object) {
      if (object.toHoursPart() == 0) {
        return MessageFormat.format(
            resources.getString("preferencesView.periodChoice.item.minutes"), object.toMinutes());
      } else {
        return resources.getString("preferencesView.periodChoice.item.hour");
      }
    }

    @Override
    public Duration fromString(String string) {
      throw new UnsupportedOperationException();
    }
  }
}
