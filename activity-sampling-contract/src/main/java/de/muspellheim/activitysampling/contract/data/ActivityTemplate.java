/*
 * Activity Sampling - Contract
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.contract.data;

import java.util.Objects;

public record ActivityTemplate(String client, String project, String task, String notes) {
  public static final ActivityTemplate NULL = new ActivityTemplate("", "", "", "");

  public ActivityTemplate {
    Objects.requireNonNull(client, "client");
    Objects.requireNonNull(project, "project");
    Objects.requireNonNull(task, "task");
    Objects.requireNonNull(notes, "notes");
  }
}
