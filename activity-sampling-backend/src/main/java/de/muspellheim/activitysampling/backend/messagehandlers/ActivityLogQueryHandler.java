/*
 * Activity Sampling - Backend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.backend.messagehandlers;

import de.muspellheim.activitysampling.backend.Event;
import de.muspellheim.activitysampling.backend.EventStore;
import de.muspellheim.activitysampling.backend.events.ActivityLoggedEvent;
import de.muspellheim.activitysampling.contract.data.Activity;
import de.muspellheim.activitysampling.contract.messages.queries.ActivityLogQuery;
import de.muspellheim.activitysampling.contract.messages.queries.ActivityLogQueryResult;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.java.Log;

@Log
public class ActivityLogQueryHandler {
  private final List<Activity> activities = new ArrayList<>();

  public ActivityLogQueryHandler(EventStore eventStore) {
    eventStore.replay(ActivityLoggedEvent.class).forEach(this::apply);
    eventStore.addRecordedObserver(this::apply);
  }

  private void apply(Event event) {
    if (event instanceof ActivityLoggedEvent e) {
      var activity =
          new Activity(
              e.id(),
              LocalDateTime.ofInstant(e.timestamp(), ZoneId.systemDefault()),
              e.period(),
              e.activity(),
              e.tags());
      activities.add(activity);
    }
  }

  public ActivityLogQueryResult handle(ActivityLogQuery query) {
    return new ActivityLogQueryResult(List.copyOf(activities));
  }
}
