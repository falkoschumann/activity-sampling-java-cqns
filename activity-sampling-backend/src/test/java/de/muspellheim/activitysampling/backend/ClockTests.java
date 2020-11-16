/*
 * Activity Sampling - Backend
 * Copyright (c) 2020 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.backend;

import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;

class ClockTests {
  @Test
  void test() throws InterruptedException {
    var clock = new Clock();
    clock.setOnTick(e -> System.out.println(e));
    clock.run();

    TimeUnit.SECONDS.sleep(3);
  }
}
