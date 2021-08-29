/*
 * Activity Sampling - Backend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.backend.adapters;

import de.muspellheim.activitysampling.contract.messages.notification.ClockTickedNotification;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;
import lombok.Getter;
import lombok.Setter;

public class SystemClock {
  @Getter @Setter Consumer<ClockTickedNotification> onClockTickedNotification;

  public void run() {
    var systemClock = new Timer(true);
    systemClock.schedule(
        new TimerTask() {
          @Override
          public void run() {
            var currentTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
            onClockTickedNotification.accept(new ClockTickedNotification(currentTime));
          }
        },
        200,
        1000);
  }
}
