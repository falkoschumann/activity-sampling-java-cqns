/*
 * Activity Sampling - Backend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.backend.messagehandlers;

import de.muspellheim.activitysampling.contract.data.WorkingHours;
import de.muspellheim.activitysampling.contract.messages.queries.Queries;
import java.util.Collection;
import java.util.stream.Stream;

public class WorkingHoursProjection {
  static Stream<WorkingHours> filterTags(
      Stream<WorkingHours> workingHours, Collection<String> tags) {
    if (tags.isEmpty()) {
      return workingHours;
    }

    return workingHours.filter(
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
}
