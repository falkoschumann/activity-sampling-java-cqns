/*
 * Activity Sampling - Frontend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.frontend;

import de.muspellheim.activitysampling.contract.messages.commands.ChangeActivityLogFileCommand;
import de.muspellheim.activitysampling.contract.messages.commands.ChangePeriodDurationCommand;
import de.muspellheim.activitysampling.contract.messages.commands.LogActivityCommand;
import de.muspellheim.activitysampling.contract.messages.queries.ActivityLogQuery;
import de.muspellheim.activitysampling.contract.messages.queries.ActivityLogQueryResult;
import de.muspellheim.activitysampling.contract.messages.queries.RecentActivitiesQuery;
import de.muspellheim.activitysampling.contract.messages.queries.RecentActivitiesQueryResult;
import de.muspellheim.activitysampling.contract.messages.queries.SettingsQuery;
import de.muspellheim.activitysampling.contract.messages.queries.SettingsQueryResult;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

public class MainView {
  @Getter @Setter private Consumer<LogActivityCommand> onLogActivityCommand;
  @Getter @Setter private Consumer<ChangePeriodDurationCommand> onChangePeriodDurationCommand;
  @Getter @Setter private Consumer<ChangeActivityLogFileCommand> onChangeActivityLogFileCommand;
  @Getter @Setter private Consumer<ActivityLogQuery> onActivityLogQuery;
  @Getter @Setter private Consumer<RecentActivitiesQuery> onRecentActivitiesQuery;
  @Getter @Setter private Consumer<SettingsQuery> onSettingsQuery;

  @FXML private VBox activityForm;
  @FXML private TextField activityText;
  @FXML private TextField tagsText;
  @FXML private SplitMenuButton logButton;
  @FXML private Label progressLabel;
  @FXML private ProgressBar progressBar;
  @FXML private TextArea activityLogText;

  private final AppTrayIcon trayIcon = new AppTrayIcon();
  private final SystemClock clock = new SystemClock();

  @SneakyThrows
  public static MainView create(Stage stage) {
    var location = MainView.class.getResource("MainView.fxml");
    var resources = ResourceBundle.getBundle("ActivitySampling");
    var loader = new FXMLLoader(location, resources);
    loader.setRoot(stage);
    loader.load();
    return loader.getController();
  }

  public void run() {
    getWindow().show();
    onSettingsQuery.accept(new SettingsQuery());
    onActivityLogQuery.accept(new ActivityLogQuery());
    onRecentActivitiesQuery.accept(new RecentActivitiesQuery());
    clock.run();
  }

  public void display(ActivityLogQueryResult result) {
    var log = new ActivityLogRenderer().toString(result.activities());
    activityLogText.setText(log);
    Platform.runLater(() -> activityLogText.setScrollTop(Double.MAX_VALUE));
  }

  public void display(RecentActivitiesQueryResult result) {
    var menuItems =
        result.activities().stream()
            .map(
                it -> {
                  var menuItem = new MenuItem(new ActivityStringConverter().toString(it));
                  menuItem.setOnAction(
                      e ->
                          onLogActivityCommand.accept(
                              new LogActivityCommand(
                                  it.timestamp(), it.period(), it.activity(), it.tags())));
                  return menuItem;
                })
            .collect(Collectors.toList());
    Platform.runLater(() -> logButton.getItems().setAll(menuItems));

    if (!result.activities().isEmpty()) {
      var lastActivity = result.activities().get(0);
      activityText.setText(lastActivity.activity());

      tagsText.setText(new TagsStringConverter().toString(lastActivity.tags()));
    }
  }

  public void display(SettingsQueryResult result) {
    // periodDuration.setValue(result.periodDuration());
    // activityLogFile.setValue(result.activityLogFile().toString());
  }

  private Stage getWindow() {
    return (Stage) activityText.getScene().getWindow();
  }

  @FXML
  private void initialize() {
    /*
    activityForm.disableProperty().bind(viewModel.formDisabledProperty());
    activityForm.disableProperty().addListener(observable -> activityText.requestFocus());

    progressLabel.textProperty().bind(viewModel.remainingTimeProperty());
    progressBar.progressProperty().bind(viewModel.progressProperty());

    activityLogText.textProperty().bind(viewModel.activityLogProperty());
    activityLogText
        .textProperty()
        .addListener(
            observable -> Platform.runLater(() -> activityLogText.setScrollTop(Double.MAX_VALUE)));

    Platform.runLater(() -> getWindow().setOnHiding(e -> trayIcon.hide()));

    clock.setOnTick(it -> Platform.runLater(() -> viewModel.clockTicked(it)));
     */
  }

  @FXML
  private void openSettings() {
    var preferencesView = SettingsView.create(getWindow());
    preferencesView.run();
  }

  @FXML
  private void openInfo() {
    var infoView = InfoView.create(getWindow());
    infoView.run();
  }

  @FXML
  private void logActivity() {
    var tags = new TagsStringConverter().fromString(tagsText.getText());
    onLogActivityCommand.accept(new LogActivityCommand(null, null, activityText.getText(), tags));
  }
}
