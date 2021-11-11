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
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class CsvEventStoreTest {
  private static final Path OUT_FILE = Paths.get("build/event-store/event-stream.csv");
  private static final Path SOLL_FILE = Paths.get("src/test/resources/event-stream.csv");

  @BeforeAll
  static void initAll() throws Exception {
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
  void replay_withEventType() {
    var eventStore = new CsvEventStore(SOLL_FILE);

    var events = eventStore.replay(ActivityLoggedEvent.class);

    assertEquals(createEvents(), events.collect(Collectors.toList()));
  }

  @Test
  void replay_withEventTypes() {
    var eventStore = new CsvEventStore(SOLL_FILE);

    var events = eventStore.replay(List.of(ActivityLoggedEvent.class));

    assertEquals(createEvents(), events.collect(Collectors.toList()));
  }

  private static List<ActivityLoggedEvent> createEvents() {
    return List.of(
        new ActivityLoggedEvent(
            "a7caf1b0-886e-406f-8fbc-71da9f34714e",
            LocalDateTime.of(2020, 12, 30, 17, 52).atZone(ZoneId.systemDefault()).toInstant(),
            Duration.ofMinutes(20),
            "ACME Ltd.",
            "TodoMVC",
            "Learning",
            "Taste JavaScript"),
        new ActivityLoggedEvent(
            "d5abc0dd-60b0-4a3b-9b2f-8b02005fb256",
            LocalDateTime.of(2020, 12, 30, 21, 20).atZone(ZoneId.systemDefault()).toInstant(),
            Duration.ofMinutes(20),
            "ACME Ltd.",
            "TodoMVC",
            "Design",
            "Create a TodoMVC template"));
  }
}
