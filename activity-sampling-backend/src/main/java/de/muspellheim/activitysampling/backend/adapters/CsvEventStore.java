/*
 * Activity Sampling - Backend
 * Copyright (c) 2020 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.backend.adapters;

import de.muspellheim.activitysampling.backend.Event;
import de.muspellheim.activitysampling.backend.events.ActivityLoggedEvent;
import java.io.Closeable;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

@Log
public class CsvEventStore extends AbstractEventStore {
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

  public CsvEventStore(String uri) {
    setUri(uri);
  }

  private Path getFile() {
    return Paths.get(getUri());
  }

  @Override
  @SneakyThrows
  public void record(Event event) {
    if (Files.notExists(getFile())) {
      createFile();
    }
    writeActivity((ActivityLoggedEvent) event);
    notifyRecordedObservers(event);
  }

  private void createFile() throws IOException {
    Files.createDirectories(getFile().getParent());
    try (var out =
        Files.newBufferedWriter(
            getFile(),
            StandardCharsets.UTF_8,
            StandardOpenOption.CREATE,
            StandardOpenOption.WRITE)) {
      CSV_FORMAT.withHeader(Headers.class).print(out);
    }
  }

  private void writeActivity(ActivityLoggedEvent e) throws IOException {
    try (var out =
        Files.newBufferedWriter(
            getFile(),
            StandardCharsets.UTF_8,
            StandardOpenOption.APPEND,
            StandardOpenOption.WRITE)) {
      var formattedTimestamp =
          LocalDateTime.ofInstant(e.timestamp(), ZoneId.systemDefault())
              .format(TIMESTAMP_FORMATTER);
      var formattedPeriod =
          LocalTime.ofSecondOfDay(e.period().toSeconds()).format(PERIOD_FORMATTER);
      var printer = new CSVPrinter(out, CSV_FORMAT);
      String formattedTags = String.join(", ", e.tags());
      printer.printRecord(e.id(), formattedTimestamp, formattedPeriod, e.activity(), formattedTags);
    }
  }

  @Override
  @SneakyThrows
  public Stream<? extends Event> replay() {
    try {
      var reader = Files.newBufferedReader(getFile(), StandardCharsets.UTF_8);
      var parser =
          new CSVParser(reader, CSV_FORMAT.withHeader(Headers.class).withSkipHeaderRecord());
      var iterator = parser.iterator();
      var spliterator =
          Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED | Spliterator.NONNULL);
      return StreamSupport.stream(spliterator, false)
          .map(record -> createEvent(record))
          .onClose(() -> close(reader));
    } catch (NoSuchFileException e) {
      return Stream.empty();
    }
  }

  private ActivityLoggedEvent createEvent(CSVRecord record) {
    var id = record.get(Headers.ID);
    var timestamp =
        LocalDateTime.parse(record.get(Headers.Timestamp), TIMESTAMP_FORMATTER)
            .atZone(ZoneId.systemDefault())
            .toInstant();
    var period =
        Duration.ofSeconds(
            LocalTime.parse(record.get(Headers.Period), PERIOD_FORMATTER).toSecondOfDay());
    var activity = record.get(Headers.Activity);
    var tags =
        List.of(record.get(Headers.Tags).split(",")).stream()
            .map(it -> it.strip())
            .filter(it -> !it.isEmpty())
            .collect(Collectors.toList());
    return new ActivityLoggedEvent(id, timestamp, period, activity, tags);
  }

  @SneakyThrows
  private void close(Closeable closeable) {
    closeable.close();
  }
}
