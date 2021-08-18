/*
 * Activity Sampling - Contract
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.contract.data;

import java.util.List;
import java.util.Objects;

public record ActivityTemplate(
    String client, String project, String task, String notes, @Deprecated List<String> tags) {
  public static final ActivityTemplate NULL = new ActivityTemplate(null, null, null, "", List.of());

  public ActivityTemplate {
    Objects.requireNonNull(notes, "notes");
    Objects.requireNonNull(tags, "tags");
  }

  public ActivityTemplate(String client, String project, String task, String notes) {
    this(client, project, task, notes, List.of());
  }

  public ActivityTemplate(String activity) {
    this(null, null, null, activity, List.of());
  }

  @Deprecated
  public ActivityTemplate(@Deprecated String activity, @Deprecated List<String> tags) {
    this(null, null, null, activity, tags);
  }

  @Deprecated
  public String activity() {
    return notes;
  }
}
