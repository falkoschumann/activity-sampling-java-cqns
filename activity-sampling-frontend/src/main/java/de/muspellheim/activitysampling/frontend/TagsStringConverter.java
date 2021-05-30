/*
 * Activity Sampling - Frontend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.frontend;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javafx.util.StringConverter;

public class TagsStringConverter extends StringConverter<List<String>> {
  @Override
  public String toString(List<String> object) {
    return String.join(", ", object);
  }

  @Override
  public List<String> fromString(String string) {
    if (string.isBlank()) {
      return Collections.emptyList();
    }

    return List.of(string.split(",")).stream().map(String::strip).collect(Collectors.toList());
  }
}
