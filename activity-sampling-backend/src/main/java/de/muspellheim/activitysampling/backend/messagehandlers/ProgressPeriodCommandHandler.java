/*
 * Activity Sampling - Backend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.backend.messagehandlers;

import de.muspellheim.activitysampling.backend.PreferencesRepository;
import de.muspellheim.activitysampling.contract.messages.commands.CommandStatus;
import de.muspellheim.activitysampling.contract.messages.commands.ProgressPeriodCommand;
import de.muspellheim.activitysampling.contract.messages.commands.Success;
import de.muspellheim.activitysampling.contract.messages.notification.PeriodEndedNotification;
import de.muspellheim.activitysampling.contract.messages.notification.PeriodProgressedNotification;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.function.Consumer;
import lombok.Getter;
import lombok.Setter;

public class ProgressPeriodCommandHandler {
  @Getter @Setter Consumer<PeriodProgressedNotification> onPeriodProgressedNotification;
  @Getter @Setter Consumer<PeriodEndedNotification> onPeriodEndedNotification;

  private Duration period;
  private LocalDateTime start;
  private LocalDateTime end;

  public ProgressPeriodCommandHandler(PreferencesRepository preferencesRepository) {
    updatePeriod(preferencesRepository.getPeriod());
    preferencesRepository.addPeriodChangedObserver(this::updatePeriod);
  }

  private void updatePeriod(Duration period) {
    this.period = period;
    start = null;
  }

  public CommandStatus handle(ProgressPeriodCommand command) {
    if (start == null) {
      if (end == null) {
        start = command.currentTime();
        onPeriodProgressedNotification.accept(
            new PeriodProgressedNotification(LocalTime.ofSecondOfDay(period.getSeconds()), 0.0));
        return new Success();
      } else {
        start = end;
      }
    }

    var elapsed = Duration.between(start, command.currentTime());
    var remaining = period.minus(elapsed);
    if (remaining.toSeconds() <= 0) {
      start = null;
      end = command.currentTime();
      onPeriodEndedNotification.accept(new PeriodEndedNotification(command.currentTime()));
    } else {
      var remainingSeconds = (double) remaining.toSeconds();
      var totalSeconds = (double) period.getSeconds();
      var progress = totalSeconds == 0 ? 0.0 : 1 - remainingSeconds / totalSeconds;
      onPeriodProgressedNotification.accept(
          new PeriodProgressedNotification(
              LocalTime.ofSecondOfDay(remaining.getSeconds()), progress));
    }
    return new Success();
  }
}
