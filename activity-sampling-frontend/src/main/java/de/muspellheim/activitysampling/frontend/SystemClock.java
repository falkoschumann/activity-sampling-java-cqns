/*
 * Activity Sampling - Frontend
 * Copyright (c) 2020 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.frontend;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;
import lombok.Getter;
import lombok.Setter;

class SystemClock {
  private final Timer timer = new Timer(true);

  @Getter @Setter private Consumer<LocalDateTime> onTick;

  void run() {
    timer.schedule(new SystemClockTask(), 0, 1000);
  }

  private class SystemClockTask extends TimerTask {
    @Override
    public void run() {
      if (onTick == null) {
        return;
      }

      var timestamp = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
      onTick.accept(timestamp);
    }
  }
}
