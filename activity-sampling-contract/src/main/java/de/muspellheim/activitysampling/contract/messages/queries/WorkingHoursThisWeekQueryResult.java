/*
 * Activity Sampling - Contract
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.contract.messages.queries;

import de.muspellheim.activitysampling.contract.data.Activity;
import java.time.Duration;
import java.util.List;

public record WorkingHoursThisWeekQueryResult(
    int calendarWeek, Duration totalWorkingHours, List<Activity> activities) {}
