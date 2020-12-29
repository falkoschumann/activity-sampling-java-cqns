/*
 * Activity Sampling - Backend
 * Copyright (c) 2020 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.backend.messagehandlers;

import de.muspellheim.activitysampling.contract.messages.notifications.ClockTickedNotification;
import de.muspellheim.activitysampling.contract.messages.notifications.PeriodEndedNotification;
import de.muspellheim.activitysampling.contract.messages.notifications.PeriodProgressedNotification;
import de.muspellheim.activitysampling.contract.messages.notifications.PeriodStartedNotification;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.function.Consumer;
import lombok.Getter;
import lombok.Setter;

public class ClockTickedNotificationHandler {
  @Getter @Setter private Duration period = Duration.ofMinutes(20);
  @Getter @Setter private Consumer<PeriodStartedNotification> onPeriodStartedNotification;
  @Getter @Setter private Consumer<PeriodProgressedNotification> onPeriodProgressedNotification;
  @Getter @Setter private Consumer<PeriodEndedNotification> onPeriodEndedNotification;

  private LocalDateTime start;

  public ClockTickedNotificationHandler() {}

  public ClockTickedNotificationHandler(Duration period) {
    setPeriod(period);
  }

  public void handle(ClockTickedNotification notification) {
    if (start == null) {
      start = notification.getTimestamp();
      onPeriodStartedNotification.accept(new PeriodStartedNotification(period));
      return;
    }

    var elapsedTime = Duration.between(start, notification.getTimestamp());
    var remainingTime = period.minus(elapsedTime);
    if (remainingTime.isZero() || remainingTime.isNegative()) {
      onPeriodEndedNotification.accept(new PeriodEndedNotification(period));
      start = null;
    } else {
      onPeriodProgressedNotification.accept(
          new PeriodProgressedNotification(period, elapsedTime, remainingTime));
    }
  }
}
