/*
 * Activity Sampling - Contract
 * Copyright (c) 2020 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.contract.util;

import java.time.Duration;

public class DurationStringConverter {
  public String toString(Duration object) {
    return String.format(
        "%1$02d:%2$02d:%3$02d",
        object.toHoursPart(), object.toMinutesPart(), object.toSecondsPart());
  }
}
