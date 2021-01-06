package de.muspellheim.activitysampling.frontend;

import de.muspellheim.activitysampling.contract.data.Activity;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;

public class ActivityStringConverterTests {
  @Test
  void activityToString() {}

  @Test
  void activityFromString() {}

  private static Activity createA() {
    return new Activity(
        "d36a20db-56ae-48af-9221-0630911cdb8d",
        LocalDateTime.of(2021, 1, 4, 14, 20),
        Duration.ofMinutes(20),
        "A",
        List.of("Foo", "Bar"));
  }

  private static Activity createB() {
    return new Activity(
        "e9ed7915-8109-402d-b9e6-2d5764ef688d",
        LocalDateTime.of(2021, 1, 4, 13, 52),
        Duration.ofMinutes(20),
        "B");
  }
}
