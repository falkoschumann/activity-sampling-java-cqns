/*
 * Activity Sampling - Backend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.backend.messagehandlers;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import de.muspellheim.activitysampling.backend.adapters.MemoryEventStore;
import de.muspellheim.activitysampling.backend.adapters.MemoryPreferencesStore;
import de.muspellheim.activitysampling.contract.messages.commands.ChangeActivityLogFileCommand;
import de.muspellheim.activitysampling.contract.messages.commands.Success;
import org.junit.jupiter.api.Test;

public class ChangeActivityLogFileCommandHandlerTests {
  @Test
  void testHandle() {
    var preferencesStore = new MemoryPreferencesStore();
    var eventStore = new MemoryEventStore();
    var handler = new ChangeActivityLogFileCommandHandler(preferencesStore, eventStore);

    var result = handler.handle(new ChangeActivityLogFileCommand("/home/alice/activity.log"));

    assertAll(
        () -> assertEquals(new Success(), result, "Command status"),
        () ->
            assertEquals(
                "/home/alice/activity.log",
                preferencesStore.loadActivityLogFile(),
                "Activity log file"));
  }
}
