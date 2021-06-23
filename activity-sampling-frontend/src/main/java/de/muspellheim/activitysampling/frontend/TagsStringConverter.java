/*
 * Activity Sampling - Frontend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.frontend;

import java.util.List;
import java.util.Optional;
import javafx.util.StringConverter;

class TagsStringConverter extends StringConverter<List<String>> {

  @Override
  public String toString(List<String> object) {
    return String.join(", ", (Optional.ofNullable(object).orElse(List.of())));
  }

  @Override
  public List<String> fromString(String string) {
    return List.of(Optional.ofNullable(string).orElse("").split(",")).stream()
        .filter(it -> !it.isBlank())
        .map(String::strip)
        .toList();
  }
}
