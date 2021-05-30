/*
 * Activity Sampling - Frontend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.frontend;

import static org.junit.jupiter.api.Assertions.assertEquals;

import de.muspellheim.activitysampling.contract.data.Activity;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import org.junit.jupiter.api.Test;

public class ActivityLogRendererTests {
  @Test
  void renderActivities() {
    Locale.setDefault(Locale.GERMANY);
    var activityA =
        new Activity(
            "a7caf1b0-886e-406f-8fbc-71da9f34714e",
            LocalDateTime.of(2020, 12, 30, 17, 52),
            Duration.ofMinutes(20),
            "Activity A",
            List.of("Foo", "Bar"));
    var activityB =
        new Activity(
            "d5abc0dd-60b0-4a3b-9b2f-8b02005fb256",
            LocalDateTime.of(2020, 12, 30, 21, 20),
            Duration.ofMinutes(20),
            "Activity B");
    var activityC =
        new Activity(
            "e9ed7915-8109-402d-b9e6-2d5764ef688d",
            LocalDateTime.of(2021, 1, 4, 13, 52),
            Duration.ofMinutes(20),
            "Activity C");
    var activities = List.of(activityA, activityB, activityC);
    var renderer = new ActivityLogRenderer();

    var string = renderer.render(activities);

    var lines =
        """
      Mittwoch, 30. Dezember 2020
      17:52 - [Foo, Bar] Activity A
      21:20 - Activity B
      Montag, 4. Januar 2021
      13:52 - Activity C
      """;
    assertEquals(lines, string);
  }

  @Test
  void renderActivities_EmptyList() {
    List<Activity> activities = List.of();
    var renderer = new ActivityLogRenderer();

    var string = renderer.render(activities);

    assertEquals("", string);
  }
}
