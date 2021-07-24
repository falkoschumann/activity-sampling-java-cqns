/*
 * Activity Sampling - Backend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.backend.messagehandlers;

import de.muspellheim.activitysampling.backend.Event;
import de.muspellheim.activitysampling.backend.EventStore;
import de.muspellheim.activitysampling.backend.events.ActivityLoggedEvent;
import de.muspellheim.activitysampling.contract.data.Activity;
import de.muspellheim.activitysampling.contract.data.ActivityTemplate;
import de.muspellheim.activitysampling.contract.messages.queries.ActivityLogQuery;
import de.muspellheim.activitysampling.contract.messages.queries.ActivityLogQueryResult;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.TreeSet;

public class ActivityLogQueryHandler {
  private final LinkedList<Activity> log = new LinkedList<>();
  private final LinkedList<ActivityTemplate> recent = new LinkedList<>();
  private ActivityTemplate last = ActivityTemplate.NULL;
  private LinkedList<String> recentTags = new LinkedList<>();

  public ActivityLogQueryHandler(EventStore eventStore) {
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
    log.add(activity);
    if (log.size() > 1000) {
      log.removeFirst();
    }

    last = new ActivityTemplate(activity.activity(), activity.tags());

    recent.removeIf(
        it ->
            Objects.equals(it.activity(), activity.activity())
                && Objects.equals(it.tags(), activity.tags()));
    recent.offerFirst(last);
    if (recent.size() > 12) {
      recent.removeLast();
    }

    recentTags.addAll(activity.tags());
    recentTags = new LinkedList<>(recentTags.stream().distinct().toList());
    while (recentTags.size() > 24) {
      recentTags.removeFirst();
    }
  }

  public ActivityLogQueryResult handle(ActivityLogQuery query) {
    return new ActivityLogQueryResult(
        List.copyOf(log), List.copyOf(recent), last, List.copyOf(new TreeSet<>(recentTags)));
  }
}
