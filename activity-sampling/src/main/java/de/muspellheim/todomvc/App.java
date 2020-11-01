/*
 * TodoMVC - Application
 * Copyright (c) 2020 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.todomvc;

import de.muspellheim.todomvc.backend.MessageHandler;
import de.muspellheim.todomvc.backend.adapters.TodoJsonRepository;
import de.muspellheim.todomvc.contract.messages.queries.TodoListQuery;
import de.muspellheim.todomvc.frontend.TodoAppViewController;
import java.nio.file.Paths;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
  private Stage stage;
  private MessageHandler messageHandler;
  private TodoAppViewController controller;

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
    var file = Paths.get("todos.json");
    var repository = new TodoJsonRepository(file);
    messageHandler = new MessageHandler(repository);

    var root = TodoAppViewController.load();
    var view = root.getKey();
    controller = root.getValue();
    Scene scene = new Scene(view);
    stage.setScene(scene);
    stage.setTitle("TodoMVC");
  }

  private void bind() {
    controller.setOnNewTodoCommand(
        it -> {
          messageHandler.handle(it);
          runQuery();
        });
    controller.setOnToggleAllCommand(
        it -> {
          messageHandler.handle(it);
          runQuery();
        });
    controller.setOnToggleCommand(
        it -> {
          messageHandler.handle(it);
          runQuery();
        });
    controller.setOnDestroyCommand(
        it -> {
          messageHandler.handle(it);
          runQuery();
        });
    controller.setOnEditCommand(
        it -> {
          messageHandler.handle(it);
          runQuery();
        });
    controller.setOnClearCompletedCommand(
        it -> {
          messageHandler.handle(it);
          runQuery();
        });
    controller.setOnTodoListQuery(it -> runQuery());
  }

  private void run() {
    stage.show();
    runQuery();
  }

  private void runQuery() {
    var result = messageHandler.handle(new TodoListQuery());
    controller.display(result);
  }
}
