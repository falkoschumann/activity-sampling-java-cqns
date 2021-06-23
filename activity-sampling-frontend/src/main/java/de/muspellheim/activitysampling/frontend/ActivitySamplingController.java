/*
 * Activity Sampling - Frontend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.frontend;

import de.muspellheim.activitysampling.contract.data.Activity;
import de.muspellheim.activitysampling.contract.messages.commands.ChangeActivityLogFileCommand;
import de.muspellheim.activitysampling.contract.messages.commands.ChangePeriodDurationCommand;
import de.muspellheim.activitysampling.contract.messages.commands.LogActivityCommand;
import de.muspellheim.activitysampling.contract.messages.queries.ActivityLogQuery;
import de.muspellheim.activitysampling.contract.messages.queries.ActivityLogQueryResult;
import de.muspellheim.activitysampling.contract.messages.queries.PreferencesQuery;
import de.muspellheim.activitysampling.contract.messages.queries.PreferencesQueryResult;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import lombok.Getter;
import lombok.Setter;

public class ActivitySamplingController {
  @Getter @Setter private Consumer<LogActivityCommand> onLogActivityCommand;
  @Getter @Setter private Consumer<ChangePeriodDurationCommand> onChangePeriodDurationCommand;
  @Getter @Setter private Consumer<ChangeActivityLogFileCommand> onChangeActivityLogFileCommand;
  @Getter @Setter private Consumer<PreferencesQuery> onPreferencesQuery;
  @Getter @Setter private Consumer<ActivityLogQuery> onActivityLogQuery;

  @FXML private Stage stage;
  @FXML private MenuBar menuBar;
  @FXML private SeparatorMenuItem quitSeparatorMenuItem;
  @FXML private MenuItem quitMenuItem;
  @FXML private TextField activityText;
  @FXML private TextField tagsText;
  @FXML private SplitMenuButton logButton;
  @FXML private Label remainingTimeLabel;
  @FXML private ProgressBar progressBar;
  @FXML private TextArea activityLogText;
  private TrayIconController trayIconViewController;

  private ActivitySamplingModel model;

  public static ActivitySamplingController create(Stage stage) {
    try {
      var location = ActivitySamplingController.class.getResource("ActivitySamplingView.fxml");
      var resources = ResourceBundle.getBundle("ActivitySampling");
      var loader = new FXMLLoader(location, resources);
      loader.setRoot(stage);
      loader.load();
      return loader.getController();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  @FXML
  private void initialize() {
    model = new ActivitySamplingModel();
    if (model.isRunningOnMac()) {
      menuBar.setUseSystemMenuBar(true);
      quitSeparatorMenuItem.setVisible(false);
      quitMenuItem.setVisible(false);
    }
    trayIconViewController = new TrayIconController();

    activityText.textProperty().bindBidirectional(model.activityProperty());
    activityText.disableProperty().bind(model.formDisabledProperty());
    tagsText.textProperty().bindBidirectional(model.tagsProperty(), new TagsStringConverter());
    tagsText.disableProperty().bind(model.formDisabledProperty());
    logButton.disableProperty().bind(model.formUnsubmittableBinding());
    remainingTimeLabel
        .textProperty()
        .bindBidirectional(model.remainingTimeProperty(), new RemainingTimeStringConverter());
    progressBar.progressProperty().bind(model.periodProgressBinding());
    activityLogText.textProperty().bindBidirectional(model.logProperty(), new LogStringConverter());
    trayIconViewController.setOnActivitySelected(this::handleLogActivity);
    Platform.runLater(() -> stage.setOnHiding(e -> trayIconViewController.hide()));
    model
        .formDisabledProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              if (oldValue && !newValue) {
                trayIconViewController.show();
                Platform.runLater(() -> activityText.requestFocus());
              } else if (!oldValue && newValue) {
                trayIconViewController.hide();
              }
            });
  }

  public void run() {
    var systemClock = new Timer(true);
    systemClock.schedule(
        new TimerTask() {
          @Override
          public void run() {
            Platform.runLater(
                () -> {
                  var currentTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
                  model.progressPeriod(currentTime);
                });
          }
        },
        0,
        1000);

    onPreferencesQuery.accept(new PreferencesQuery());
    onActivityLogQuery.accept(new ActivityLogQuery());

    stage.show();
  }

  public void display(PreferencesQueryResult result) {
    model.setActivityLogFile(result.activityLogFile());
    model.setPeriodDuration(result.periodDuration());
  }

  public void display(ActivityLogQueryResult result) {
    model.setLog(result.log());
    model.setRecent(result.recent());
    model.setActivity(result.last().activity());
    model.setTags(result.last().tags());

    var recent =
        model.getRecent().stream()
            .map(it -> new ActivityTemplate(it.activity(), it.tags()))
            .toList();
    var converter = new ActivityTemplateStringConverter();
    logButton
        .getItems()
        .setAll(
            recent.stream()
                .map(
                    it -> {
                      var menuItem = new MenuItem(converter.toString(it));
                      menuItem.setOnAction(e -> handleLogActivity(it));
                      return menuItem;
                    })
                .toList());
    Platform.runLater(() -> activityLogText.setScrollTop(Double.MAX_VALUE));
    trayIconViewController.setRecent(recent);
  }

  @FXML
  private void handleOpenPreferences() {
    var controller = PreferencesController.create(stage);
    controller.setPeriodDuration(model.getPeriodDuration());
    controller.setActivityLogFile(model.getActivityLogFile());

    controller
        .periodDurationProperty()
        .addListener(
            observable ->
                onChangePeriodDurationCommand.accept(
                    new ChangePeriodDurationCommand(controller.getPeriodDuration())));
    controller
        .activityLogFileProperty()
        .addListener(
            observable ->
                onChangeActivityLogFileCommand.accept(
                    new ChangeActivityLogFileCommand(controller.getActivityLogFile())));

    controller.run();
  }

  @FXML
  private void handleQuit() {
    Platform.exit();
  }

  @FXML
  private void handleOpenAbout() {
    var controller = AboutController.create(stage);
    controller.run();
  }

  @FXML
  private void handleLogActivity() {
    logActivity();
  }

  private void handleLogActivity(ActivityTemplate template) {
    model.setActivity(template.activity());
    model.setTags(template.tags());
    logActivity();
  }

  private void logActivity() {
    model.setFormDisabled(true);
    onLogActivityCommand.accept(
        new LogActivityCommand(
            model.getPeriodEnd(), model.getPeriodDuration(), model.getActivity(), model.getTags()));
  }

  static class RemainingTimeStringConverter extends StringConverter<Duration> {
    @Override
    public String toString(Duration object) {
      return String.format(
          "%1$02d:%2$02d:%3$02d",
          object.toHoursPart(), object.toMinutesPart(), object.toSecondsPart());
    }

    @Override
    public Duration fromString(String string) {
      throw new UnsupportedOperationException();
    }
  }

  static class LogStringConverter extends StringConverter<List<Activity>> {
    @Override
    public String toString(List<Activity> object) {
      var dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL);
      var timeFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT);
      var logBuilder = new StringBuilder();
      var tagsConverter = new TagsStringConverter();
      for (int i = 0; i < object.size(); i++) {
        var activity = object.get(i);
        if (i == 0) {
          logBuilder.append(dateFormatter.format(activity.timestamp()));
          logBuilder.append("\n");
        } else {
          var lastActivity = object.get(i - 1);
          if (!lastActivity.timestamp().toLocalDate().equals(activity.timestamp().toLocalDate())) {
            logBuilder.append(dateFormatter.format(activity.timestamp()));
            logBuilder.append("\n");
          }
        }

        logBuilder.append(timeFormatter.format(activity.timestamp()));
        logBuilder.append(" - ");
        String activityText = activity.activity();
        if (!activity.tags().isEmpty()) {
          activityText = "[" + tagsConverter.toString(activity.tags()) + "] " + activityText;
        }
        logBuilder.append(activityText);
        logBuilder.append("\n");
      }
      return logBuilder.toString();
    }

    @Override
    public List<Activity> fromString(String string) {
      throw new UnsupportedOperationException();
    }
  }
}
