/*
 * Activity Sampling - Backend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.backend.messagehandlers;

import de.muspellheim.activitysampling.backend.EventStore;
import de.muspellheim.activitysampling.backend.events.ActivityLoggedEvent;
import de.muspellheim.activitysampling.contract.data.Activity;
import de.muspellheim.activitysampling.contract.messages.queries.RecentActivitiesQuery;
import de.muspellheim.activitysampling.contract.messages.queries.RecentActivitiesQueryResult;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import lombok.extern.java.Log;

@Log
public class RecentActivitiesQueryHandler {
  // TODO Nutze Eventstore#onRecorded, anstelle Replay bei jedem Aufruf
  private final EventStore eventStore;

  public RecentActivitiesQueryHandler(EventStore eventStore) {
    this.eventStore = eventStore;
  }

  public RecentActivitiesQueryResult handle(RecentActivitiesQuery query) {
    try {
      var log =
          eventStore
              .replay(ActivityLoggedEvent.class)
              .map(
                  it ->
                      new Activity(
                          it.id(),
                          LocalDateTime.ofInstant(it.timestamp(), ZoneId.systemDefault()),
                          it.period(),
                          it.activity(),
                          it.tags()))
              .toList();

      var recent = new LinkedList<Activity>();
      log.forEach(
          it -> {
            recent.stream()
                .filter(
                    other ->
                        Objects.equals(it.activity(), other.activity())
                            && Objects.equals(it.tags(), other.tags()))
                .findFirst()
                .ifPresent(same -> recent.remove(same));
            recent.add(it);
            if (recent.size() > 10) {
              recent.remove(0);
            }
          });
      Collections.reverse(recent);
      return new RecentActivitiesQueryResult(recent);
    } catch (Exception e) {
      log.log(Level.WARNING, "Can not handle query: " + query, e);
      return new RecentActivitiesQueryResult(List.of());
    }
  }
}
