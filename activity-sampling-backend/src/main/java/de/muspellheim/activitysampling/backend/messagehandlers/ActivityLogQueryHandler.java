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
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class ActivityLogQueryHandler {
  private final LinkedList<Activity> log = new LinkedList<>();
  private final LinkedList<Activity> recent = new LinkedList<>();

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
      log.add(activity);

      recent.removeIf(
          it ->
              Objects.equals(it.activity(), activity.activity())
                  && Objects.equals(it.tags(), activity.tags()));
      recent.offerFirst(activity);
    }
  }

  public ActivityLogQueryResult handle(@SuppressWarnings("unused") ActivityLogQuery query) {
    return new ActivityLogQueryResult(List.copyOf(log), List.copyOf(recent));
  }
}
