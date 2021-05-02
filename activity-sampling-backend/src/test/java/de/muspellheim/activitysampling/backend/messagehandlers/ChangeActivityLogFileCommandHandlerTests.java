/*
 * Activity Sampling - Backend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.backend.messagehandlers;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import de.muspellheim.activitysampling.backend.adapters.MemoryEventStore;
import de.muspellheim.activitysampling.backend.adapters.MemorySettingsRepository;
import de.muspellheim.activitysampling.contract.messages.commands.ChangeActivityLogFileCommand;
import de.muspellheim.activitysampling.contract.messages.commands.Success;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;

public class ChangeActivityLogFileCommandHandlerTests {
  @Test
  void changeActivityLogFile() {
    var settingsRepository = new MemorySettingsRepository();
    var eventStore = new MemoryEventStore();
    var commandHandler = new ChangeActivityLogFileCommandHandler(settingsRepository, eventStore);

    var result =
        commandHandler.handle(new ChangeActivityLogFileCommand(Paths.get("/home/activity.log")));

    assertAll(
        () -> assertEquals(new Success(), result, "Command status"),
        () ->
            assertEquals(
                Paths.get("/home/activity.log"),
                settingsRepository.loadActivityLogFile(),
                "Activity log file"));
  }
}
