/*
 * Activity Sampling - Frontend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.frontend;

import java.util.List;
import java.util.Objects;

record ActivityTemplate(String activity, List<String> tags) {
  ActivityTemplate {
    Objects.requireNonNull(activity, "activity");
    Objects.requireNonNull(tags, "tags");
  }

  ActivityTemplate(String activity) {
    this(activity, List.of());
  }
}
