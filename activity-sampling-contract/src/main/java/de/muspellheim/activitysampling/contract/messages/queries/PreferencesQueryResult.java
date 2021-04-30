/*
 * Activity Sampling - Contract
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.contract.messages.queries;

import java.nio.file.Path;
import java.time.Duration;

public record PreferencesQueryResult(Duration periodDuration, Path activityLogFile) {}
