/*
 * Activity Sampling - Frontend
 * Copyright (c) 2020 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.frontend;

import java.time.Duration;
import javafx.util.StringConverter;

public class DurationStringConverter extends StringConverter<Duration> {
  @Override
  public String toString(Duration object) {
    return String.format(
        "%1$02d:%2$02d:%3$02d",
        object.toHoursPart(), object.toMinutesPart(), object.toSecondsPart());
  }

  @Override
  public Duration fromString(String string) {
    throw new UnsupportedOperationException("Not implemented yet");
  }
}
