/*
 * Activity Sampling - Contract
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.contract.messages.queries;

import de.muspellheim.activitysampling.contract.data.WorkingHours;
import java.util.List;
import java.util.SortedSet;

public record WorkingHoursByActivityQueryResult(
    List<WorkingHours> workingHours, @Deprecated SortedSet<String> tags) {}
