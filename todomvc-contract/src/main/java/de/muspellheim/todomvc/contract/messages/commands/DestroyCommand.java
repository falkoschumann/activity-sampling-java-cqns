/*
 * TodoMVC - Contract
 * Copyright (c) 2020 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.todomvc.contract.messages.commands;

import lombok.NonNull;
import lombok.Value;

@Value
public class DestroyCommand {
  @NonNull String id;
}
