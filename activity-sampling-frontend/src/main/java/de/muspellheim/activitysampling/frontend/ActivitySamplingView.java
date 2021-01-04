/*
 * Activity Sampling - Frontend
 * Copyright (c) 2020 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.frontend;

import de.muspellheim.activitysampling.contract.messages.commands.LogActivityCommand;
import de.muspellheim.activitysampling.contract.messages.queries.ActivityLogQuery;
import de.muspellheim.activitysampling.contract.messages.queries.ActivityLogQueryResult;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.function.Consumer;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import lombok.Getter;
import lombok.Setter;

public class ActivitySamplingView extends VBox {
  @Getter @Setter private Consumer<LogActivityCommand> onLogActivityCommand;
  @Getter @Setter private Consumer<ActivityLogQuery> onActivityLogQuery;

  private final BooleanProperty activityFormDisabled = new SimpleBooleanProperty(true);

  private final FormInput activityInput;
  private final FormInput optionalTagsInput;
  private final PeriodProgress periodProgress;
  private final ActivityLog activityLog;
  private final AppTrayIcon trayIcon;
  private final SystemClock clock;

  private Duration period;
  private LocalDateTime timestamp;

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
              new LogActivityCommand(
                  timestamp, period, activityInput.getValue(), optionalTagsInput.getValue());
          handleLogActivity(command);
        });

    periodProgress = new PeriodProgress();

    activityLog = new ActivityLog();
    VBox.setVgrow(activityLog, Priority.ALWAYS);

    setStyle("-fx-font-family: Verdana;");
    setPadding(new Insets(Views.MARGIN));
    setSpacing(Views.UNRELATED_GAP);
    setPrefSize(360, 640);
    getChildren().setAll(activityInput, optionalTagsInput, logButton, periodProgress, activityLog);

    var periodCheck = new PeriodCheck(Duration.ofMinutes(1));
    periodCheck.setOnPeriodStarted(it -> periodStarted(it));
    periodCheck.setOnPeriodProgressed(it -> periodProgressed(it));
    periodCheck.setOnPeriodEnded(it -> periodEnded(it));

    clock = new SystemClock();
    clock.setOnTick(it -> periodCheck.check(it));

    trayIcon = new AppTrayIcon();
    trayIcon.setOnLogActivityCommand(it -> handleLogActivity(it));
    Platform.runLater(() -> getScene().getWindow().setOnHiding(e -> trayIcon.hide()));
  }

  public void run() {
    clock.run();
    onActivityLogQuery.accept(new ActivityLogQuery());
  }

  public void display(ActivityLogQueryResult result) {
    activityLog.display(result.getLog());
  }

  private void periodStarted(Duration period) {
    this.period = period;
    Platform.runLater(() -> periodProgress.start(period));
  }

  private void periodProgressed(Duration elapsedTime) {
    var remainingTime = period.minus(elapsedTime);
    Platform.runLater(() -> periodProgress.progress(period, elapsedTime, remainingTime));
  }

  private void periodEnded(LocalDateTime timestamp) {
    this.timestamp = timestamp;
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
