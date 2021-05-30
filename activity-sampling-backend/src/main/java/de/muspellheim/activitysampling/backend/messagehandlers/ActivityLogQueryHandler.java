/*
 * Activity Sampling - Backend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.backend.messagehandlers;

import de.muspellheim.activitysampling.backend.EventStore;
import de.muspellheim.activitysampling.backend.events.ActivityLoggedEvent;
import de.muspellheim.activitysampling.contract.data.Activity;
import de.muspellheim.activitysampling.contract.messages.queries.ActivityLogQuery;
import de.muspellheim.activitysampling.contract.messages.queries.ActivityLogQueryResult;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class ActivityLogQueryHandler {
  private final EventStore eventStore;

  public ActivityLogQueryHandler(EventStore eventStore) {
    this.eventStore = eventStore;
  }

  public ActivityLogQueryResult handle(@SuppressWarnings("unused") ActivityLogQuery query) {
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
                .ifPresent(recent::remove);
            recent.add(it);
            if (recent.size() > 10) {
              recent.remove(0);
            }
          });
      Collections.reverse(recent);
      return new ActivityLogQueryResult(log, recent);
    } catch (Exception e) {
      return new ActivityLogQueryResult(List.of(), List.of());
    }
  }
}
