/*
 * Activity Sampling - Backend
 * Copyright (c) 2020 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.backend;

import de.muspellheim.activitysampling.contract.messages.notifications.ClockTickedNotification;
import de.muspellheim.activitysampling.contract.messages.notifications.PeriodEndedNotification;
import de.muspellheim.activitysampling.contract.messages.notifications.PeriodProgressedNotification;
import de.muspellheim.activitysampling.contract.messages.notifications.PeriodStartedNotification;
import java.time.Duration;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;
import lombok.Getter;
import lombok.Setter;

public class PeriodCheck {
  @Getter @Setter private Consumer<PeriodStartedNotification> onPeriodStartedNotification;
  @Getter @Setter private Consumer<PeriodProgressedNotification> onPeriodProgressedNotification;
  @Getter @Setter private Consumer<PeriodEndedNotification> onPeriodEndedNotification;

  public void handle(ClockTickedNotification notification) {
    onPeriodStartedNotification.accept(
        new PeriodStartedNotification(Duration.ofMinutes(20)));
    onPeriodProgressedNotification.accept(
        new PeriodProgressedNotification(Duration.ofMinutes(8).plusSeconds(15)));
    onPeriodEndedNotification.accept(new PeriodEndedNotification());
  }
}
