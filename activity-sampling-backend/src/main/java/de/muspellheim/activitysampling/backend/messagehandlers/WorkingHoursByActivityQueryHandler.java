/*
 * Activity Sampling - Backend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.backend.messagehandlers;

import de.muspellheim.activitysampling.backend.EventStore;
import de.muspellheim.activitysampling.contract.messages.queries.WorkingHoursByActivityQuery;
import de.muspellheim.activitysampling.contract.messages.queries.WorkingHoursByActivityQueryResult;
import java.util.List;
import java.util.TreeSet;

public class WorkingHoursByActivityQueryHandler extends BaseWorkingHoursQueryHandler {
  public WorkingHoursByActivityQueryHandler(EventStore eventStore) {
    super(eventStore);
  }

  public WorkingHoursByActivityQueryResult handle(WorkingHoursByActivityQuery query) {
    var tags = workingHours.values().stream().flatMap(it -> it.tags().stream()).toList();
    var filtered =
        WorkingHoursProjection.filterTags(workingHours.values().stream(), query.includedTags())
            .toList();
    return new WorkingHoursByActivityQueryResult(List.copyOf(filtered), new TreeSet<>(tags));
  }
}
