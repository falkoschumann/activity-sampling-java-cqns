package de.muspellheim.activitysampling.frontend;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Duration;
import org.junit.jupiter.api.Test;

public class DurationStringConverterTests {
  @Test
  void durationToString() {
    var converter = new DurationStringConverter();

    assertEquals("00:20:00", converter.toString(Duration.ofMinutes(20)));
    assertEquals("01:02:03", converter.toString(Duration.ofHours(1).plusMinutes(2).plusSeconds(3)));
  }

  @Test
  void durationFromString() {
    var converter = new DurationStringConverter();

    assertThrows(UnsupportedOperationException.class, () -> converter.fromString("00:20:00"));
  }
}
