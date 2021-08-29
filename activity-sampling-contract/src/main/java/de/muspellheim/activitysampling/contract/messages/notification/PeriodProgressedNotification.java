/*
 * Activity Sampling - Contract
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.contract.messages.notification;

import java.time.LocalDateTime;
import java.time.LocalTime;

public record PeriodProgressedNotification(
    LocalTime remaining, double progress, LocalDateTime end) {}
