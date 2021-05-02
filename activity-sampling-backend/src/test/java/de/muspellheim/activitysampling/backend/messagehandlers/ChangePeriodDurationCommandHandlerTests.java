/*
 * Activity Sampling - Backend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.backend.messagehandlers;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import de.muspellheim.activitysampling.backend.adapters.MemorySettingsRepository;
import de.muspellheim.activitysampling.contract.messages.commands.ChangePeriodDurationCommand;
import de.muspellheim.activitysampling.contract.messages.commands.Success;
import java.time.Duration;
import org.junit.jupiter.api.Test;

public class ChangePeriodDurationCommandHandlerTests {
  @Test
  void changePeriodDuration() {
    var settingsRepository = new MemorySettingsRepository();
    var commandHandler = new ChangePeriodDurationCommandHandler(settingsRepository);

    var result = commandHandler.handle(new ChangePeriodDurationCommand(Duration.ofMinutes(30)));

    assertAll(
        () -> assertEquals(new Success(), result, "Command status"),
        () ->
            assertEquals(
                Duration.ofMinutes(30),
                settingsRepository.loadPeriodDuration(),
                "Period duration"));
  }
}
