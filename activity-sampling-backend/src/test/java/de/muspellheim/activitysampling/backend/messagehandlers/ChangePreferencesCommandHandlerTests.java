/*
 * Activity Sampling - Backend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.backend.messagehandlers;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import de.muspellheim.activitysampling.backend.adapters.MemoryPreferencesRepository;
import de.muspellheim.activitysampling.contract.messages.commands.ChangePreferencesCommand;
import de.muspellheim.activitysampling.contract.messages.commands.Success;
import java.time.Duration;
import org.junit.jupiter.api.Test;

class ChangePreferencesCommandHandlerTests {
  @Test
  void handle() {
    var repository = new MemoryPreferencesRepository();
    var handler = new ChangePreferencesCommandHandler(repository);

    var result = handler.handle(new ChangePreferencesCommand(Duration.ofMinutes(30)));

    assertAll(
        () -> assertEquals(new Success(), result, "Command status"),
        () ->
            assertEquals(
                Duration.ofMinutes(30), repository.loadPeriodDuration(), "Period duration"));
  }
}
