/*
 * Activity Sampling - Contract
 * Copyright (c) 2020 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.contract.messages.notifications;

import de.muspellheim.messages.Notification;
import java.time.LocalDateTime;
import lombok.NonNull;
import lombok.Value;

@Value
public class ClockTickedNotification implements Notification {
  @NonNull LocalDateTime timestamp;
}
