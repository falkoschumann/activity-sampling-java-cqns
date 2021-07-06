/*
 * Activity Sampling - Backend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.backend.messagehandlers;

import de.muspellheim.activitysampling.backend.events.ActivityLoggedEvent;
import de.muspellheim.activitysampling.contract.data.Activity;
import java.time.LocalDateTime;
import java.time.ZoneId;

class ActivityFactory {
  private ActivityFactory() {}

  static Activity create(ActivityLoggedEvent event) {
    return new Activity(
        event.id(),
        LocalDateTime.ofInstant(event.timestamp(), ZoneId.systemDefault()),
        event.period(),
        event.activity(),
        event.tags());
  }
}
