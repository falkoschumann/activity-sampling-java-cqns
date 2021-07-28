/*
 * Activity Sampling - Frontend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.frontend;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.SortedSet;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javafx.beans.binding.BooleanExpression;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.StringConverter;
import lombok.Getter;
import lombok.Setter;

public class TagsController implements Initializable {
  @Getter @Setter Consumer<Set<String>> onSelectedTagsChanged;

  @FXML private Stage stage;
  @FXML private CheckBox allTagsCheckBox;
  @FXML private ListView<String> tagList;

  // TODO Extrahiere Model
  private final Map<String, BooleanProperty> checkedTags = new LinkedHashMap<>();

  static TagsController create(Stage owner) {
    try {
      var location = PreferencesController.class.getResource("TagsView.fxml");
      var resources = ResourceBundle.getBundle("ActivitySampling");
      var loader = new FXMLLoader(location, resources);
      loader.load();

      var controller = (TagsController) loader.getController();
      controller.stage.initOwner(owner);
      controller.stage.initStyle(StageStyle.UTILITY);
      return controller;
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    tagList.setCellFactory(
        CheckBoxListCell.forListView(checkedTags::get, new TagStringConverter(resources)));

    Stages.hookCloseHandler(stage);
  }

  void setTags(SortedSet<String> tags) {
    tagList.getItems().setAll(tags);
    updateCheckedTags(tags);
  }

  private void updateCheckedTags(SortedSet<String> newTags) {
    var lastTags = Set.copyOf(checkedTags.keySet());
    lastTags.forEach(
        it -> {
          if (!newTags.contains(it)) {
            checkedTags.remove(it);
          }
        });

    newTags.forEach(
        it -> {
          if (!checkedTags.containsKey(it)) {
            checkedTags.put(
                it,
                new SimpleBooleanProperty(true) {
                  @Override
                  protected void invalidated() {
                    var allTagsChecked =
                        checkedTags.values().stream()
                            .map(BooleanExpression::getValue)
                            .reduce(Boolean::logicalAnd)
                            .orElse(true);
                    allTagsCheckBox.setSelected(allTagsChecked);
                    sendWorkingHoursTodayQuery();
                  }
                });
          }
        });
  }

  void run() {
    stage.show();
  }

  @FXML
  private void handleCheckAllTags() {
    var allTagsChecked = allTagsCheckBox.isSelected();
    checkedTags.values().forEach(it -> it.set(allTagsChecked));
    sendWorkingHoursTodayQuery();
  }

  private void sendWorkingHoursTodayQuery() {
    var selectedTags =
        checkedTags.entrySet().stream()
            .filter(it -> it.getValue().get())
            .map(Entry::getKey)
            .collect(Collectors.toSet());
    onSelectedTagsChanged.accept(selectedTags);
  }

  private static class TagStringConverter extends StringConverter<String> {
    private final ResourceBundle resources;

    TagStringConverter(ResourceBundle resources) {
      this.resources = resources;
    }

    @Override
    public String toString(String object) {
      return "".equals(object) ? resources.getString("tags.noTag") : object;
    }

    @Override
    public String fromString(String string) {
      return resources.getString("tags.noTag").equals(string) ? "" : string;
    }
  }
}
