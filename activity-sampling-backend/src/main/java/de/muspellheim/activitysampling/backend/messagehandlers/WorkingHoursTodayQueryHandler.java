/*
 * Activity Sampling - Backend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.backend.messagehandlers;

import de.muspellheim.activitysampling.backend.Event;
import de.muspellheim.activitysampling.backend.EventStore;
import de.muspellheim.activitysampling.backend.events.ActivityLoggedEvent;
import de.muspellheim.activitysampling.contract.data.Activity;
import de.muspellheim.activitysampling.contract.messages.queries.WorkingHoursTodayQuery;
import de.muspellheim.activitysampling.contract.messages.queries.WorkingHoursTodayQueryResult;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.LinkedList;
import java.util.List;

public class WorkingHoursTodayQueryHandler {
  private final LinkedList<Activity> activities = new LinkedList<>();
  private final Clock clock;
  private LocalDate today;

  public WorkingHoursTodayQueryHandler(EventStore eventStore) {
    this(eventStore, Clock.systemDefaultZone());
  }

  public WorkingHoursTodayQueryHandler(EventStore eventStore, Clock clock) {
    this.clock = clock;

    eventStore.replay(ActivityLoggedEvent.class).forEach(this::apply);
    eventStore.addRecordedObserver(this::apply);
  }

  private void apply(Event event) {
    if (event instanceof ActivityLoggedEvent e) {
      apply(e);
    }
  }

  private void apply(ActivityLoggedEvent event) {
    var activity =
        new Activity(
            event.id(),
            LocalDateTime.ofInstant(event.timestamp(), ZoneId.systemDefault()),
            event.period(),
            event.activity(),
            event.tags());
    activities.add(activity);

    today = LocalDate.now(clock);
    activities.removeIf(it -> !it.timestamp().toLocalDate().equals(today));
  }

  public WorkingHoursTodayQueryResult handle(WorkingHoursTodayQuery query) {
    var tags = ActivityProjection.distinctTags(activities.stream());
    var filtered =
        ActivityProjection.filterTags(activities.stream(), query.includedTags()).toList();
    var totalWorkingHours = ActivityProjection.totalWorkingHours(filtered.stream());
    return new WorkingHoursTodayQueryResult(today, totalWorkingHours, List.copyOf(filtered), tags);
  }
}
