/*
 * Activity Sampling - Contract
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.contract.data;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Objects;

public record Timesheets(
    LocalDate date,
    String client,
    String project,
    String task,
    String notes,
    Duration hours,
    String firstName,
    String lastName) {
  public Timesheets {
    Objects.requireNonNull(date, "date");
    Objects.requireNonNull(notes, "notes");
    Objects.requireNonNull(hours, "hours");
  }
}
