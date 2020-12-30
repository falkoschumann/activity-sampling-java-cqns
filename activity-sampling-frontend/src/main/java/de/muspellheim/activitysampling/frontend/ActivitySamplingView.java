/*
 * Activity Sampling - Frontend
 * Copyright (c) 2020 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.frontend;

import de.muspellheim.activitysampling.contract.messages.commands.LogActivityCommand;
import de.muspellheim.activitysampling.contract.messages.notifications.PeriodEndedNotification;
import de.muspellheim.activitysampling.contract.messages.notifications.PeriodProgressedNotification;
import de.muspellheim.activitysampling.contract.messages.notifications.PeriodStartedNotification;
import java.util.function.Consumer;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import lombok.Getter;
import lombok.Setter;

public class ActivitySamplingView extends VBox {
  @Getter @Setter private Consumer<LogActivityCommand> onLogActivityCommand;

  private final BooleanProperty activityFormDisabled = new SimpleBooleanProperty(true);

  private final FormInput activityInput;
  private final FormInput optionalTagsInput;
  private final PeriodProgress periodProgress;
  private final AppTrayIcon trayIcon;

  public ActivitySamplingView() {
    activityInput = new FormInput("Activity*", "What are you working on?");
    activityInput.setDisable(true);
    activityInput.disableProperty().bind(activityFormDisabled);

    optionalTagsInput = new FormInput("Optional tags", "Customer, Project, Product");
    optionalTagsInput.setDisable(true);
    optionalTagsInput.disableProperty().bind(activityFormDisabled);

    var logButton = new Button("Log");
    logButton.setMaxWidth(Double.MAX_VALUE);
    logButton.setDisable(true);
    logButton.setDefaultButton(true);
    logButton
        .disableProperty()
        .bind(activityFormDisabled.or(activityInput.valueProperty().isEmpty()));
    logButton.setOnAction(
        e -> {
          var command =
              new LogActivityCommand(activityInput.getValue(), optionalTagsInput.getValue());
          handleLogActivity(command);
        });

    periodProgress = new PeriodProgress();

    setStyle("-fx-font-family: Verdana;");
    setPadding(new Insets(Views.MARGIN));
    setSpacing(Views.UNRELATED_GAP);
    setPrefWidth(360);
    getChildren().setAll(activityInput, optionalTagsInput, logButton, periodProgress);

    trayIcon = new AppTrayIcon();
    trayIcon.setOnLogActivityCommand(it -> handleLogActivity(it));
    Platform.runLater(() -> getScene().getWindow().setOnHiding(e -> trayIcon.hide()));
  }

  public void display(PeriodStartedNotification notification) {
    Platform.runLater(() -> periodProgress.start(notification.getPeriod()));
  }

  public void display(PeriodProgressedNotification notification) {
    Platform.runLater(
        () ->
            periodProgress.progress(
                notification.getPeriod(),
                notification.getElapsedTime(),
                notification.getRemainingTime()));
  }

  public void display(PeriodEndedNotification notification) {
    Platform.runLater(
        () -> {
          activityFormDisabled.set(false);
          periodProgress.end();
          trayIcon.show();
        });
  }

  private void handleLogActivity(LogActivityCommand command) {
    activityFormDisabled.set(true);
    trayIcon.hide();
    trayIcon.setLastCommand(command);

    if (onLogActivityCommand == null) {
      return;
    }

    onLogActivityCommand.accept(command);
  }
}
