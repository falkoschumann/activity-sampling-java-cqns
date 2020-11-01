/*
 * TodoMVC - Contract
 * Copyright (c) 2020 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.todomvc.contract.messages.queries;

import de.muspellheim.todomvc.contract.data.Todo;
import java.util.List;
import lombok.NonNull;
import lombok.Value;

@Value
public class TodoListQueryResult {
  @NonNull List<Todo> todos;
}
