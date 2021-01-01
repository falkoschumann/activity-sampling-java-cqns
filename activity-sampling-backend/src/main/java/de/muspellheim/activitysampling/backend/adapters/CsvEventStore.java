/*
 * Activity Sampling - Backend
 * Copyright (c) 2020 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.backend.adapters;

import de.muspellheim.activitysampling.backend.Event;
import de.muspellheim.activitysampling.backend.EventStore;
import de.muspellheim.activitysampling.backend.events.ActivityLoggedEvent;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;

public class CsvEventStore implements EventStore {
  @Getter @Setter Consumer<Event> onRecorded;

  private final Path file;

  public CsvEventStore(Path file) {
    this.file = file;
  }

  @Override
  public void record(Event event) throws Exception {
    if (Files.notExists(file)) {
      createFile();
    }

    writeActivity((ActivityLoggedEvent) event);

    if (onRecorded == null) {
      return;
    }

    onRecorded.accept(event);
  }

  private void createFile() throws IOException {
    Files.createDirectories(file.getParent());
    String header = "\"id\",\"timestamp\",\"period\",\"activity\",\"tags\"\n";
    Files.writeString(file, header, StandardCharsets.UTF_8, StandardOpenOption.CREATE_NEW);
  }

  private void writeActivity(ActivityLoggedEvent e) throws IOException {
    var formattedTimestamp =
        LocalDateTime.ofInstant(e.getTimestamp(), ZoneId.systemDefault())
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    var formattedPeriod =
        LocalTime.ofSecondOfDay(e.getPeriod().toSeconds())
            .format(DateTimeFormatter.ofPattern("HH:mm"));
    String record =
        "\""
            + e.getId()
            + "\","
            + formattedTimestamp
            + ","
            + formattedPeriod
            + ",\""
            + e.getActivity()
            + "\","
            + (e.getTags() == null ? "" : "\"" + e.getTags() + "\"")
            + "\n";
    Files.writeString(file, record, StandardCharsets.UTF_8, StandardOpenOption.APPEND);
  }

  @Override
  public List<Event> replay() throws Exception {
    return Files.readAllLines(file).stream()
        .skip(1)
        .map(it -> lineToEvent(it))
        .collect(Collectors.toList());
  }

  private Event lineToEvent(String line) {
    String id = "";
    Instant timestamp = Instant.now();
    Duration period = Duration.ofMinutes(20);
    String activity = "Lorem ipsum";
    String tags = null;
    return new ActivityLoggedEvent(id, timestamp, period, activity, tags);
  }
}
