/*
 * Activity Sampling - Contract
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.contract.messages.queries;

import de.muspellheim.activitysampling.contract.data.Activity;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.SortedSet;

public record WorkingHoursTodayQueryResult(
    LocalDate date,
    Duration totalWorkingHours,
    List<Activity> activities,
    SortedSet<String> tags) {}
