/*
 * Activity Sampling - Contract
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.contract.messages.queries;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public record TimeReportQueryResult(
    LocalDate start,
    LocalDate end,
    Duration totalHours,
    List<ClientEntry> clients,
    List<ProjectEntry> projects,
    List<TaskEntry> tasks,
    List<TimesheetEntry> timesheet) {
  public TimeReportQueryResult {
    Objects.requireNonNull(start, "start");
    Objects.requireNonNull(end, "end");
    Objects.requireNonNull(totalHours, "totalHours");
    Objects.requireNonNull(clients, "clients");
    Objects.requireNonNull(projects, "projects");
    Objects.requireNonNull(tasks, "tasks");
    Objects.requireNonNull(timesheet, "timesheet");
  }

  public static record ClientEntry(String client, Duration hours) {
    public ClientEntry {
      Objects.requireNonNull(client, "client");
      Objects.requireNonNull(hours, "hours");
    }
  }

  public static record ProjectEntry(String project, String client, Duration hours) {
    public ProjectEntry {
      Objects.requireNonNull(client, "client");
      Objects.requireNonNull(project, "project");
      Objects.requireNonNull(hours, "hours");
    }
  }

  public static record TaskEntry(String task, Duration hours) {
    public TaskEntry {
      Objects.requireNonNull(task, "task");
      Objects.requireNonNull(hours, "hours");
    }
  }

  // TODO TeamEntry(name, hours)

  public static record TimesheetEntry(
      LocalDate date,
      String client,
      String project,
      String task,
      String notes,
      Duration hours,
      String firstName,
      String lastName) {
    public TimesheetEntry {
      Objects.requireNonNull(date, "date");
      Objects.requireNonNull(client, "client");
      Objects.requireNonNull(project, "project");
      Objects.requireNonNull(task, "task");
      Objects.requireNonNull(hours, "hours");
    }
  }
}
