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
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
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

  public ActivitySamplingView(boolean useSystemMenuBar) {
    var preferencesMenuItem = new MenuItem("Preferences");
    preferencesMenuItem.setOnAction(
        e -> {
          // TODO Stage in App öffnen

          var view = new PreferencesView();

          var scene = new Scene(view, 569, 320);

          var stage = new Stage();
          stage.initOwner(getScene().getWindow());
          stage.setScene(scene);
          stage.setTitle("Preferences");
          stage.setMinWidth(400);
          stage.setMinHeight(120);
          stage.show();
        });

    var exitMenuItem = new MenuItem("Exit");
    exitMenuItem.setOnAction(e -> Platform.exit());

    var fileMenu = new Menu("File");
    fileMenu.getItems().setAll(preferencesMenuItem, new SeparatorMenuItem(), exitMenuItem);

    var menuBar = new MenuBar(fileMenu);
    menuBar.setUseSystemMenuBar(useSystemMenuBar);

    activityForm = new ActivityForm();
    activityForm.setDisable(true);
    activityForm.setOnActivitySelected(it -> logActivity(it));

    periodProgress = new PeriodProgress();

    activityLog = new ActivityLog();
    VBox.setVgrow(activityLog, Priority.ALWAYS);

    var main = new VBox();
    main.setPadding(new Insets(Views.MARGIN));
    main.setSpacing(Views.UNRELATED_GAP);
    main.getChildren().setAll(activityForm, periodProgress, activityLog);
    VBox.setVgrow(main, Priority.ALWAYS);

    setStyle("-fx-font-family: Verdana;");
    setPrefSize(360, 640);
    getChildren().setAll(menuBar, main);

    var periodCheck = new PeriodCheck();
    periodCheck.setOnPeriodStarted(it -> periodStarted(it));
    periodCheck.setOnPeriodProgressed(it -> periodProgressed(it));
    periodCheck.setOnPeriodEnded(it -> periodEnded(it));

    // TODO Clock nach außenlegen und in App auch deaktivieren
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
    Platform.runLater(
        () -> {
          activityForm.getRecentActivities().setAll(result.getRecent());
          activityLog.getActivities().setAll(result.getLog());
        });
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
