/*
 * Activity Sampling - Backend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.backend.messagehandlers;

import de.muspellheim.activitysampling.backend.Event;
import de.muspellheim.activitysampling.backend.EventStore;
import de.muspellheim.activitysampling.backend.events.ActivityLoggedEvent;
import de.muspellheim.activitysampling.contract.data.Activity;
import de.muspellheim.activitysampling.contract.messages.queries.RecentActivitiesQuery;
import de.muspellheim.activitysampling.contract.messages.queries.RecentActivitiesQueryResult;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import lombok.extern.java.Log;

@Log
public class RecentActivitiesQueryHandler {
  private final LinkedList<Activity> activities = new LinkedList<>();

  public RecentActivitiesQueryHandler(EventStore eventStore) {
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
      activities.removeIf(
          it ->
              Objects.equals(it.activity(), activity.activity())
                  && Objects.equals(it.tags(), activity.tags()));
      activities.offerFirst(activity);
    }
  }

  public RecentActivitiesQueryResult handle(RecentActivitiesQuery query) {
    return new RecentActivitiesQueryResult(List.copyOf(activities));
  }
}
