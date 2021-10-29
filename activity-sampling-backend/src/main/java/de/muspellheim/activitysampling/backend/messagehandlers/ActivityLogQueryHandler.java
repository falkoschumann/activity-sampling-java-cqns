/*
 * Activity Sampling - Backend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.backend.messagehandlers;

import de.muspellheim.activitysampling.backend.Event;
import de.muspellheim.activitysampling.backend.EventStore;
import de.muspellheim.activitysampling.backend.events.ActivityLoggedEvent;
import de.muspellheim.activitysampling.contract.data.ActivityTemplate;
import de.muspellheim.activitysampling.contract.messages.queries.ActivityLogQuery;
import de.muspellheim.activitysampling.contract.messages.queries.ActivityLogQueryResult;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;

public class ActivityLogQueryHandler {
  private final LinkedList<ActivityLoggedEvent> activities = new LinkedList<>();
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
    activities.add(event);
    if (activities.size() > 1000) {
      activities.removeFirst();
    }

    last = new ActivityTemplate(event.client(), event.project(), event.task(), event.notes());
    recent.removeIf(
        it ->
            Objects.equals(it.client(), event.client())
                && Objects.equals(it.project(), event.project())
                && Objects.equals(it.task(), event.task())
                && Objects.equals(it.notes(), event.notes()));
    recent.offerFirst(last);
    if (recent.size() > 12) {
      recent.removeLast();
    }

    recentClients.add(event.client());
    recentProjects.add(event.project());
    recentTasks.add(event.task());
  }

  public ActivityLogQueryResult handle(ActivityLogQuery query) {
    return new ActivityLogQueryResult(
        getLog(),
        List.copyOf(recent),
        last,
        List.copyOf(recentClients),
        List.copyOf(recentProjects),
        List.copyOf(recentTasks));
  }

  private String getLog() {
    var dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL);
    var timeFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT);
    var log = new StringBuilder();
    for (int i = 0; i < activities.size(); i++) {
      var activity = activities.get(i);
      var date = LocalDateTime.ofInstant(activity.timestamp(), ZoneId.systemDefault());
      if (i == 0) {
        log.append(dateFormatter.format(date)).append("\n");
      } else {
        var lastActivity = activities.get(i - 1);
        var lastDate = LocalDateTime.ofInstant(lastActivity.timestamp(), ZoneId.systemDefault());
        if (!lastDate.toLocalDate().equals(date.toLocalDate())) {
          log.append(dateFormatter.format(date)).append("\n");
        }
      }

      log.append(timeFormatter.format(date))
          .append(" - ")
          .append(activity.project())
          .append(" (")
          .append(activity.client())
          .append(") ")
          .append(activity.task());
      if (activity.notes() != null) {
        log.append(" - ").append(activity.notes());
      }
      log.append("\n");
    }
    return log.toString();
  }
}
