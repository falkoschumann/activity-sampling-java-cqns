/*
 * Activity Sampling - Contract
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.contract.messages.queries;

import de.muspellheim.activitysampling.contract.data.Activity;
import java.util.List;
import lombok.NonNull;

public record RecentActivitiesQueryResult(@NonNull List<Activity> activities) {}
