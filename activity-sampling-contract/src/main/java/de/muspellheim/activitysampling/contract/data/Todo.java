/*
 * Activity Sampling - Contract
 * Copyright (c) 2020 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.contract.data;

import java.util.UUID;
import lombok.NonNull;
import lombok.Value;
import lombok.With;

@Value
public class Todo {
  @NonNull String id;
  @With @NonNull String title;
  @With boolean completed;

  public static Todo of(@NonNull String title) {
    return new Todo(UUID.randomUUID().toString(), title, false);
  }
}
