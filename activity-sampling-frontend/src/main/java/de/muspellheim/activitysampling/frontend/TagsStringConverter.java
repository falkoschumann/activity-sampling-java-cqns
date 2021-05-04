/*
 * Activity Sampling - Frontend
 * Copyright (c) 2020 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.frontend;

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
    return List.of(string.split(",")).stream().map(it -> it.strip()).collect(Collectors.toList());
  }
}
