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
import java.util.SortedSet;
import java.util.TreeSet;

public class ActivityLogQueryHandler {
  private final LinkedList<Activity> log = new LinkedList<>();
  private final LinkedList<ActivityTemplate> recent = new LinkedList<>();
  private ActivityTemplate last;
  private final SortedSet<String> recentClients = new TreeSet<>();
  private final SortedSet<String> recentProjects = new TreeSet<>();
  private final SortedSet<String> recentTasks = new TreeSet<>();

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
            event.client(),
            event.project(),
            event.task(),
            event.notes());
    log.add(activity);
    if (log.size() > 1000) {
      log.removeFirst();
    }

    last =
        new ActivityTemplate(
            activity.client(), activity.project(), activity.task(), activity.notes());
    recent.removeIf(
        it ->
            Objects.equals(it.client(), activity.client())
                && Objects.equals(it.project(), activity.project())
                && Objects.equals(it.task(), activity.task())
                && Objects.equals(it.notes(), activity.notes()));
    recent.offerFirst(last);
    if (recent.size() > 12) {
      recent.removeLast();
    }

    if (event.client() != null) {
      recentClients.add(event.client());
    }
    if (event.project() != null) {
      recentProjects.add(event.project());
    }
    if (event.task() != null) {
      recentTasks.add(event.task());
    }
  }

  public ActivityLogQueryResult handle(ActivityLogQuery query) {
    return new ActivityLogQueryResult(
        List.copyOf(log),
        List.copyOf(recent),
        last,
        List.copyOf(recentClients),
        List.copyOf(recentProjects),
        List.copyOf(recentTasks));
  }
}
