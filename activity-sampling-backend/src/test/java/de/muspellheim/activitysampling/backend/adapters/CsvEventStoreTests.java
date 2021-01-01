/*
 * Activity Sampling - Backend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.backend.adapters;

import static org.junit.jupiter.api.Assertions.assertEquals;

import de.muspellheim.activitysampling.backend.events.ActivityLoggedEvent;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class CsvEventStoreTests {
  private static final Path OUT_FILE = Paths.get("build/tests/activity-log.csv");
  private static final Path SOLL_FILE = Paths.get("src/test/resources/activity-log.csv");

  @BeforeAll
  static void setUpBeforeAll() throws Exception {
    Files.deleteIfExists(OUT_FILE);
    Files.createDirectories(OUT_FILE.getParent());
  }

  @Test
  void record() throws Exception {
    var eventStore = new CsvEventStore(OUT_FILE);
    var events = createEvents();

    eventStore.record(events.get(0));
    eventStore.record(events.get(1));

    assertEquals(Files.readAllLines(SOLL_FILE), Files.readAllLines(OUT_FILE));
  }

  @Test
  @Disabled("Not implemented yet")
  void replay() throws Exception {
    var eventStore = new CsvEventStore(SOLL_FILE);

    var events = eventStore.replay();

    assertEquals(createEvents(), events);
  }

  private static List<ActivityLoggedEvent> createEvents() {
    return List.of(
        new ActivityLoggedEvent(
            "a7caf1b0-886e-406f-8fbc-71da9f34714e",
            LocalDateTime.of(2020, 12, 30, 17, 52).atZone(ZoneId.systemDefault()).toInstant(),
            Duration.ofMinutes(20),
            "Taste JavaScript",
            null),
        new ActivityLoggedEvent(
            "d5abc0dd-60b0-4a3b-9b2f-8b02005fb256",
            LocalDateTime.of(2020, 12, 30, 21, 20).atZone(ZoneId.systemDefault()).toInstant(),
            Duration.ofMinutes(20),
            "Create a TodoMVC template",
            "TodoMVC"));
  }
}