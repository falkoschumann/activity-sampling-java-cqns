/*
 * TodoMVC - Backend
 * Copyright (c) 2020 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.todomvc.backend;

import de.muspellheim.todomvc.contract.data.Todo;
import java.io.IOException;
import java.util.List;

public interface TodoRepository {
  List<Todo> load() throws IOException;

  void store(List<Todo> todos) throws IOException;
}
