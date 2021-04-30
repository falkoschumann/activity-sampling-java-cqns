/*
 * Activity Sampling - Contract
 * Copyright (c) 2020 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.contract.messages.commands;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public record LogActivityCommand(
    LocalDateTime timestamp, Duration period, String activity, List<String> tags) {}
