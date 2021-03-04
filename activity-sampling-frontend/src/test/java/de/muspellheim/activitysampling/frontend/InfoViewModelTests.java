/*
 * Activity Sampling - Frontend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.frontend;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Properties;
import org.junit.jupiter.api.Test;

class InfoViewModelTests {
  @Test
  void initProperties() {
    var viewModel = new InfoViewModel();
    var props = new Properties();
    props.setProperty("title", "abc");
    props.setProperty("version", "def");
    props.setProperty("copyright", "ghj");

    viewModel.initProperties(props);

    assertAll(
        () -> assertEquals("abc", viewModel.titleProperty().get()),
        () -> assertEquals("def", viewModel.versionProperty().get()),
        () -> assertEquals("ghj", viewModel.copyrightProperty().get()));
  }
}
