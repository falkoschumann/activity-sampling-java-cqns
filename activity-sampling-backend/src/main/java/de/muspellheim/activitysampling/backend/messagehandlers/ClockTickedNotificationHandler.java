/*
 * Activity Sampling - Backend
 * Copyright (c) 2020 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.backend.messagehandlers;

import de.muspellheim.activitysampling.contract.messages.notifications.ClockTickedNotification;
import de.muspellheim.activitysampling.contract.messages.notifications.PeriodEndedNotification;
import de.muspellheim.activitysampling.contract.messages.notifications.PeriodProgressedNotification;
import de.muspellheim.activitysampling.contract.messages.notifications.PeriodStartedNotification;
import de.muspellheim.messages.NotificationHandling;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.function.Consumer;
import lombok.Getter;
import lombok.Setter;

public class ClockTickedNotificationHandler
    implements NotificationHandling<ClockTickedNotification> {
  @Getter @Setter private Duration period;
  @Getter @Setter private Consumer<PeriodStartedNotification> onPeriodStartedNotification;
  @Getter @Setter private Consumer<PeriodProgressedNotification> onPeriodProgressedNotification;
  @Getter @Setter private Consumer<PeriodEndedNotification> onPeriodEndedNotification;

  private LocalDateTime start;

  public ClockTickedNotificationHandler() {
    this(Duration.ofMinutes(20));
  }

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
    if (remainingTime.toSeconds() <= 0) {
      onPeriodEndedNotification.accept(new PeriodEndedNotification(period));
      start = null;
    } else {
      onPeriodProgressedNotification.accept(
          new PeriodProgressedNotification(period, elapsedTime, remainingTime));
    }
  }
}
