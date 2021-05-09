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
import java.util.List;
import java.util.logging.Level;
import lombok.extern.java.Log;

@Log
public class ActivityLogQueryHandler {
  // TODO Nutze Replay nur bei Initialisierung und danach Eventstore#onRecorded,
  //  anstelle Replay bei jedem Aufruf
  private final EventStore eventStore;

  public ActivityLogQueryHandler(EventStore eventStore) {
    this.eventStore = eventStore;
  }

  public ActivityLogQueryResult handle(ActivityLogQuery query) {
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
      return new ActivityLogQueryResult(log);
    } catch (Exception e) {
      log.log(Level.WARNING, "Can not handle query: " + query, e);
      return new ActivityLogQueryResult(List.of());
    }
  }
}
