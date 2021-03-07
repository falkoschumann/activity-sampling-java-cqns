/*
 * Activity Sampling - Frontend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.frontend;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.URL;
import java.util.Properties;
import org.junit.jupiter.api.Test;

class InfoViewModelTests {
  @Test
  void initProperties() throws Exception {
    var props = new Properties();
    props.setProperty("title", "abc");
    props.setProperty("version", "def");
    props.setProperty("copyright", "ghj");
    var viewModel = new InfoViewModel(new URL("file:/app.png"), props);

    assertAll(
        () -> assertEquals("abc", viewModel.titleProperty().get()),
        () -> assertEquals("def", viewModel.versionProperty().get()),
        () -> assertEquals("ghj", viewModel.copyrightProperty().get()));
  }
}
