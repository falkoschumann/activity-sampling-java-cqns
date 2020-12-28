/*
 * Activity Sampling - Backend
 * Copyright (c) 2020 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.backend;

import de.muspellheim.activitysampling.backend.events.ActivityLoggedEvent;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Consumer;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

public class CsvEventStore implements EventStore {
  @Getter @Setter @NonNull Consumer<Event> onRecorded = e -> {};

  private final Path file;

  public CsvEventStore(String file) {
    this(Paths.get(file));
  }

  public CsvEventStore(Path file) {
    this.file = file;
  }

  @Override
  public void record(Event event) {
    try {
      if (Files.notExists(file)) {
        Files.createDirectories(file.getParent());
        String header = "id,timestamp,period,activity,tags\n";
        Files.writeString(file, header, StandardCharsets.UTF_8);
      }

      ActivityLoggedEvent e = (ActivityLoggedEvent) event;
      String record =
          e.getId()
              + ","
              + e.getTimestamp()
              + ","
              + e.getPeriod()
              + ","
              + e.getActivity()
              + ","
              + e.getTags()
              + "\n";
      Files.writeString(file, record, StandardCharsets.UTF_8);

      onRecorded.accept(event);
    } catch (IOException e) {
      // TODO Handle exception?
      System.err.println(e.toString());
    }
  }

  @Override
  public List<Event> replay() {
    throw new UnsupportedOperationException("not implemented yet");
  }
}
