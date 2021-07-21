/*
 * Activity Sampling - Frontend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.frontend;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.SortedSet;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.stage.Stage;
import javafx.util.StringConverter;

public class TagsController implements Initializable {
  @FXML private ListView<String> tagList;
  @FXML private CheckBox allTagsCheckBox;
  private Dialog<Collection<String>> dialog;
  private Set<String> selectedTags;

  static TagsController create(Stage owner) {
    try {
      var location = PreferencesController.class.getResource("TagsView.fxml");
      var resources = ResourceBundle.getBundle("ActivitySampling");
      var loader = new FXMLLoader(location, resources);
      var view = (DialogPane) loader.load();

      var controller = (TagsController) loader.getController();
      var dialog = new Dialog<Collection<String>>();
      dialog.initOwner(owner);
      dialog.setTitle(resources.getString("tags.title"));
      dialog.setDialogPane(view);
      controller.dialog = dialog;
      return controller;
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    tagList.setCellFactory(
        CheckBoxListCell.forListView(
            it -> {
              var prop = new SimpleBooleanProperty(getSelectedTags().contains(it));
              prop.addListener(
                  (observable, oldValue, newValue) -> {
                    if (!oldValue && newValue) {
                      getSelectedTags().add(it);
                    } else if (oldValue && !newValue) {
                      getSelectedTags().remove(it);
                    }
                  });
              return prop;
            },
            new TagStringConverter(resources)));

    // TODO allTagsCheckBox nur ausgewählt, wenn alle Tags ausgewählt, sonst nicht
    allTagsCheckBox
        .selectedProperty()
        .addListener(
            observable -> {
              if (allTagsCheckBox.isSelected()) {
                System.out.println("Alle ausgewählt");
                selectedTags = new LinkedHashSet<>(tagList.getItems());
              } else {
                selectedTags.clear();
              }
              tagList.refresh();
            });
  }

  final void initTags(SortedSet<String> tags, Set<String> selectedTags) {
    this.selectedTags = new LinkedHashSet<>(selectedTags);
    // TODO Füge Kein Tag nur hinzu, wenn es vorkommt, Zuständigkeit ins Backend verschieben
    this.selectedTags.add("");

    tagList.getItems().setAll(tags);
    tagList.getItems().add(0, "");
  }

  final Set<String> getSelectedTags() {
    return selectedTags;
  }

  void run() {
    // Ersetze mit show()
    dialog.showAndWait();
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
