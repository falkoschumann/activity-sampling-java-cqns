package de.muspellheim.activitysampling.contract.messages.notifications;

import java.time.LocalDateTime;
import lombok.NonNull;
import lombok.Value;

@Value
public class ClockTickedNotification {
  @NonNull LocalDateTime timestamp;
}
