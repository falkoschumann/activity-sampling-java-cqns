/*
 * Activity Sampling - Backend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.backend.messagehandlers;

import de.muspellheim.activitysampling.backend.Event;
import de.muspellheim.activitysampling.backend.EventStore;
import de.muspellheim.activitysampling.backend.events.ActivityLoggedEvent;
import de.muspellheim.activitysampling.contract.messages.queries.TimeReportQuery;
import de.muspellheim.activitysampling.contract.messages.queries.TimeReportQueryResult;
import de.muspellheim.activitysampling.contract.messages.queries.TimeReportQueryResult.ClientEntry;
import de.muspellheim.activitysampling.contract.messages.queries.TimeReportQueryResult.ProjectEntry;
import de.muspellheim.activitysampling.contract.messages.queries.TimeReportQueryResult.TaskEntry;
import de.muspellheim.activitysampling.contract.messages.queries.TimeReportQueryResult.TimesheetEntry;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class TimeReportQueryHandler {
  private final SortedMap<LocalDate, List<TimesheetEntry>> timesheet = new TreeMap<>();

  public TimeReportQueryHandler(EventStore eventStore) {
    eventStore.replay(ActivityLoggedEvent.class).forEach(this::apply);
    eventStore.addRecordedObserver(this::apply);
  }

  private void apply(Event event) {
    if (event instanceof ActivityLoggedEvent e) {
      apply(e);
    }
  }

  private void apply(ActivityLoggedEvent event) {
    var date = LocalDate.ofInstant(event.timestamp(), ZoneId.systemDefault());
    var entries = timesheet.computeIfAbsent(date, it -> new ArrayList<>());
    // TODO Extrahiere Methoden
    var index = -1;
    for (var i = 0; i < entries.size(); i++) {
      var entry = entries.get(i);
      if (Objects.equals(entry.client(), event.client())
          && Objects.equals(entry.project(), event.project())
          && Objects.equals(entry.task(), event.task())
          && Objects.equals(entry.notes(), event.notes())) {
        index = i;
        break;
      }
    }
    if (index == -1) {
      var entry =
          new TimesheetEntry(
              date,
              event.client(),
              event.project(),
              event.task(),
              event.notes(),
              event.period(),
              null,
              null);
      entries.add(entry);
    } else {
      var entry = addHours(entries.get(index), event.period());
      entries.set(index, entry);
    }
  }

  private static TimesheetEntry addHours(TimesheetEntry entry, Duration value) {
    return new TimesheetEntry(
        entry.date(),
        entry.client(),
        entry.project(),
        entry.task(),
        entry.notes(),
        entry.hours().plus(value),
        entry.firstName(),
        entry.lastName());
  }

  public TimeReportQueryResult handle(TimeReportQuery query) {
    LocalDate start;
    LocalDate end;
    if (query.start() != null && query.end() != null) {
      start = query.start();
      end = query.end();
    } else {
      end = LocalDate.now();
      start = end.minusDays(end.getDayOfWeek().getValue() - 1);
    }
    return timesheet.subMap(start, end.plusDays(1)).values().stream()
        .flatMap(Collection::stream)
        .collect(new TimeReportCollector(start, end));
  }

  private static class TimeReportCollector
      implements Collector<TimesheetEntry, TimeReportData, TimeReportQueryResult> {
    private final LocalDate start;
    private final LocalDate end;

    public TimeReportCollector(LocalDate start, LocalDate end) {
      this.start = start;
      this.end = end;
    }

    @Override
    public Supplier<TimeReportData> supplier() {
      return () -> new TimeReportData(start, end);
    }

    @Override
    public BiConsumer<TimeReportData, TimesheetEntry> accumulator() {
      // TODO Extrahiere Methoden
      return (d, e) -> {
        if (d.totalHours.isZero()) {
          d.totalHours = e.hours();
          d.clients.add(new ClientEntry(e.client(), e.hours()));
          d.projects.add(new ProjectEntry(e.project(), e.client(), e.hours()));
          d.tasks.add(new TaskEntry(e.task(), e.hours()));
          d.timesheet.add(e);
        } else {
          d.totalHours = d.totalHours.plus(e.hours());
          d.timesheet.add(e);

          var clientIndex = -1;
          for (var i = 0; i < d.clients.size(); i++) {
            var entry = d.clients.get(i);
            if (Objects.equals(entry.client(), e.client())) {
              clientIndex = i;
              break;
            }
          }
          if (clientIndex == -1) {
            var entry = new ClientEntry(e.client(), e.hours());
            d.clients.add(entry);
            d.clients.sort((e1, e2) -> e1.client().compareToIgnoreCase(e2.client()));
          } else {
            var entry = addHours(d.clients.get(clientIndex), e.hours());
            d.clients.set(clientIndex, entry);
          }

          var projectIndex = -1;
          for (var i = 0; i < d.projects.size(); i++) {
            var entry = d.projects.get(i);
            if (Objects.equals(entry.client(), e.client())
                && Objects.equals(entry.project(), e.project())) {
              projectIndex = i;
              break;
            }
          }
          if (projectIndex == -1) {
            var entry = new ProjectEntry(e.project(), e.client(), e.hours());
            d.projects.add(entry);
            d.projects.sort(
                (e1, e2) -> {
                  var pc = e1.project().compareToIgnoreCase(e2.project());
                  if (pc != 0) {
                    return pc;
                  }
                  return e1.client().compareToIgnoreCase(e2.client());
                });
          } else {
            var entry = addHours(d.projects.get(projectIndex), e.hours());
            d.projects.set(projectIndex, entry);
          }

          var taskIndex = -1;
          for (var i = 0; i < d.tasks.size(); i++) {
            var entry = d.tasks.get(i);
            if (Objects.equals(entry.task(), e.task())) {
              taskIndex = i;
              break;
            }
          }
          if (taskIndex == -1) {
            var entry = new TaskEntry(e.task(), e.hours());
            d.tasks.add(entry);
            d.tasks.sort((e1, e2) -> e1.task().compareToIgnoreCase(e2.task()));
          } else {
            var entry = addHours(d.tasks.get(taskIndex), e.hours());
            d.tasks.set(taskIndex, entry);
          }
        }
      };
    }

    private static ClientEntry addHours(ClientEntry entry, Duration value) {
      return new ClientEntry(entry.client(), entry.hours().plus(value));
    }

    private static ProjectEntry addHours(ProjectEntry entry, Duration value) {
      return new ProjectEntry(entry.project(), entry.client(), entry.hours().plus(value));
    }

    private static TaskEntry addHours(TaskEntry entry, Duration value) {
      return new TaskEntry(entry.task(), entry.hours().plus(value));
    }

    @Override
    public BinaryOperator<TimeReportData> combiner() {
      return (d1, d2) -> {
        throw new UnsupportedOperationException();
      };
    }

    @Override
    public Function<TimeReportData, TimeReportQueryResult> finisher() {
      return d ->
          new TimeReportQueryResult(
              d.start, d.end, d.totalHours, d.clients, d.projects, d.tasks, d.timesheet);
    }

    @Override
    public Set<Characteristics> characteristics() {
      return Set.of();
    }
  }

  private static class TimeReportData {
    final LocalDate start;
    final LocalDate end;
    Duration totalHours = Duration.ZERO;
    List<ClientEntry> clients = new ArrayList<>();
    List<ProjectEntry> projects = new ArrayList<>();
    List<TaskEntry> tasks = new ArrayList<>();
    List<TimesheetEntry> timesheet = new ArrayList<>();

    TimeReportData(LocalDate start, LocalDate end) {
      this.start = start;
      this.end = end;
    }
  }
}
