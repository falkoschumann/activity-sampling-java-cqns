/*
 * Activity Sampling - Frontend
 * Copyright (c) 2020 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.frontend;

import de.muspellheim.activitysampling.contract.data.Todo;
import de.muspellheim.activitysampling.contract.messages.commands.ClearCompletedCommand;
import de.muspellheim.activitysampling.contract.messages.commands.DestroyCommand;
import de.muspellheim.activitysampling.contract.messages.commands.EditCommand;
import de.muspellheim.activitysampling.contract.messages.commands.NewTodoCommand;
import de.muspellheim.activitysampling.contract.messages.commands.ToggleAllCommand;
import de.muspellheim.activitysampling.contract.messages.commands.ToggleCommand;
import de.muspellheim.activitysampling.contract.messages.queries.TodoListQuery;
import de.muspellheim.activitysampling.contract.messages.queries.TodoListQueryResult;
import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Pair;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

public class TodoAppViewController {
  @Getter @Setter private Consumer<NewTodoCommand> onNewTodoCommand;
  @Getter @Setter private Consumer<ToggleAllCommand> onToggleAllCommand;
  @Getter @Setter private Consumer<ToggleCommand> onToggleCommand;
  @Getter @Setter private Consumer<EditCommand> onEditCommand;
  @Getter @Setter private Consumer<DestroyCommand> onDestroyCommand;
  @Getter @Setter private Consumer<ClearCompletedCommand> onClearCompletedCommand;
  @Getter @Setter private Consumer<TodoListQuery> onTodoListQuery;

  @FXML private CheckBox toggleAll;
  @FXML private TextField newTodo;

  @FXML private ListView<TodoModel> todoList;

  @FXML private Pane footer;
  @FXML private TextFlow todoCount;
  @FXML private ToggleGroup filters;
  @FXML private ToggleButton all;
  @FXML private ToggleButton active;
  @FXML private ToggleButton completed;
  @FXML private Button clearCompleted;

  private List<Todo> todos = List.of();

  public static Pair<Parent, TodoAppViewController> load() {
    FXMLLoader fxmlLoader =
        new FXMLLoader(TodoAppViewController.class.getResource("TodoAppView.fxml"));
    try {
      fxmlLoader.load();
      return new Pair<>(fxmlLoader.getRoot(), fxmlLoader.getController());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void newTodo() {
    var text = newTodo.getText().trim();
    if (text.isEmpty()) {
      return;
    }
    onNewTodoCommand.accept(new NewTodoCommand(text));
    newTodo.setText("");
  }

  public void toggleAll() {
    var checked = toggleAll.isSelected();
    onToggleAllCommand.accept(new ToggleAllCommand(checked));
  }

  public void filterAll() {
    updateTodoList();
  }

  public void filterActive() {
    updateTodoList();
  }

  public void filterCompleted() {
    updateTodoList();
  }

  public void clearCompleted() {
    onClearCompletedCommand.accept(new ClearCompletedCommand());
  }

  public void display(@NonNull TodoListQueryResult result) {
    todos = result.getTodos();
    updateTodoList();

    var completedCount = result.getTodos().stream().filter(Todo::isCompleted).count();
    var activeTodoCount = result.getTodos().size() - completedCount;

    var hasTodos = !result.getTodos().isEmpty();
    toggleAll.setVisible(hasTodos);
    todoList.setVisible(hasTodos);
    todoList.setManaged(hasTodos);
    footer.setVisible(hasTodos);
    footer.setManaged(hasTodos);

    toggleAll.setSelected(result.getTodos().size() == completedCount);

    var text = new Text(String.valueOf(activeTodoCount));
    text.setStyle("-fx-font-weight: bold");
    todoCount
        .getChildren()
        .setAll(text, new Text(" item" + (completedCount == 1 ? "" : "s") + " left"));

    clearCompleted.setVisible(completedCount > 0);
  }

  private void updateTodoList() {
    var todoModels =
        todos.stream()
            .filter(
                it ->
                    filters.getSelectedToggle() == active && !it.isCompleted()
                        || filters.getSelectedToggle() == completed && it.isCompleted()
                        || filters.getSelectedToggle() == all)
            .map(it -> new TodoModel(it, onToggleCommand, onEditCommand, onDestroyCommand))
            .collect(Collectors.toList());
    todoList.getItems().setAll(todoModels);
  }
}
