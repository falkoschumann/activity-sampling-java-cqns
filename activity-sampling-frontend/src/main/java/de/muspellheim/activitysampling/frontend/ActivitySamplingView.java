/*
 * Activity Sampling - Frontend
 * Copyright (c) 2020 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.frontend;

import de.muspellheim.activitysampling.contract.data.Activity;
import de.muspellheim.activitysampling.contract.messages.commands.LogActivityCommand;
import de.muspellheim.activitysampling.contract.messages.queries.ActivityLogQuery;
import de.muspellheim.activitysampling.contract.messages.queries.ActivityLogQueryResult;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.function.Consumer;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import lombok.Getter;
import lombok.Setter;

public class ActivitySamplingView extends VBox {
  @Getter @Setter private Consumer<LogActivityCommand> onLogActivityCommand;
  @Getter @Setter private Consumer<ActivityLogQuery> onActivityLogQuery;

  private final ActivityForm activityForm;
  private final PeriodProgress periodProgress;
  private final ActivityLog activityLog;
  private final AppTrayIcon trayIcon;
  private final SystemClock clock;

  private Duration period;
  private LocalDateTime timestamp;

  public ActivitySamplingView() {
    activityForm = new ActivityForm();
    activityForm.setDisable(true);
    activityForm.setOnActivitySelected(it -> logActivity(it));

    periodProgress = new PeriodProgress();

    activityLog = new ActivityLog();
    VBox.setVgrow(activityLog, Priority.ALWAYS);

    setStyle("-fx-font-family: Verdana;");
    setPadding(new Insets(Views.MARGIN));
    setSpacing(Views.UNRELATED_GAP);
    setPrefSize(360, 640);
    getChildren().setAll(activityForm, periodProgress, activityLog);

    var periodCheck = new PeriodCheck();
    periodCheck.setOnPeriodStarted(it -> periodStarted(it));
    periodCheck.setOnPeriodProgressed(it -> periodProgressed(it));
    periodCheck.setOnPeriodEnded(it -> periodEnded(it));

    clock = new SystemClock();
    clock.setOnTick(it -> periodCheck.check(it));

    trayIcon = new AppTrayIcon();
    trayIcon.setOnActivitySelected(it -> logActivity(it));
    Platform.runLater(() -> getScene().getWindow().setOnHiding(e -> trayIcon.hide()));
  }

  public void run() {
    clock.run();
    onActivityLogQuery.accept(new ActivityLogQuery());
  }

  public void display(ActivityLogQueryResult result) {
    activityForm.display(result.getRecent());
    activityLog.display(result.getLog());
    trayIcon.display(result.getRecent());
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
          activityForm.setDisable(false);
          periodProgress.end();
          trayIcon.show();
        });
  }

  private void logActivity(Activity activity) {
    activityForm.setDisable(true);
    trayIcon.hide();

    if (onLogActivityCommand == null) {
      return;
    }

    var command =
        new LogActivityCommand(timestamp, period, activity.getActivity(), activity.getTags());
    onLogActivityCommand.accept(command);
  }
}
