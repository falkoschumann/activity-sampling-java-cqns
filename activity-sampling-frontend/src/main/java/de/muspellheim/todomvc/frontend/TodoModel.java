/*
 * TodoMVC - Frontend
 * Copyright (c) 2020 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.todomvc.frontend;

import de.muspellheim.todomvc.contract.data.Todo;
import de.muspellheim.todomvc.contract.messages.commands.DestroyCommand;
import de.muspellheim.todomvc.contract.messages.commands.EditCommand;
import de.muspellheim.todomvc.contract.messages.commands.ToggleCommand;
import java.util.function.Consumer;
import lombok.NonNull;
import lombok.Value;

@Value
public class TodoModel {
  @NonNull Todo todo;
  @NonNull Consumer<ToggleCommand> onToggleCommand;
  @NonNull Consumer<EditCommand> onEditCommand;
  @NonNull Consumer<DestroyCommand> onDestroyCommand;
}
