/*
 * TodoMVC - Contract
 * Copyright (c) 2020 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.todomvc.contract.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class TodoTests {
  @Test
  void create() {
    Todo todo = new Todo("foobar", "Taste JavaScript", true);

    assertEquals("foobar", todo.getId(), "Id is set");
    assertEquals("Taste JavaScript", todo.getTitle(), "Title is set");
    assertTrue(todo.isCompleted(), "Todo is completed");
  }

  @Test
  void createConvenience() {
    Todo todo = Todo.of("Taste JavaScript");

    assertNotNull(todo.getId(), "Id is set");
    assertEquals("Taste JavaScript", todo.getTitle(), "Title is set");
    assertFalse(todo.isCompleted(), "Todo is not completed");
  }
}
