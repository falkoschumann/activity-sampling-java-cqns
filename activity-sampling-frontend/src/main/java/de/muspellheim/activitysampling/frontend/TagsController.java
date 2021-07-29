/*
 * Activity Sampling - Frontend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.frontend;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.function.Consumer;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.StringConverter;

public class TagsController implements Initializable {
  @FXML private Stage stage;
  @FXML private CheckBox allTagsCheckBox;
  @FXML private ListView<String> tagList;

  private TagsModel model;

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
    model = new TagsModel();
    tagList.setCellFactory(
        CheckBoxListCell.forListView(model::getSelectedFor, new TagStringConverter(resources)));

    allTagsCheckBox.selectedProperty().bindBidirectional(model.allSelectedProperty());
    Stages.hookCloseHandler(stage);
  }

  public void setOnSelectedTagsChanged(Consumer<Set<String>> onSelectedTagsChanged) {
    model.setOnSelectedTagsChanged(onSelectedTagsChanged);
  }

  void setTags(Set<String> tags) {
    model.setTags(tags);
    tagList.getItems().setAll(model.getTags());
  }

  void run() {
    stage.show();
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
