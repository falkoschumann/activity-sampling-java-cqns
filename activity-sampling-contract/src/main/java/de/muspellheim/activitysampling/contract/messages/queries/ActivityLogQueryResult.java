/*
 * Activity Sampling - Contract
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.contract.messages.queries;

import de.muspellheim.activitysampling.contract.data.Activity;
import de.muspellheim.messages.QueryResult;
import java.util.List;
import lombok.NonNull;
import lombok.Value;

@Value
public class ActivityLogQueryResult implements QueryResult {
  @NonNull List<Activity> log;
}
