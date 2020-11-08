/*
 * Activity Sampling - Application
 * Copyright (c) 2020 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling;

import de.muspellheim.activitysampling.backend.MessageHandler;
import de.muspellheim.activitysampling.backend.PeriodCheck;
import de.muspellheim.activitysampling.backend.adapters.TodoJsonRepository;
import de.muspellheim.activitysampling.contract.messages.queries.TodoListQuery;
import de.muspellheim.activitysampling.frontend.ActivitySamplingView;
import de.muspellheim.activitysampling.frontend.AppTrayIcon;
import de.muspellheim.activitysampling.frontend.TodoAppViewController;
import java.nio.file.Paths;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
  private Stage stage;
  private MessageHandler messageHandler;
  private TodoAppViewController todoAppViewController;
  private AppTrayIcon trayIconController;

  public static void main(String[] args) {
    Application.launch(args);
  }

  @Override
  public void start(Stage primaryStage) {
    var root = new ActivitySamplingView();
    root.setOnLogActivityCommand(c -> System.out.println(c));

    trayIconController = new AppTrayIcon();

    var periodTimer = new PeriodCheck();
    periodTimer.setOnPeriodStartedNotification(n -> root.display(n));
    periodTimer.setOnPeriodProgressedNotification(n -> root.display(n));
    periodTimer.setOnPeriodEndedNotification(
        n -> {
          root.display(n);
          trayIconController.display(n);
        });
    periodTimer.run();

    var scene = new Scene(root);
    primaryStage.setScene(scene);
    primaryStage.show();
    // stage = primaryStage;
    // build();
    // bind();
    // run();
  }

  @Override
  public void stop() {
    trayIconController.dispose();
  }

  private void build() {
    var file = Paths.get("todos.json");
    var repository = new TodoJsonRepository(file);
    messageHandler = new MessageHandler(repository);

    var root = TodoAppViewController.load();
    var view = root.getKey();
    todoAppViewController = root.getValue();
    Scene scene = new Scene(view);
    stage.setScene(scene);
    stage.setTitle("Activity Sampling");
  }

  private void bind() {
    todoAppViewController.setOnNewTodoCommand(
        it -> {
          messageHandler.handle(it);
          runQuery();
        });
    todoAppViewController.setOnToggleAllCommand(
        it -> {
          messageHandler.handle(it);
          runQuery();
        });
    todoAppViewController.setOnToggleCommand(
        it -> {
          messageHandler.handle(it);
          runQuery();
        });
    todoAppViewController.setOnDestroyCommand(
        it -> {
          messageHandler.handle(it);
          runQuery();
        });
    todoAppViewController.setOnEditCommand(
        it -> {
          messageHandler.handle(it);
          runQuery();
        });
    todoAppViewController.setOnClearCompletedCommand(
        it -> {
          messageHandler.handle(it);
          runQuery();
        });
    todoAppViewController.setOnTodoListQuery(it -> runQuery());
  }

  private void run() {
    stage.show();
    runQuery();
  }

  private void runQuery() {
    var result = messageHandler.handle(new TodoListQuery());
    todoAppViewController.display(result);
  }
}
