/*
 * Activity Sampling - Contract
 * Copyright (c) 2020 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.contract.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Duration;
import org.junit.jupiter.api.Test;

class DurationStringConverterTests {
  @Test
  void durationToString() {
    var duration = Duration.ofHours(4).plusMinutes(7).plusSeconds(11);
    var converter = new DurationStringConverter();

    var string = converter.toString(duration);

    assertEquals("04:07:11", string);
  }
}
