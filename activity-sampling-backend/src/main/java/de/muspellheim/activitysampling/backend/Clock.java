/*
 * Activity Sampling - Backend
 * Copyright (c) 2020 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.backend;

import de.muspellheim.activitysampling.contract.messages.notifications.ClockTickedNotification;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;
import lombok.Getter;
import lombok.Setter;

public class Clock {
  private final Timer timer = new Timer(true);

  @Getter @Setter private Consumer<ClockTickedNotification> onTick;

  public void run() {
    timer.schedule(
        new TimerTask() {
          @Override
          public void run() {
            if (onTick == null) return;

            var timestamp = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
            var notification = new ClockTickedNotification(timestamp);
            onTick.accept(notification);
          }
        },
        0,
        1000);
  }
}
