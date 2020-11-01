/*
 * TodoMVC - Frontend
 * Copyright (c) 2020 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.todomvc.frontend;

import de.muspellheim.todomvc.contract.messages.commands.DestroyCommand;
import de.muspellheim.todomvc.contract.messages.commands.EditCommand;
import de.muspellheim.todomvc.contract.messages.commands.ToggleCommand;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class TodoListCell<T extends TodoModel> extends ListCell<T> {
  private HBox container;
  private CheckBox completed;
  private Label title;
  private Button destroy;
  private TextField edit;

  public TodoListCell() {
    build();
    bind();
  }

  private void build() {
    title = new Label();
    title.setMaxWidth(Double.MAX_VALUE);
    title.setMaxHeight(Double.MAX_VALUE);
    HBox.setHgrow(title, Priority.ALWAYS);

    edit = new TextField();
    HBox.setHgrow(edit, Priority.ALWAYS);

    destroy = new Button("X");
    destroy.setVisible(false);

    completed = new CheckBox();

    container = new HBox();
    container.getChildren().setAll(completed, title, destroy);
  }

  private void bind() {
    container
        .hoverProperty()
        .addListener(
            (observableValue, oldValue, newValue) ->
                destroy.setVisible(newValue && container.getChildren().contains(title)));
  }

  @Override
  protected void updateItem(T item, boolean empty) {
    super.updateItem(item, empty);

    if (empty || item == null) {
      setText(null);
      setGraphic(null);
    } else {
      setGraphic(container);

      completed.setSelected(item.getTodo().isCompleted());
      completed.setOnAction(
          it -> item.getOnToggleCommand().accept((new ToggleCommand(item.getTodo().getId()))));

      title.setText(item.getTodo().getTitle());
      title.setOnMouseClicked(
          it -> {
            if (it.getButton() == MouseButton.PRIMARY && it.getClickCount() == 2) {
              completed.setVisible(false);
              container.getChildren().set(1, edit);
              edit.requestFocus();
              destroy.setVisible(false);
            }
          });

      edit.setText(item.getTodo().getTitle());
      edit.setOnAction(it -> edit());
      edit.focusedProperty()
          .addListener(
              (observableValue, oldValue, newValue) -> {
                if (oldValue && !newValue) {
                  edit();
                }
              });

      destroy.setOnAction(
          it -> item.getOnDestroyCommand().accept((new DestroyCommand(item.getTodo().getId()))));
    }
  }

  private void edit() {
    getItem()
        .getOnEditCommand()
        .accept(new EditCommand(getItem().getTodo().getId(), edit.getText().trim()));
    completed.setVisible(true);
    container.getChildren().set(1, title);
    destroy.setVisible(container.isHover());
  }
}
