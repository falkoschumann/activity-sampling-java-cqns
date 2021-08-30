/*
 * Activity Sampling - Application
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import de.muspellheim.activitysampling.backend.adapters.MemoryEventStore;
import de.muspellheim.activitysampling.backend.adapters.MemoryPreferencesRepository;
import de.muspellheim.activitysampling.contract.data.ActivityTemplate;
import de.muspellheim.activitysampling.contract.data.Bounds;
import de.muspellheim.activitysampling.contract.messages.commands.ChangeMainWindowBoundsCommand;
import de.muspellheim.activitysampling.contract.messages.commands.ChangePreferencesCommand;
import de.muspellheim.activitysampling.contract.messages.commands.LogActivityCommand;
import de.muspellheim.activitysampling.contract.messages.notification.ClockTickedNotification;
import de.muspellheim.activitysampling.contract.messages.notification.PeriodProgressedNotification;
import de.muspellheim.activitysampling.contract.messages.queries.ActivityLogQuery;
import de.muspellheim.activitysampling.contract.messages.queries.ActivityLogQueryResult;
import de.muspellheim.activitysampling.contract.messages.queries.MainWindowBoundsQuery;
import de.muspellheim.activitysampling.contract.messages.queries.MainWindowBoundsQueryResult;
import de.muspellheim.activitysampling.contract.messages.queries.PreferencesQuery;
import de.muspellheim.activitysampling.contract.messages.queries.PreferencesQueryResult;
import de.muspellheim.activitysampling.contract.messages.queries.TimeReportQuery;
import de.muspellheim.activitysampling.contract.messages.queries.TimeReportQueryResult;
import de.muspellheim.activitysampling.contract.messages.queries.TimeReportQueryResult.ClientEntry;
import de.muspellheim.activitysampling.contract.messages.queries.TimeReportQueryResult.ProjectEntry;
import de.muspellheim.activitysampling.contract.messages.queries.TimeReportQueryResult.TaskEntry;
import de.muspellheim.activitysampling.contract.messages.queries.TimeReportQueryResult.TimesheetEntry;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Locale;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;

@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
class AcceptanceTest {
  private static RequestHandler requestHandler;
  private static PeriodProgressedNotification periodProgressedNotification;

  @BeforeAll
  static void initAll() {
    Locale.setDefault(Locale.GERMANY);
    // TODO Teste gegen "richtigen" Event Store
    var eventStore = new MemoryEventStore();
    // TODO Teste gegen "richtigen" Preferences Repository
    var preferencesRepository = new MemoryPreferencesRepository();
    requestHandler = new RequestHandler(eventStore, preferencesRepository);
    requestHandler.setOnPeriodProgressedNotification(n -> periodProgressedNotification = n);
  }

  @Test
  @Order(1)
  void startUp_InitialValues() {
    var mainWindowBoundsQueryResult = requestHandler.handle(new MainWindowBoundsQuery());
    assertEquals(new MainWindowBoundsQueryResult(Bounds.NULL), mainWindowBoundsQueryResult);

    var preferencesQueryResult = requestHandler.handle(new PreferencesQuery());
    assertEquals(new PreferencesQueryResult(Duration.ofMinutes(20)), preferencesQueryResult);

    var activityLogQueryResult = requestHandler.handle(new ActivityLogQuery());
    assertEquals(
        new ActivityLogQueryResult("", List.of(), null, List.of(), List.of(), List.of()),
        activityLogQueryResult);
  }

  @Test
  @Order(2)
  void timeReport_Empty() {
    var result =
        requestHandler.handle(
            new TimeReportQuery(LocalDate.of(2021, 1, 1), LocalDate.of(2021, 8, 29)));

    assertEquals(
        new TimeReportQueryResult(
            LocalDate.of(2021, 1, 1),
            LocalDate.of(2021, 8, 29),
            Duration.ZERO,
            List.of(),
            List.of(),
            List.of(),
            List.of()),
        result);
  }

  @Test
  @Order(3)
  void changeMainWindowBounds() {
    requestHandler.handle(new ChangeMainWindowBoundsCommand(new Bounds(40, 60, 400, 600)));

    var result = requestHandler.handle(new MainWindowBoundsQuery());
    assertEquals(new MainWindowBoundsQueryResult(new Bounds(40, 60, 400, 600)), result);
  }

  @Test
  @Order(4)
  void changePreferences() {
    // TODO Ändere period duration während period läuft
    var preferencesQueryResult =
        requestHandler.handle(new ChangePreferencesCommand(Duration.ofMinutes(12)));

    assertEquals(new PreferencesQueryResult(Duration.ofMinutes(12)), preferencesQueryResult);
  }

  @Test
  @Order(5)
  void clockTicked_Period1Started() {
    assertNull(periodProgressedNotification);
    requestHandler.handle(new ClockTickedNotification(LocalDateTime.of(2021, 8, 29, 18, 25)));

    assertEquals(
        new PeriodProgressedNotification(LocalTime.of(0, 12), 0.0, null),
        periodProgressedNotification);
  }

  @Test
  @Order(6)
  void clockTicked_Period1Progressed() {
    requestHandler.handle(new ClockTickedNotification(LocalDateTime.of(2021, 8, 29, 18, 34)));

    assertEquals(
        new PeriodProgressedNotification(LocalTime.of(0, 3), 0.75, null),
        periodProgressedNotification);
  }

  @Test
  @Order(7)
  void clockTicked_Period1Ended() {
    // TODO PeriodProgressedNotification ohne timestamp?
    requestHandler.handle(new ClockTickedNotification(LocalDateTime.of(2021, 8, 29, 18, 37)));

    assertEquals(
        new PeriodProgressedNotification(
            LocalTime.of(0, 0), 1.0, LocalDateTime.of(2021, 8, 29, 18, 37)),
        periodProgressedNotification);
  }

  @Test
  @Order(8)
  void logActivity1() {
    // TODO LogActivityCommand ohne timestamp und duration?
    var result =
        requestHandler.handle(
            new LogActivityCommand(
                LocalDateTime.of(2021, 8, 29, 18, 37),
                Duration.ofMinutes(12),
                "ACME Ltd.",
                "Foobar",
                "Analyze",
                "Analyze requirements"));

    assertEquals(
        new ActivityLogQueryResult(
            """
          Sonntag, 29. August 2021
          18:37 - Analyze requirements
          """,
            List.of(new ActivityTemplate("ACME Ltd.", "Foobar", "Analyze", "Analyze requirements")),
            new ActivityTemplate("ACME Ltd.", "Foobar", "Analyze", "Analyze requirements"),
            List.of("ACME Ltd."),
            List.of("Foobar"),
            List.of("Analyze")),
        result);
  }

  @Test
  @Order(9)
  void clockTicked_Period2Progressed() {
    requestHandler.handle(new ClockTickedNotification(LocalDateTime.of(2021, 8, 29, 18, 43)));

    assertEquals(
        new PeriodProgressedNotification(LocalTime.of(0, 6), 0.5, null),
        periodProgressedNotification);
  }

  @Test
  @Order(10)
  void clockTicked_Period2Ended() {
    requestHandler.handle(new ClockTickedNotification(LocalDateTime.of(2021, 8, 29, 18, 49)));

    assertEquals(
        new PeriodProgressedNotification(
            LocalTime.of(0, 0), 1.0, LocalDateTime.of(2021, 8, 29, 18, 49)),
        periodProgressedNotification);
  }

  @Test
  @Order(11)
  void clockTicked_Period3Progressed() {
    requestHandler.handle(new ClockTickedNotification(LocalDateTime.of(2021, 8, 29, 18, 52)));

    assertEquals(
        new PeriodProgressedNotification(LocalTime.of(0, 9), 0.25, null),
        periodProgressedNotification);
  }

  @Test
  @Order(12)
  void logActivity2() {
    var result =
        requestHandler.handle(
            new LogActivityCommand(
                LocalDateTime.of(2021, 8, 29, 18, 49),
                Duration.ofMinutes(12),
                "ACME Ltd.",
                "Foobar",
                "Design",
                "Design architecture"));

    assertEquals(
        new ActivityLogQueryResult(
            """
        Sonntag, 29. August 2021
        18:37 - Analyze requirements
        18:49 - Design architecture
        """,
            List.of(
                new ActivityTemplate("ACME Ltd.", "Foobar", "Design", "Design architecture"),
                new ActivityTemplate("ACME Ltd.", "Foobar", "Analyze", "Analyze requirements")),
            new ActivityTemplate("ACME Ltd.", "Foobar", "Design", "Design architecture"),
            List.of("ACME Ltd."),
            List.of("Foobar"),
            List.of("Analyze", "Design")),
        result);
  }

  @Test
  @Order(13)
  void timeReport() {
    var result =
        requestHandler.handle(
            new TimeReportQuery(LocalDate.of(2021, 1, 1), LocalDate.of(2021, 8, 29)));

    assertEquals(
        new TimeReportQueryResult(
            LocalDate.of(2021, 1, 1),
            LocalDate.of(2021, 8, 29),
            Duration.ofMinutes(24),
            List.of(new ClientEntry("ACME Ltd.", Duration.ofMinutes(24))),
            List.of(new ProjectEntry("Foobar", "ACME Ltd.", Duration.ofMinutes(24))),
            List.of(
                new TaskEntry("Analyze", "Foobar", "ACME Ltd.", Duration.ofMinutes(12)),
                new TaskEntry("Design", "Foobar", "ACME Ltd.", Duration.ofMinutes(12))),
            List.of(
                new TimesheetEntry(
                    LocalDate.of(2021, 8, 29),
                    "ACME Ltd.",
                    "Foobar",
                    "Analyze",
                    "Analyze requirements",
                    Duration.ofMinutes(12),
                    null,
                    null),
                new TimesheetEntry(
                    LocalDate.of(2021, 8, 29),
                    "ACME Ltd.",
                    "Foobar",
                    "Design",
                    "Design architecture",
                    Duration.ofMinutes(12),
                    null,
                    null))),
        result);
  }
}