/*
 * Activity Sampling - Contract
 * Copyright (c) 2020 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.contract.messages.commands;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import lombok.NonNull;

public record LogActivityCommand(
    @NonNull LocalDateTime timestamp,
    @NonNull Duration period,
    @NonNull String activity,
    @NonNull List<String> tags) {}
