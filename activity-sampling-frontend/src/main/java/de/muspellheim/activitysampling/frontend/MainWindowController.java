/*
 * Activity Sampling - Frontend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.frontend;

import de.muspellheim.activitysampling.contract.data.ActivityTemplate;
import de.muspellheim.activitysampling.contract.data.Bounds;
import de.muspellheim.activitysampling.contract.messages.commands.ChangeMainWindowBoundsCommand;
import de.muspellheim.activitysampling.contract.messages.commands.ChangePreferencesCommand;
import de.muspellheim.activitysampling.contract.messages.commands.Failure;
import de.muspellheim.activitysampling.contract.messages.commands.LogActivityCommand;
import de.muspellheim.activitysampling.contract.messages.queries.ActivityLogQuery;
import de.muspellheim.activitysampling.contract.messages.queries.ActivityLogQueryResult;
import de.muspellheim.activitysampling.contract.messages.queries.MainWindowBoundsQuery;
import de.muspellheim.activitysampling.contract.messages.queries.MainWindowBoundsQueryResult;
import de.muspellheim.activitysampling.contract.messages.queries.PreferencesQuery;
import de.muspellheim.activitysampling.contract.messages.queries.PreferencesQueryResult;
import de.muspellheim.activitysampling.contract.messages.queries.WorkingHoursByActivityQuery;
import de.muspellheim.activitysampling.contract.messages.queries.WorkingHoursByActivityQueryResult;
import de.muspellheim.activitysampling.contract.messages.queries.WorkingHoursByNumberQuery;
import de.muspellheim.activitysampling.contract.messages.queries.WorkingHoursByNumberQueryResult;
import de.muspellheim.activitysampling.contract.messages.queries.WorkingHoursThisWeekQuery;
import de.muspellheim.activitysampling.contract.messages.queries.WorkingHoursThisWeekQueryResult;
import de.muspellheim.activitysampling.contract.messages.queries.WorkingHoursTodayQuery;
import de.muspellheim.activitysampling.contract.messages.queries.WorkingHoursTodayQueryResult;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Screen;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;

public class MainWindowController {
  @Getter @Setter private Consumer<ChangeMainWindowBoundsCommand> onChangeMainWindowBoundsCommand;
  @Getter @Setter private Consumer<MainWindowBoundsQuery> onMainWindowBoundsQuery;
  @Getter @Setter private Consumer<PreferencesQuery> onPreferencesQuery;
  @Getter @Setter private Consumer<ActivityLogQuery> onActivityLogQuery;

  @FXML private Stage stage;
  @FXML private MenuBar menuBar;
  @FXML private TextField activityText;
  @FXML private TextField tagsText;
  @FXML private MenuButton addTagButton;
  @FXML private SplitMenuButton logButton;
  @FXML private Label remainingTimeLabel;
  @FXML private ProgressBar progressBar;
  @FXML private TextArea activityLogText;

  private TrayIconController trayIconViewController;
  private PreferencesController preferencesController;
  private WorkingHoursTodayController workingHoursTodayController;
  private WorkingHoursThisWeekController workingHoursThisWeekController;
  private WorkingHoursByActivityController workingHoursByActivityController;
  private WorkingHoursByNumberController workingHoursByNumberController;

  private final Timer timer = new Timer(true);
  private MainWindowModel model;

  public static MainWindowController create(Stage stage) {
    try {
      var location = MainWindowController.class.getResource("MainWindowView.fxml");
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
    menuBar.setUseSystemMenuBar(true);
    trayIconViewController = new TrayIconController();
    preferencesController = PreferencesController.create(stage);
    workingHoursTodayController = WorkingHoursTodayController.create(stage);
    workingHoursThisWeekController = WorkingHoursThisWeekController.create(stage);
    workingHoursByActivityController = WorkingHoursByActivityController.create(stage);
    workingHoursByNumberController = WorkingHoursByNumberController.create(stage);
    model = new MainWindowModel();

    activityText.textProperty().bindBidirectional(model.activityProperty());
    activityText.disableProperty().bind(model.formDisabledProperty());
    tagsText.textProperty().bindBidirectional(model.tagsProperty(), new TagsStringConverter());
    tagsText.disableProperty().bind(model.formDisabledProperty());
    addTagButton.disableProperty().bind(model.addTagButtonDisabledBinding());
    logButton.disableProperty().bind(model.logButtonDisabledBinding());
    remainingTimeLabel.textProperty().bind(model.remainingTimeProperty().asString());
    progressBar.progressProperty().bind(model.periodProgressBinding());
    activityLogText.textProperty().bind(model.logProperty());
    activityLogText.textProperty().addListener(o -> scrollLogToBottom());
    trayIconViewController.visibleProperty().bind(model.trayIconVisibleProperty());
    trayIconViewController.setOnActivitySelected(model::logActivity);
    model.recentActivitiesProperty().addListener(o -> updateRecentActivities());
    model.recentTagsProperty().addListener(o -> updateRecentTags());
    model.setOnPeriodEnded(this::handlePeriodEnded);
    stage.setOnHiding(e -> model.dispose());
  }

  private void scrollLogToBottom() {
    timer.schedule(
        new TimerTask() {
          @Override
          public void run() {
            Platform.runLater(() -> activityLogText.setScrollTop(Double.MAX_VALUE));
          }
        },
        200);
  }

  private void updateRecentActivities() {
    {
      var recent =
          model.getRecentActivities().stream()
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
                        menuItem.setOnAction(e -> model.logActivity(it));
                        return menuItem;
                      })
                  .toList());
      trayIconViewController.setRecent(recent);
    }
  }

  private void updateRecentTags() {
    addTagButton
        .getItems()
        .setAll(
            model.getRecentTags().stream()
                .map(
                    it -> {
                      var menuItem = new MenuItem(it);
                      menuItem.setOnAction(e -> model.addTag(it));
                      return menuItem;
                    })
                .toList());
  }

  private void handlePeriodEnded() {
    Platform.runLater(
        () -> {
          activityText.requestFocus();
          trayIconViewController.showQuestion();
        });
  }

  public void setOnChangePreferencesCommand(
      Consumer<ChangePreferencesCommand> onChangePreferencesCommand) {
    preferencesController.setOnChangePreferencesCommand(onChangePreferencesCommand);
  }

  public void setOnLogActivityCommand(Consumer<LogActivityCommand> onLogActivityCommand) {
    model.setOnLogActivityCommand(onLogActivityCommand);
  }

  public void setOnWorkingHoursTodayQuery(
      Consumer<WorkingHoursTodayQuery> onWorkingHoursTodayQuery) {
    workingHoursTodayController.setOnWorkingHoursTodayQuery(onWorkingHoursTodayQuery);
  }

  public void setOnWorkingHoursThisWeekQuery(
      Consumer<WorkingHoursThisWeekQuery> onWorkingHoursThisWeekQuery) {
    workingHoursThisWeekController.setOnWorkingHoursThisWeekQuery(onWorkingHoursThisWeekQuery);
  }

  public void setOnWorkingHoursByActivityQuery(
      Consumer<WorkingHoursByActivityQuery> onWorkingHoursByActivityQuery) {
    workingHoursByActivityController.setOnWorkingHoursByActivityQuery(
        onWorkingHoursByActivityQuery);
  }

  public void setOnWorkingHoursByNumberQuery(
      Consumer<WorkingHoursByNumberQuery> onWorkingHoursByNumberQuery) {
    workingHoursByNumberController.setOnWorkingHoursByNumberQuery(onWorkingHoursByNumberQuery);
  }

  public void run() {
    onMainWindowBoundsQuery.accept(new MainWindowBoundsQuery());
    onPreferencesQuery.accept(new PreferencesQuery());
    onActivityLogQuery.accept(new ActivityLogQuery());
    startPeriod();
  }

  private void startPeriod() {
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
  }

  public void display(MainWindowBoundsQueryResult result) {
    if (!Bounds.NULL.equals(result.bounds())) {
      var x = result.bounds().x();
      var y = result.bounds().y();
      var width = result.bounds().width();
      var height = result.bounds().height();
      if (!Screen.getScreensForRectangle(x, y, width, height).isEmpty()) {
        stage.setX(x);
        stage.setY(y);
        stage.setWidth(width);
        stage.setHeight(height);
      }
    }
    stage.show();
  }

  public void display(PreferencesQueryResult result) {
    preferencesController.display(result);
    model.display(result);
  }

  public void display(ActivityLogQueryResult result) {
    model.display(result);
  }

  public void display(WorkingHoursTodayQueryResult result) {
    workingHoursTodayController.display(result);
  }

  public void display(WorkingHoursThisWeekQueryResult result) {
    workingHoursThisWeekController.display(result);
  }

  public void display(WorkingHoursByActivityQueryResult result) {
    workingHoursByActivityController.display(result);
  }

  public void display(WorkingHoursByNumberQueryResult result) {
    workingHoursByNumberController.display(result);
  }

  public void display(Failure failure) {
    var index = failure.errorMessage().indexOf(": ");
    var header = index == -1 ? null : failure.errorMessage().substring(0, index);
    var content =
        index == -1 ? failure.errorMessage() : failure.errorMessage().substring(index + 1);

    var alert = new Alert(AlertType.ERROR);
    alert.initOwner(stage);
    alert.setHeaderText(header);
    alert.setContentText(content);
    alert.show();
  }

  @FXML
  private void handleOpenPreferences() {
    preferencesController.run();
  }

  @FXML
  private void handleClose() {
    onChangeMainWindowBoundsCommand.accept(
        new ChangeMainWindowBoundsCommand(
            new Bounds(stage.getX(), stage.getY(), stage.getWidth(), stage.getHeight())));
    stage.close();
  }

  @FXML
  private void handleWorkingHoursToday() {
    workingHoursTodayController.run();
  }

  @FXML
  private void handleWorkingHoursThisWeek() {
    workingHoursThisWeekController.run();
  }

  @FXML
  private void handleWorkingHoursByActivity() {
    workingHoursByActivityController.run();
  }

  @FXML
  private void handleWorkingHoursByNumber() {
    workingHoursByNumberController.run();
  }

  @FXML
  private void handleOpenAbout() {
    var controller = AboutController.create(stage);
    controller.run();
  }

  @FXML
  private void handleLogActivity() {
    model.logActivity();
  }
}
