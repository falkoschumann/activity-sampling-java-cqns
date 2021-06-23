/*
 * Activity Sampling - Frontend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.frontend;

class ActivitySamplingControllerTests {
  /*
  @Test
  void testDurationToString() {
    assertEquals("00:20:00", durationToString(Duration.ofMinutes(20)));
    assertEquals("01:02:03", durationToString(Duration.ofHours(1).plusMinutes(2).plusSeconds(3)));
  }

  @Test
  void testActivityLogToString() {
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
    var string = activityLogToString(activities);

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
  void testActivityLogToString_emptyList() {
    List<Activity> activities = List.of();

    var string = activityLogToString(activities);

    assertEquals("", string);
  }

  @Nested
  class PeriodCheckTests {
    @Mock private Consumer<Duration> remainingTimeChanged;
    @Mock private Consumer<LocalDateTime> periodEnded;

    private PeriodCheck periodCheck;

    @BeforeEach
    void init() {
      openMocks(this);

      periodCheck = new PeriodCheck();
      periodCheck.setOnRemainingTimeChanged(remainingTimeChanged);
      periodCheck.setOnPeriodEnded(periodEnded);
    }

    @Test
    void testCheck_periodStarted() {
      periodCheck.setPeriod(Duration.ofMinutes(20));

      var currentTime = LocalDateTime.of(2020, 11, 8, 17, 20);
      periodCheck.check(currentTime);

      verify(remainingTimeChanged).accept(Duration.ofMinutes(20));
      verify(periodEnded, never()).accept(any());
    }

    @Test
    void testCheck_periodProgressed() {
      periodCheck.setPeriod(Duration.ofMinutes(20));
      var startTime = LocalDateTime.of(2020, 11, 8, 17, 20);
      periodCheck.check(startTime);

      var currentTime = LocalDateTime.of(2020, 11, 8, 17, 31, 45);
      periodCheck.check(currentTime);

      verify(remainingTimeChanged).accept(Duration.ofMinutes(8).plusSeconds(15));
      verify(periodEnded, never()).accept(any());
    }

    @Test
    void testCheck_periodEnded() {
      periodCheck.setPeriod(Duration.ofMinutes(20));
      var startTime = LocalDateTime.of(2020, 11, 8, 17, 20);
      periodCheck.check(startTime);

      var currentTime = LocalDateTime.of(2020, 11, 8, 17, 40);
      periodCheck.check(currentTime);

      verify(remainingTimeChanged).accept(Duration.ZERO);
      verify(periodEnded).accept(currentTime);
    }
  }
  */
}
