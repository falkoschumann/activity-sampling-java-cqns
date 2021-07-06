/*
 * Activity Sampling - Backend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.backend.messagehandlers;

import de.muspellheim.activitysampling.contract.data.Activity;
import de.muspellheim.activitysampling.contract.messages.queries.Queries;
import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

class ActivityProjection {
  private ActivityProjection() {}

  static List<String> distinctTags(Stream<Activity> activities) {
    return activities.flatMap(it -> it.tags().stream()).toList();
  }

  static Stream<Activity> filterTags(Stream<Activity> activities, Collection<String> tags) {
    if (tags.isEmpty()) {
      return activities;
    }

    return activities.filter(
        it -> {
          if (it.tags().isEmpty() && tags.contains(Queries.NO_TAG)) {
            return true;
          }

          for (var tag : it.tags()) {
            if (tags.contains(tag)) {
              return true;
            }
          }

          return false;
        });
  }

  static Duration totalWorkingHours(Stream<Activity> activities) {
    return activities.map(Activity::period).reduce(Duration.ZERO, Duration::plus);
  }
}
