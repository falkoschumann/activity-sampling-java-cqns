/*
 * Activity Sampling - Backend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.backend.messagehandlers;

import de.muspellheim.activitysampling.backend.Event;
import de.muspellheim.activitysampling.backend.EventStore;
import de.muspellheim.activitysampling.backend.events.ActivityLoggedEvent;
import de.muspellheim.activitysampling.contract.messages.queries.WorkingHoursByActivityQuery;
import de.muspellheim.activitysampling.contract.messages.queries.WorkingHoursByActivityQueryResult;
import de.muspellheim.activitysampling.contract.messages.queries.WorkingHoursByActivityQueryResult.WorkingHours;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class WorkingHoursByActivityQueryHandler {
  private final SortedMap<String, WorkingHours> activities = new TreeMap<>();

  public WorkingHoursByActivityQueryHandler(EventStore eventStore) {
    eventStore.replay(ActivityLoggedEvent.class).forEach(this::apply);
    eventStore.addRecordedObserver(this::apply);
  }

  private void apply(Event event) {
    if (event instanceof ActivityLoggedEvent e) {
      apply(e);
    }
  }

  private void apply(ActivityLoggedEvent event) {
    if (activities.containsKey(event.activity())) {
      var workingHours = this.activities.get(event.activity());
      this.activities.put(
          workingHours.activity(),
          new WorkingHours(
              workingHours.activity(), event.period().plus(workingHours.workingHours())));
    } else {
      activities.put(event.activity(), new WorkingHours(event.activity(), event.period()));
    }
  }

  public WorkingHoursByActivityQueryResult handle(
      @SuppressWarnings("unused") WorkingHoursByActivityQuery query) {
    return new WorkingHoursByActivityQueryResult(List.copyOf(activities.values()));
  }
}
