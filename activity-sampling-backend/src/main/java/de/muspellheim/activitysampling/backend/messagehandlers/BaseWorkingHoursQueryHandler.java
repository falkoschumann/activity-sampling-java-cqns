/*
 * Activity Sampling - Backend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.backend.messagehandlers;

import de.muspellheim.activitysampling.backend.Event;
import de.muspellheim.activitysampling.backend.EventStore;
import de.muspellheim.activitysampling.backend.events.ActivityLoggedEvent;
import de.muspellheim.activitysampling.contract.data.WorkingHours;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

class BaseWorkingHoursQueryHandler {
  protected final List<WorkingHours> workingHours = new ArrayList<>();

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
    var index = -1;
    for (var i = 0; i < workingHours.size(); i++) {
      var it = workingHours.get(i);
      if (it.activity().equals(event.activity()) && it.tags().equals(event.tags())) {
        index = i;
        break;
      }
    }

    if (index != -1) {
      var workingHours = this.workingHours.get(index);
      var tags = new LinkedHashSet<>(workingHours.tags());
      tags.addAll(event.tags());
      this.workingHours.set(
          index,
          new WorkingHours(
              workingHours.activity(),
              List.copyOf(tags),
              event.period().plus(workingHours.workingHours())));
    } else {
      workingHours.add(new WorkingHours(event.activity(), event.tags(), event.period()));
      workingHours.sort(
          (e1, e2) -> {
            var a = e1.activity().compareTo(e2.activity());
            if (a != 0) {
              return a;
            } else {
              return Integer.compare(e1.tags().size(), e2.tags().size());
            }
          });
    }
  }
}
