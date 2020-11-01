/*
 * TodoMVC - Backend
 * Copyright (c) 2020 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.todomvc.backend.adapters;

import static org.junit.jupiter.api.Assertions.assertEquals;

import de.muspellheim.todomvc.contract.data.Todo;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.junit.jupiter.api.Test;

public class TodoJsonRepositoryTests {
  private static final Path READ_TEST_FILE = Paths.get("src/test/resources/todos.json");
  private static final Path WRITTEN_TEST_FILE = Paths.get("build/test/todos.json");

  @Test
  void load() throws IOException {
    var repository = new TodoJsonRepository(READ_TEST_FILE);

    var todos = repository.load();

    assertEquals(createTestData(), todos);
  }

  @Test
  void store() throws IOException {
    Files.createDirectories(WRITTEN_TEST_FILE.getParent());
    var repository = new TodoJsonRepository(WRITTEN_TEST_FILE);
    var todos = createTestData();

    repository.store(todos);

    var actualTodos = repository.load();
    assertEquals(createTestData(), actualTodos);
  }

  private static List<Todo> createTestData() {
    return List.of(
        new Todo("119e6785-8ffc-42e0-8df6-dbc64881f2b7", "Taste JavaScript", true),
        new Todo("d2f7760d-8f03-4cb3-9176-06311cb89993", "Buy a unicorn", false));
  }
}
