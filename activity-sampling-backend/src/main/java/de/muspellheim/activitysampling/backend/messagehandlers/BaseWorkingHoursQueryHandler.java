/*
 * Activity Sampling - Backend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.backend.messagehandlers;

import de.muspellheim.activitysampling.backend.Event;
import de.muspellheim.activitysampling.backend.EventStore;
import de.muspellheim.activitysampling.backend.events.ActivityLoggedEvent;
import de.muspellheim.activitysampling.contract.data.WorkingHours;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

class BaseWorkingHoursQueryHandler {
  protected final SortedMap<String, WorkingHours> workingHours = new TreeMap<>();

  BaseWorkingHoursQueryHandler(EventStore eventStore) {
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
}
