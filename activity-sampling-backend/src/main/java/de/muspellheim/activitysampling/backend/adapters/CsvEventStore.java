/*
 * Activity Sampling - Backend
 * Copyright (c) 2020 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.backend.adapters;

import de.muspellheim.activitysampling.backend.Event;
import de.muspellheim.activitysampling.backend.EventStore;
import de.muspellheim.activitysampling.backend.events.ActivityLoggedEvent;
import java.io.Closeable;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;

public class CsvEventStore implements EventStore {
  private static final CSVFormat CSV_FORMAT = CSVFormat.RFC4180;
  private static final DateTimeFormatter TIMESTAMP_FORMATTER =
      DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
  private static final DateTimeFormatter PERIOD_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

  private enum Headers {
    ID,
    Timestamp,
    Period,
    Activity,
    Tags
  }

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
    publishRecorded(event);
  }

  private void createFile() throws IOException {
    Files.createDirectories(file.getParent());
    try (var out =
        Files.newBufferedWriter(
            file, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
      CSV_FORMAT.withHeader(Headers.class).print(out);
    }
  }

  private void writeActivity(ActivityLoggedEvent e) throws IOException {
    try (var out =
        Files.newBufferedWriter(
            file, StandardCharsets.UTF_8, StandardOpenOption.APPEND, StandardOpenOption.WRITE)) {
      var formattedTimestamp =
          LocalDateTime.ofInstant(e.getTimestamp(), ZoneId.systemDefault())
              .format(TIMESTAMP_FORMATTER);
      var formattedPeriod =
          LocalTime.ofSecondOfDay(e.getPeriod().toSeconds()).format(PERIOD_FORMATTER);
      var printer = new CSVPrinter(out, CSV_FORMAT);
      printer.printRecord(
          e.getId(), formattedTimestamp, formattedPeriod, e.getActivity(), e.getTags());
    }
  }

  private void publishRecorded(Event event) {
    if (onRecorded == null) {
      return;
    }

    onRecorded.accept(event);
  }

  @Override
  public Stream<? extends Event> replay() throws Exception {
    var reader = Files.newBufferedReader(file, StandardCharsets.UTF_8);
    var parser = new CSVParser(reader, CSV_FORMAT.withHeader(Headers.class).withSkipHeaderRecord());
    var iterator = parser.iterator();
    var spliterator =
        Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED | Spliterator.NONNULL);
    return StreamSupport.stream(spliterator, false)
        .map(record -> createEvent(record))
        .onClose(() -> close(reader));
  }

  private ActivityLoggedEvent createEvent(org.apache.commons.csv.CSVRecord record) {
    var id = record.get(Headers.ID);
    var timestamp =
        LocalDateTime.parse(record.get(Headers.Timestamp), TIMESTAMP_FORMATTER)
            .atZone(ZoneId.systemDefault())
            .toInstant();
    var period =
        Duration.ofSeconds(
            LocalTime.parse(record.get(Headers.Period), PERIOD_FORMATTER).toSecondOfDay());
    var activity = record.get(Headers.Activity);
    var tags = record.get(Headers.Tags).isEmpty() ? null : record.get(Headers.Tags);
    return new ActivityLoggedEvent(id, timestamp, period, activity, tags);
  }

  private void close(Closeable closeable) {
    try {
      closeable.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
