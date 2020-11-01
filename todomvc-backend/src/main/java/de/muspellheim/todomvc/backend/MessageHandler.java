/*
 * TodoMVC - Backend
 * Copyright (c) 2020 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.todomvc.backend;

import de.muspellheim.todomvc.contract.data.Todo;
import de.muspellheim.todomvc.contract.messages.commands.ClearCompletedCommand;
import de.muspellheim.todomvc.contract.messages.commands.CommandStatus;
import de.muspellheim.todomvc.contract.messages.commands.DestroyCommand;
import de.muspellheim.todomvc.contract.messages.commands.EditCommand;
import de.muspellheim.todomvc.contract.messages.commands.Failure;
import de.muspellheim.todomvc.contract.messages.commands.NewTodoCommand;
import de.muspellheim.todomvc.contract.messages.commands.Success;
import de.muspellheim.todomvc.contract.messages.commands.ToggleAllCommand;
import de.muspellheim.todomvc.contract.messages.commands.ToggleCommand;
import de.muspellheim.todomvc.contract.messages.queries.TodoListQuery;
import de.muspellheim.todomvc.contract.messages.queries.TodoListQueryResult;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.NonNull;

public class MessageHandler {
  private final TodoRepository repository;

  public MessageHandler(TodoRepository repository) {
    this.repository = repository;
  }

  public CommandStatus handle(@NonNull NewTodoCommand command) {
    try {
      var todos = new ArrayList<>(repository.load());
      todos.add(Todo.of(command.getTitle()));
      repository.store(todos);
      return new Success();
    } catch (IOException e) {
      return new Failure(e.getLocalizedMessage());
    }
  }

  public CommandStatus handle(@NonNull ToggleAllCommand command) {
    try {
      var todos =
          repository.load().stream()
              .map(it -> it.withCompleted(command.isCompleted()))
              .collect(Collectors.toList());
      repository.store(todos);
      return new Success();
    } catch (IOException e) {
      return new Failure(e.getLocalizedMessage());
    }
  }

  public CommandStatus handle(@NonNull ToggleCommand command) {
    try {
      var todos =
          repository.load().stream()
              .map(
                  it ->
                      (it.getId().equals(command.getId()))
                          ? it.withCompleted(!it.isCompleted())
                          : it)
              .collect(Collectors.toList());
      repository.store(todos);
      return new Success();
    } catch (IOException e) {
      return new Failure(e.getLocalizedMessage());
    }
  }

  public CommandStatus handle(@NonNull DestroyCommand command) {
    try {
      var todos =
          repository.load().stream()
              .filter(it -> it.getId() != command.getId())
              .collect(Collectors.toList());
      repository.store(todos);
      return new Success();
    } catch (IOException e) {
      return new Failure(e.getLocalizedMessage());
    }
  }

  public CommandStatus handle(@NonNull EditCommand command) {
    try {
      var todos =
          repository.load().stream()
              .map(it -> it.getId().equals(command.getId()) ? it.withTitle(command.getTitle()) : it)
              .collect(Collectors.toList());
      repository.store(todos);
      return new Success();
    } catch (IOException e) {
      return new Failure(e.getLocalizedMessage());
    }
  }

  public CommandStatus handle(@NonNull ClearCompletedCommand command) {
    try {
      var todos =
          repository.load().stream().filter(it -> !it.isCompleted()).collect(Collectors.toList());
      repository.store(todos);
      return new Success();
    } catch (IOException e) {
      return new Failure(e.getLocalizedMessage());
    }
  }

  public TodoListQueryResult handle(@NonNull TodoListQuery query) {
    try {
      var todos = repository.load();
      return new TodoListQueryResult(todos);
    } catch (IOException e) {
      System.err.println(e);
      return new TodoListQueryResult(List.of());
    }
  }
}
