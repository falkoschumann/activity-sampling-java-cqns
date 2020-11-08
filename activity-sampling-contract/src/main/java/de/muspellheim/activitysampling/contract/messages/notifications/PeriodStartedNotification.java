/*
 * Activity Sampling - Contract
 * Copyright (c) 2020 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.contract.messages.notifications;

import java.time.Duration;
import lombok.NonNull;
import lombok.Value;

@Value
public class PeriodStartedNotification {
  @NonNull Duration period;
}
