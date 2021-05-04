/*
 * Activity Sampling - Frontend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.frontend;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import lombok.Getter;
import lombok.Setter;

class SystemClock {
  @Getter @Setter private Consumer<LocalDateTime> onTick;

  private final Timer timer = new Timer(true);

  void run() {
    timer.schedule(new SystemClockTask(), 0, TimeUnit.SECONDS.toMillis(1));
  }

  private class SystemClockTask extends TimerTask {
    @Override
    public void run() {
      var timestamp = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
      onTick.accept(timestamp);
    }
  }
}
