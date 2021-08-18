/*
 * Activity Sampling - Contract
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.contract.messages.queries;

import de.muspellheim.activitysampling.contract.data.Activity;
import de.muspellheim.activitysampling.contract.data.ActivityTemplate;
import java.util.List;
import java.util.Objects;

public record ActivityLogQueryResult(
    List<Activity> log, // TODO Ersetze durch String
    List<ActivityTemplate> recent,
    ActivityTemplate last,
    List<String> recentClients,
    List<String> recentProjects,
    List<String> recentTasks,
    @Deprecated List<String> recentTags) {
  public ActivityLogQueryResult {
    Objects.requireNonNull(log, "log");
    Objects.requireNonNull(recent, "recent");
    Objects.requireNonNull(recentTags, "recentTags");
  }

  public ActivityLogQueryResult(
      List<Activity> log,
      List<ActivityTemplate> recent,
      ActivityTemplate last,
      List<String> recentClients,
      List<String> recentProjects,
      List<String> recentTasks) {
    this(log, recent, last, recentClients, recentProjects, recentTasks, List.of());
  }

  @Deprecated
  public ActivityLogQueryResult(
      List<Activity> log,
      List<ActivityTemplate> recent,
      ActivityTemplate last,
      @Deprecated List<String> recentTags) {
    this(log, recent, last, null, null, null, recentTags);
  }
}
