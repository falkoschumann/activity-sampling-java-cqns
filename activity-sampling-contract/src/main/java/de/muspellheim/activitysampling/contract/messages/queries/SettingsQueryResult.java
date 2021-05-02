/*
 * Activity Sampling - Contract
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.contract.messages.queries;

import java.nio.file.Path;
import java.time.Duration;
import lombok.NonNull;

public record SettingsQueryResult(
    @NonNull Duration periodDuration, @NonNull Path activityLogFile) {}
