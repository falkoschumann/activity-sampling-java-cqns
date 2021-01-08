/*
 * Activity Sampling - Backend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.backend.messagehandlers;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import de.muspellheim.activitysampling.backend.adapters.MemoryPreferencesStore;
import de.muspellheim.activitysampling.contract.messages.commands.ChangeActivityLogFileCommand;
import de.muspellheim.messages.Success;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;

public class ChangeActivityLogFileCommandHandlerTests {
  @Test
  void changeActivityLogFile() {
    var store = new MemoryPreferencesStore();
    var handler = new ChangeActivityLogFileCommandHandler(store);

    var result = handler.handle(new ChangeActivityLogFileCommand(Paths.get("/home/activity.log")));

    assertAll(
        () -> assertEquals(new Success(), result, "Command status"),
        () ->
            assertEquals(
                Paths.get("/home/activity.log"), store.loadActivityLogFile(), "Activity log file"));
  }
}
