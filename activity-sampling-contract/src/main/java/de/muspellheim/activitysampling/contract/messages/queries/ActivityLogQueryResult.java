/*
 * Activity Sampling - Contract
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.contract.messages.queries;

import de.muspellheim.activitysampling.contract.data.ActivityTemplate;
import java.util.List;
import java.util.Objects;

public record ActivityLogQueryResult(
    String log,
    List<ActivityTemplate> recent,
    ActivityTemplate last,
    List<String> recentClients,
    List<String> recentProjects,
    List<String> recentTasks) {
  public ActivityLogQueryResult {
    Objects.requireNonNull(log, "log");
    Objects.requireNonNull(recent, "recent");
    Objects.requireNonNull(recentClients, "recentClients");
    Objects.requireNonNull(recentProjects, "recentProjects");
    Objects.requireNonNull(recentTasks, "recentTasks");
  }
}
