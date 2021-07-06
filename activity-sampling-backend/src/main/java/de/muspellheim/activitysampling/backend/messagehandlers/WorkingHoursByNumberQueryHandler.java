/*
 * Activity Sampling - Backend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.backend.messagehandlers;

import de.muspellheim.activitysampling.backend.EventStore;
import de.muspellheim.activitysampling.contract.messages.queries.WorkingHoursByNumberQuery;
import de.muspellheim.activitysampling.contract.messages.queries.WorkingHoursByNumberQueryResult;
import de.muspellheim.activitysampling.contract.messages.queries.WorkingHoursByNumberQueryResult.WorkingHoursCategory;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

public class WorkingHoursByNumberQueryHandler extends BaseWorkingHoursQueryHandler {
  public WorkingHoursByNumberQueryHandler(EventStore eventStore) {
    super(eventStore);
  }

  public WorkingHoursByNumberQueryResult handle(WorkingHoursByNumberQuery query) {
    var tags = workingHours.stream().flatMap(it -> it.tags().stream()).toList();
    var numbers = new TreeMap<Duration, WorkingHoursCategory>();
    WorkingHoursProjection.filterTags(workingHours.stream(), query.includedTags())
        .forEach(
            it -> {
              var hours = it.workingHours().truncatedTo(ChronoUnit.HOURS);
              if (it.workingHours().toMinutesPart() > 0) {
                hours = hours.plusHours(1);
              }
              if (numbers.containsKey(hours)) {
                var category = numbers.get(hours);
                numbers.put(
                    category.workingHours(),
                    new WorkingHoursCategory(category.workingHours(), category.number() + 1));
              } else {
                numbers.put(hours, new WorkingHoursCategory(hours, 1));
              }
            });
    return new WorkingHoursByNumberQueryResult(List.copyOf(numbers.values()), new TreeSet<>(tags));
  }
}
