/*
 * Activity Sampling - Frontend
 * Copyright (c) 2020 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.frontend;

import de.muspellheim.activitysampling.contract.messages.commands.LogActivityCommand;
import de.muspellheim.activitysampling.contract.messages.notifications.PeriodEndedNotification;
import de.muspellheim.activitysampling.contract.messages.notifications.PeriodProgressedNotification;
import de.muspellheim.activitysampling.contract.messages.notifications.PeriodStartedNotification;
import de.muspellheim.activitysampling.contract.util.DurationStringConverter;
import java.time.Duration;
import java.time.LocalDateTime;
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

  public static final int MARGIN = 12;
  public static final int GAP = 4;

  private final BooleanProperty enableForm;

  private final DurationStringConverter durationStringConverter;

  private final ProgressBar progressBar;
  private final Label remainingTimeLabel;

  @Getter @Setter private Consumer<LogActivityCommand> onLogActivityCommand;

  private Duration period;

  public ActivitySamplingView() {
    /*
     * Build
     */

    enableForm = new SimpleBooleanProperty(false);

    durationStringConverter = new DurationStringConverter();

    getStylesheets().add("/de/muspellheim/activitysampling/frontend/style.css");
    setPadding(new Insets(MARGIN));
    setSpacing(GAP);
    setPrefWidth(360);

    var activityLabel = new Label("Activity");
    getChildren().add(activityLabel);

    TextField activityText = new TextField();
    activityText.setPromptText("What are you working on?");
    activityText.setDisable(true);
    getChildren().add(activityText);

    var optionalTagsLabel = new Label("Optional tags");
    setMargin(optionalTagsLabel, new Insets(GAP, 0, 0, 0));
    getChildren().add(optionalTagsLabel);

    TextField optionalTagsText = new TextField();
    optionalTagsText.setPromptText("Customer, Project, Product");
    optionalTagsText.setDisable(true);
    getChildren().add(optionalTagsText);

    Button logButton = new Button("Log");
    logButton.setMaxWidth(Double.MAX_VALUE);
    logButton.setDisable(true);
    logButton.setDefaultButton(true);
    setMargin(logButton, new Insets(GAP, 0, 0, 0));
    getChildren().add(logButton);

    remainingTimeLabel = new Label("00:20:00");
    remainingTimeLabel.setMaxWidth(Double.MAX_VALUE);
    remainingTimeLabel.setAlignment(Pos.CENTER);
    setMargin(remainingTimeLabel, new Insets(GAP, 0, 0, 0));
    getChildren().add(remainingTimeLabel);

    progressBar = new ProgressBar();
    progressBar.setMaxWidth(Double.MAX_VALUE);
    progressBar.setProgress(0);
    getChildren().add(progressBar);

    /*
     * Bind
     */

    activityText.disableProperty().bind(enableForm.not());
    optionalTagsText.disableProperty().bind(enableForm.not());
    logButton.disableProperty().bind(enableForm.not().or(activityText.textProperty().isEmpty()));
    logButton.setOnAction(
        e -> {
          if (onLogActivityCommand == null) return;

          enableForm.set(false);
          LogActivityCommand command =
              new LogActivityCommand(
                  LocalDateTime.now(), period, activityText.getText(), optionalTagsText.getText());
          onLogActivityCommand.accept(command);
        });
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
          double progress =
              (double) notification.getRemainingTime().getSeconds() / period.getSeconds();
          progressBar.setProgress(progress);
        });
  }

  public void display(PeriodEndedNotification notification) {
    Platform.runLater(
        () -> {
          updateRemainingTime(Duration.ZERO);
          progressBar.setProgress(1.0);
          enableForm.set(true);
        });
  }

  private void updateRemainingTime(Duration remainingTime) {
    var text = durationStringConverter.toString(remainingTime);
    remainingTimeLabel.setText(text);
  }
}
