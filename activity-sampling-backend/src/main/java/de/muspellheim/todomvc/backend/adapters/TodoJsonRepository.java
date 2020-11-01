/*
 * TodoMVC - Backend
 * Copyright (c) 2020 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.todomvc.backend.adapters;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import de.muspellheim.todomvc.backend.TodoRepository;
import de.muspellheim.todomvc.contract.data.Todo;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import lombok.NonNull;

public class TodoJsonRepository implements TodoRepository {
  private static final Type LIST_TYPE = new TypeToken<List<Todo>>() {}.getType();
  private final Path file;

  public TodoJsonRepository(Path file) {
    this.file = file;
  }

  @Override
  public List<Todo> load() throws IOException {
    if (!Files.exists(file)) {
      return List.of();
    }

    var gson = new Gson();
    try (BufferedReader reader = Files.newBufferedReader(file)) {
      return gson.fromJson(reader, LIST_TYPE);
    }
  }

  @Override
  public void store(@NonNull List<Todo> todos) throws IOException {
    var gson = new GsonBuilder().setPrettyPrinting().create();
    try (BufferedWriter writer = Files.newBufferedWriter(file)) {
      gson.toJson(todos, writer);
    }
  }
}
