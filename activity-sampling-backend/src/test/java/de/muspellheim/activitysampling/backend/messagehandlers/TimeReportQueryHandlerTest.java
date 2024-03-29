/*
 * Activity Sampling - Backend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.backend.messagehandlers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import de.muspellheim.activitysampling.backend.adapters.MemoryEventStore;
import de.muspellheim.activitysampling.backend.events.ActivityLoggedEvent;
import de.muspellheim.activitysampling.contract.messages.queries.TimeReportQuery;
import de.muspellheim.activitysampling.contract.messages.queries.TimeReportQueryResult;
import de.muspellheim.activitysampling.contract.messages.queries.TimeReportQueryResult.ClientEntry;
import de.muspellheim.activitysampling.contract.messages.queries.TimeReportQueryResult.ProjectEntry;
import de.muspellheim.activitysampling.contract.messages.queries.TimeReportQueryResult.SummaryEntry;
import de.muspellheim.activitysampling.contract.messages.queries.TimeReportQueryResult.TaskEntry;
import de.muspellheim.activitysampling.contract.messages.queries.TimeReportQueryResult.TimesheetEntry;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import org.junit.jupiter.api.Test;

class TimeReportQueryHandlerTest {
  @Test
  void handle_success() {
    var eventStore = new MemoryEventStore();
    eventStore.record(
        List.of(
            new ActivityLoggedEvent(
                LocalDateTime.of(2021, 8, 18, 19, 18).atZone(ZoneId.systemDefault()).toInstant(),
                Duration.ofMinutes(20),
                "Wayne Enterprises",
                "Batmobil",
                "Design",
                "Black car"),
            new ActivityLoggedEvent(
                LocalDateTime.of(2021, 8, 18, 19, 38).atZone(ZoneId.systemDefault()).toInstant(),
                Duration.ofMinutes(20),
                "Axis Chemical Co.",
                "Gotham",
                "Production",
                "Double the production of Smylex"),
            new ActivityLoggedEvent(
                LocalDateTime.of(2021, 8, 18, 20, 6).atZone(ZoneId.systemDefault()).toInstant(),
                Duration.ofMinutes(20),
                "Wayne Enterprises",
                "Batmobil",
                "Design",
                "Black car"),
            new ActivityLoggedEvent(
                LocalDateTime.of(2021, 8, 19, 20, 26).atZone(ZoneId.systemDefault()).toInstant(),
                Duration.ofMinutes(20),
                "Wayne Enterprises",
                "Batmobil",
                "Design",
                "Black car")));
    var handler = new TimeReportQueryHandler(eventStore);

    var result =
        handler.handle(new TimeReportQuery(LocalDate.of(2021, 8, 16), LocalDate.of(2021, 8, 20)));

    assertEquals(
        new TimeReportQueryResult(
            LocalDate.of(2021, 8, 16),
            LocalDate.of(2021, 8, 20),
            Duration.ofMinutes(80),
            List.of(
                new ClientEntry("Axis Chemical Co.", Duration.ofMinutes(20)),
                new ClientEntry("Wayne Enterprises", Duration.ofMinutes(60))),
            List.of(
                new ProjectEntry("Batmobil", "Wayne Enterprises", Duration.ofMinutes(60)),
                new ProjectEntry("Gotham", "Axis Chemical Co.", Duration.ofMinutes(20))),
            List.of(
                new TaskEntry("Design", Duration.ofMinutes(60)),
                new TaskEntry("Production", Duration.ofMinutes(20))),
            List.of(
                new TimesheetEntry(
                    LocalDate.of(2021, 8, 18),
                    "Wayne Enterprises",
                    "Batmobil",
                    "Design",
                    "Black car",
                    Duration.ofMinutes(40),
                    null,
                    null),
                new TimesheetEntry(
                    LocalDate.of(2021, 8, 18),
                    "Axis Chemical Co.",
                    "Gotham",
                    "Production",
                    "Double the production of Smylex",
                    Duration.ofMinutes(20),
                    null,
                    null),
                new TimesheetEntry(
                    LocalDate.of(2021, 8, 19),
                    "Wayne Enterprises",
                    "Batmobil",
                    "Design",
                    "Black car",
                    Duration.ofMinutes(20),
                    null,
                    null)),
            List.of(
                new SummaryEntry(
                    "Axis Chemical Co.", "Gotham", "Production", Duration.ofMinutes(20)),
                new SummaryEntry(
                    "Wayne Enterprises", "Batmobil", "Design", Duration.ofMinutes(60)))),
        result);
  }
}
