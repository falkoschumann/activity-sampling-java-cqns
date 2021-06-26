/*
 * Activity Sampling - Backend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.backend.messagehandlers;

import de.muspellheim.activitysampling.backend.Event;
import de.muspellheim.activitysampling.backend.EventStore;
import de.muspellheim.activitysampling.backend.events.ActivityLoggedEvent;
import de.muspellheim.activitysampling.contract.data.WorkingHours;
import de.muspellheim.activitysampling.contract.messages.queries.WorkingHoursByNumberQuery;
import de.muspellheim.activitysampling.contract.messages.queries.WorkingHoursByNumberQueryResult;
import de.muspellheim.activitysampling.contract.messages.queries.WorkingHoursByNumberQueryResult.WorkingHoursCategory;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class WorkingHoursByNumberQueryHandler {
  private final SortedMap<String, WorkingHours> workingHours = new TreeMap<>();

  public WorkingHoursByNumberQueryHandler(EventStore eventStore) {
    eventStore.replay(ActivityLoggedEvent.class).forEach(this::apply);
    eventStore.addRecordedObserver(this::apply);
  }

  private void apply(Event event) {
    if (event instanceof ActivityLoggedEvent e) {
      apply(e);
    }
  }

  private void apply(ActivityLoggedEvent event) {
    if (workingHours.containsKey(event.activity())) {
      var workingHours = this.workingHours.get(event.activity());
      var tags = new LinkedHashSet<>(workingHours.tags());
      tags.addAll(event.tags());
      this.workingHours.put(
          workingHours.activity(),
          new WorkingHours(
              workingHours.activity(),
              List.copyOf(tags),
              event.period().plus(workingHours.workingHours())));
    } else {
      workingHours.put(
          event.activity(), new WorkingHours(event.activity(), event.tags(), event.period()));
    }
  }

  public WorkingHoursByNumberQueryResult handle(
      @SuppressWarnings("unused") WorkingHoursByNumberQuery query) {
    var numbers = new TreeMap<Duration, WorkingHoursCategory>();
    workingHours
        .values()
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
    return new WorkingHoursByNumberQueryResult(List.copyOf(numbers.values()));
  }
}
