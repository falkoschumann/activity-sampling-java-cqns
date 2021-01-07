/*
 * Activity Sampling - Frontend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.frontend;

import de.muspellheim.activitysampling.contract.data.Activity;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import lombok.Getter;
import lombok.Setter;

class ActivityForm extends VBox {
  @Getter @Setter private Consumer<Activity> onActivitySelected;

  private final ObservableList<Activity> recentActivities = FXCollections.observableArrayList();

  private final VFormInput<TextField> activityInput;
  private final VFormInput<TextField> optionalTagsInput;
  private final SplitMenuButton logButton;

  ActivityForm() {
    var activityField = new TextField();
    activityField.setPromptText("What are you working on?");
    activityField.setOnAction(e -> handleActivitySelected());
    activityInput = new VFormInput<>("Activity*", activityField);

    var optionalTagsField = new TextField();
    optionalTagsField.setPromptText("Customer, Project, Product");
    optionalTagsField.setOnAction(e -> handleActivitySelected());
    optionalTagsInput = new VFormInput<>("Optional tags", optionalTagsField);

    logButton = new SplitMenuButton();
    logButton.setText("Log");
    logButton.setAlignment(Pos.CENTER);
    logButton.setMaxWidth(Double.MAX_VALUE);
    logButton.setDisable(true);
    logButton.disableProperty().bind(activityField.textProperty().isEmpty());
    logButton.setOnAction(e -> handleActivitySelected());

    setSpacing(Views.UNRELATED_GAP);
    getChildren().setAll(activityInput, optionalTagsInput, logButton);

    recentActivities.addListener((InvalidationListener) l -> update());
  }

  ObservableList<Activity> getRecentActivities() {
    return recentActivities;
  }

  private void handleActivitySelected() {
    var activity =
        new Activity(
            "",
            LocalDateTime.now(),
            Duration.ZERO,
            activityInput.getControl().getText(),
            List.of(optionalTagsInput.getControl().getText().split(",")));
    handleActivitySelected(activity);
  }

  private void handleActivitySelected(Activity activity) {
    if (onActivitySelected == null) {
      return;
    }

    onActivitySelected.accept(activity);
  }

  private void update() {
    var activityStringConverter = new ActivityStringConverter();
    var menuItems =
        recentActivities.stream()
            .map(
                it -> {
                  var menuItem = new MenuItem(activityStringConverter.toString(it));
                  menuItem.setOnAction(e -> handleActivitySelected(it));
                  return menuItem;
                })
            .collect(Collectors.toList());
    Platform.runLater(
        () -> {
          logButton.getItems().setAll(menuItems);

          if (!recentActivities.isEmpty()) {
            var lastActivity = recentActivities.get(0);
            activityInput.getControl().setText(lastActivity.getActivity());
            optionalTagsInput.getControl().setText(String.join(", ", lastActivity.getTags()));
          }
        });
  }
}
