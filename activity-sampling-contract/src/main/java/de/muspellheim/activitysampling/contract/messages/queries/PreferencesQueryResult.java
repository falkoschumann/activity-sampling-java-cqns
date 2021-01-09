/*
 * Activity Sampling - Contract
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.contract.messages.queries;

import de.muspellheim.messages.QueryResult;
import java.nio.file.Path;
import java.time.Duration;
import lombok.NonNull;
import lombok.Value;

@Value
public class PreferencesQueryResult implements QueryResult {
  @NonNull Duration periodDuration;
  @NonNull Path activityLogFile;
}
