/*
 * Activity Sampling - Contract
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.contract.messages.queries;

import de.muspellheim.activitysampling.contract.data.WorkingHours;
import java.util.List;

public record WorkingHoursByActivityQueryResult(List<WorkingHours> workingHours) {}
