/*
 * Activity Sampling - Backend
 * Copyright (c) 2020 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.backend.adapters;

import de.muspellheim.activitysampling.backend.Event;
import de.muspellheim.activitysampling.backend.events.ActivityLoggedEvent;
import java.io.Closeable;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVFormat.Builder;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

public class CsvEventStore extends AbstractEventStore {
  private static final CSVFormat CSV_FORMAT =
      CSVFormat.Builder.create(CSVFormat.RFC4180).setNullString("").build();
  private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ISO_DATE_TIME;

  private enum Headers {
    Timestamp,
    Period,
    Client,
    Project,
    Task,
    Notes
  }

  private final Path file;

  public CsvEventStore() {
    this(Paths.get(System.getProperty("user.home"), "activity-sampling.csv"));
  }

  public CsvEventStore(Path file) {
    this.file = file;
  }

  @Override
  public void record(Event event) {
    if (Files.notExists(file)) {
      createFile();
    }
    writeActivity((ActivityLoggedEvent) event);
    notifyRecordedObservers(event);
  }

  private void createFile() {
    try {
      Files.createDirectories(file.getParent());
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }

    try (var out =
        Files.newBufferedWriter(
            file, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
      var format = Builder.create(CSV_FORMAT).setHeader(Headers.class).build();
      format.print(out);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private void writeActivity(ActivityLoggedEvent event) {
    try (var out =
        Files.newBufferedWriter(
            file, StandardCharsets.UTF_8, StandardOpenOption.APPEND, StandardOpenOption.WRITE)) {
      var formattedTimestamp =
          LocalDateTime.ofInstant(event.timestamp(), ZoneId.systemDefault())
              .format(TIMESTAMP_FORMATTER);
      var printer = new CSVPrinter(out, CSV_FORMAT);
      printer.printRecord(
          formattedTimestamp,
          event.period().toSeconds(),
          event.client(),
          event.project(),
          event.task(),
          event.notes());
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  @Override
  public Stream<? extends Event> replay() {
    try {
      var reader = Files.newBufferedReader(file, StandardCharsets.UTF_8);
      var format =
          Builder.create(CSV_FORMAT).setHeader(Headers.class).setSkipHeaderRecord(true).build();
      var parser = new CSVParser(reader, format);
      var iterator = parser.iterator();
      var spliterator =
          Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED | Spliterator.NONNULL);
      return StreamSupport.stream(spliterator, false)
          .map(this::createEvent)
          .onClose(() -> closeUnchecked(reader));
    } catch (NoSuchFileException e) {
      return Stream.empty();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private void closeUnchecked(Closeable closeable) {
    try {
      closeable.close();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private ActivityLoggedEvent createEvent(CSVRecord record) {
    var timestamp =
        LocalDateTime.parse(record.get(Headers.Timestamp), TIMESTAMP_FORMATTER)
            .atZone(ZoneId.systemDefault())
            .toInstant();
    var period = Duration.ofSeconds(Long.parseLong(record.get(Headers.Period)));
    var client = record.get(Headers.Client);
    var project = record.get(Headers.Project);
    var task = record.get(Headers.Task);
    var notes = record.get(Headers.Notes);
    return new ActivityLoggedEvent(timestamp, period, client, project, task, notes);
  }
}
