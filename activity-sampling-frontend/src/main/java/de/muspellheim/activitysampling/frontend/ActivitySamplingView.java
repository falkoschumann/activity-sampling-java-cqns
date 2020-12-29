/*
 * Activity Sampling - Frontend
 * Copyright (c) 2020 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.frontend;

import de.muspellheim.activitysampling.contract.messages.commands.LogActivityCommand;
import de.muspellheim.activitysampling.contract.messages.notifications.PeriodEndedNotification;
import de.muspellheim.activitysampling.contract.messages.notifications.PeriodProgressedNotification;
import de.muspellheim.activitysampling.contract.messages.notifications.PeriodStartedNotification;
import java.time.Duration;
import java.util.function.Consumer;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import lombok.Getter;
import lombok.Setter;

public class ActivitySamplingView extends VBox {
  @Getter @Setter private Consumer<LogActivityCommand> onLogActivityCommand;

  private final BooleanProperty formEnabled = new SimpleBooleanProperty(false);

  private final ProgressBar progressBar;
  private final Label remainingTimeLabel;

  private Duration period;

  public ActivitySamplingView() {
    var activityLabel = new Label("Activity");

    var activityText = new TextField();
    activityText.setPromptText("What are you working on?");
    activityText.setDisable(true);
    activityText.disableProperty().bind(formEnabled.not());

    var optionalTagsLabel = new Label("Optional tags");
    VBox.setMargin(optionalTagsLabel, new Insets(Views.GAP, 0, 0, 0));

    var optionalTagsText = new TextField();
    optionalTagsText.setPromptText("Customer, Project, Product");
    optionalTagsText.setDisable(true);
    optionalTagsText.disableProperty().bind(formEnabled.not());

    var logButton = new Button("Log");
    logButton.setMaxWidth(Double.MAX_VALUE);
    logButton.setDisable(true);
    logButton.setDefaultButton(true);
    logButton.disableProperty().bind(formEnabled.not().or(activityText.textProperty().isEmpty()));
    logButton.setOnAction(
        e -> {
          if (onLogActivityCommand == null) {
            return;
          }

          formEnabled.set(false);
          var command = new LogActivityCommand(activityText.getText(), optionalTagsText.getText());
          onLogActivityCommand.accept(command);
        });
    VBox.setMargin(logButton, new Insets(Views.GAP, 0, 0, 0));

    remainingTimeLabel = new Label("00:20:00");
    remainingTimeLabel.setMaxWidth(Double.MAX_VALUE);
    remainingTimeLabel.setAlignment(Pos.CENTER);
    VBox.setMargin(remainingTimeLabel, new Insets(Views.GAP, 0, 0, 0));

    progressBar = new ProgressBar();
    progressBar.setMaxWidth(Double.MAX_VALUE);
    progressBar.setProgress(0);

    setStyle("-fx-font-family: Verdana;");
    setPadding(new Insets(Views.MARGIN));
    setSpacing(Views.GAP);
    setPrefWidth(360);
    getChildren()
        .addAll(
            activityLabel,
            activityText,
            optionalTagsLabel,
            optionalTagsText,
            logButton,
            remainingTimeLabel,
            progressBar);
  }

  public void display(PeriodStartedNotification notification) {
    Platform.runLater(
        () -> {
          period = notification.getPeriod();
          updateRemainingTime(notification.getPeriod());
          progressBar.setProgress(0.0);
        });
  }

  public void display(PeriodProgressedNotification notification) {
    Platform.runLater(
        () -> {
          updateRemainingTime(notification.getRemainingTime());
          var progress = (double) notification.getElapsedTime().getSeconds() / period.getSeconds();
          progressBar.setProgress(progress);
        });
  }

  public void display(PeriodEndedNotification notification) {
    Platform.runLater(
        () -> {
          updateRemainingTime(Duration.ZERO);
          progressBar.setProgress(1.0);
          formEnabled.set(true);
        });
  }

  private void updateRemainingTime(Duration remainingTime) {
    var text = new DurationStringConverter().toString(remainingTime);
    remainingTimeLabel.setText(text);
  }
}
