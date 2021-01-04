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
import de.muspellheim.messages.QueryHandling;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ActivityLogQueryHandler
    implements QueryHandling<ActivityLogQuery, ActivityLogQueryResult> {
  private final EventStore eventStore;

  public ActivityLogQueryHandler(EventStore eventStore) {
    this.eventStore = eventStore;
  }

  @Override
  public ActivityLogQueryResult handle(ActivityLogQuery activityLogQuery) {
    try {
      var log =
          eventStore
              .replay(ActivityLoggedEvent.class)
              .map(
                  it ->
                      new Activity(
                          it.getId(),
                          LocalDateTime.ofInstant(it.getTimestamp(), ZoneId.systemDefault()),
                          it.getPeriod(),
                          it.getActivity(),
                          mapTags(it.getTags())))
              .collect(Collectors.toUnmodifiableList());

      var recent = new LinkedList<Activity>();
      log.forEach(
          it -> {
            recent.stream()
                .filter(
                    other ->
                        Objects.equals(it.getActivity(), other.getActivity())
                            && Objects.equals(it.getTags(), other.getTags()))
                .findFirst()
                .ifPresent(same -> recent.remove(same));
            recent.add(it);
            if (recent.size() > 10) {
              recent.remove(0);
            }
          });
      Collections.reverse(recent);
      return new ActivityLogQueryResult(log, recent);
    } catch (Exception e) {
      System.err.println(e);
      return new ActivityLogQueryResult(List.of(), List.of());
    }
  }

  private static List<String> mapTags(String tags) {
    if (tags == null) {
      return List.of();
    }

    return List.of(tags.split(",")).stream().map(it -> it.strip()).collect(Collectors.toList());
  }
}
