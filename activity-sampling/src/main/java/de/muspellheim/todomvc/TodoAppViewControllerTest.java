/*
 * TodoMVC - Application
 * Copyright (c) 2020 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.todomvc;

import de.muspellheim.todomvc.contract.data.Todo;
import de.muspellheim.todomvc.contract.messages.queries.TodoListQueryResult;
import de.muspellheim.todomvc.frontend.TodoAppViewController;
import java.util.List;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class TodoAppViewControllerTest extends Application {
  private TodoAppViewController controller;
  private Stage stage;

  public static void main(String[] args) {
    Application.launch(args);
  }

  @Override
  public void start(Stage primaryStage) {
    stage = primaryStage;
    build();
    bind();
    run();
  }

  private void build() {
    var root = TodoAppViewController.load();
    var view = root.getKey();
    controller = root.getValue();
    Scene scene = new Scene(view);
    stage.setScene(scene);
  }

  private void bind() {
    controller.setOnNewTodoCommand(System.out::println);
    controller.setOnToggleAllCommand(System.out::println);
    controller.setOnToggleCommand(System.out::println);
    controller.setOnEditCommand(System.out::println);
    controller.setOnDestroyCommand(System.out::println);
    controller.setOnClearCompletedCommand(System.out::println);
    controller.setOnTodoListQuery(System.out::println);
  }

  private void run() {
    controller.display(
        new TodoListQueryResult(
            List.of(
                new Todo("119e6785-8ffc-42e0-8df6-dbc64881f2b7", "Taste JavaScript", true),
                new Todo("d2f7760d-8f03-4cb3-9176-06311cb89993", "Buy a unicorn", false))));
    stage.show();
  }
}
