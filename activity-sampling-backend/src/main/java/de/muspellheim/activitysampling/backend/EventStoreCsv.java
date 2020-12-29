/*
 * Activity Sampling - Backend
 * Copyright (c) 2020 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.backend;

import de.muspellheim.activitysampling.backend.events.ActivityLoggedEvent;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;
import lombok.Getter;
import lombok.Setter;

public class EventStoreCsv implements EventStore {
  @Getter @Setter Consumer<Event> onRecorded;

  private final Path file;

  public EventStoreCsv(Path file) {
    this.file = file;
  }

  @Override
  public void record(Event event) throws Exception {
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

    if (onRecorded == null) {
      return;
    }

    onRecorded.accept(event);
  }

  @Override
  public List<Event> replay() throws Exception {
    throw new UnsupportedOperationException("not implemented yet");
  }
}
