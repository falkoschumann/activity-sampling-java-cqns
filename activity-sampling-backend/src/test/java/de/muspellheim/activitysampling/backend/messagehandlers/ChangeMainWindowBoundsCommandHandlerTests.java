/*
 * Activity Sampling - Backend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.backend.messagehandlers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import de.muspellheim.activitysampling.backend.adapters.MemoryPreferencesRepository;
import de.muspellheim.activitysampling.contract.data.Bounds;
import de.muspellheim.activitysampling.contract.messages.commands.ChangeMainWindowBoundsCommand;
import de.muspellheim.activitysampling.contract.messages.commands.Success;
import org.junit.jupiter.api.Test;

public class ChangeMainWindowBoundsCommandHandlerTests {
  @Test
  void testHandle() {
    var repository = new MemoryPreferencesRepository();
    repository.addExamples();
    var handler = new ChangeMainWindowBoundsCommandHandler(repository);

    var status = handler.handle(new ChangeMainWindowBoundsCommand(new Bounds(1, 2, 3, 4)));

    assertEquals(new Success(), status, "command status");
    assertEquals(new Bounds(1, 2, 3, 4), repository.loadMainWindowBounds(), "main window bounds");
  }
}
