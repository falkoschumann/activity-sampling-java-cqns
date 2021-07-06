/*
 * Activity Sampling - Backend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.backend.messagehandlers;

import de.muspellheim.activitysampling.backend.Event;
import de.muspellheim.activitysampling.backend.EventStore;
import de.muspellheim.activitysampling.backend.events.ActivityLoggedEvent;
import de.muspellheim.activitysampling.contract.data.Activity;
import de.muspellheim.activitysampling.contract.messages.queries.WorkingHoursThisWeekQuery;
import de.muspellheim.activitysampling.contract.messages.queries.WorkingHoursThisWeekQueryResult;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.WeekFields;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

public class WorkingHoursThisWeekQueryHandler {
  private final LinkedList<Activity> activities = new LinkedList<>();
  private final Clock clock;
  private int calenderWeek;

  public WorkingHoursThisWeekQueryHandler(EventStore eventStore) {
    this(eventStore, Clock.systemDefaultZone());
  }

  public WorkingHoursThisWeekQueryHandler(EventStore eventStore, Clock clock) {
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

    calenderWeek = LocalDate.now(clock).get(WeekFields.ISO.weekOfYear());
    activities.removeIf(it -> calenderWeek != it.timestamp().get(WeekFields.ISO.weekOfYear()));
  }

  public WorkingHoursThisWeekQueryResult handle(WorkingHoursThisWeekQuery query) {
    var tags = ActivityProjection.distinctTags(activities.stream());
    var filtered =
        ActivityProjection.filterTags(activities.stream(), query.includedTags()).toList();
    var totalWorkingHours = ActivityProjection.totalWorkingHours(filtered.stream());
    return new WorkingHoursThisWeekQueryResult(
        calenderWeek, totalWorkingHours, List.copyOf(filtered), new TreeSet<>(tags));
  }
}
