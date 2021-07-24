/*
 * Activity Sampling - Contract
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.contract.data;

import java.util.List;
import java.util.Objects;

public record ActivityTemplate(String activity, List<String> tags) {
  public static final ActivityTemplate NULL = new ActivityTemplate("", List.of());

  public ActivityTemplate {
    Objects.requireNonNull(activity, "activity");
    Objects.requireNonNull(tags, "tags");
  }

  public ActivityTemplate(String activity) {
    this(activity, List.of());
  }
}
