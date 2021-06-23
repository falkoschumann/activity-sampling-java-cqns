/*
 * Activity Sampling - Frontend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.frontend;

import java.util.List;
import java.util.regex.Pattern;
import javafx.util.StringConverter;

class ActivityTemplateStringConverter extends StringConverter<ActivityTemplate> {

  @Override
  public String toString(ActivityTemplate object) {
    String string = object.activity();
    if (!object.tags().isEmpty()) {
      var tagsStringConverter = new TagsStringConverter();
      string = "[" + tagsStringConverter.toString(object.tags()) + "] " + string;
    }
    return string;
  }

  @Override
  public ActivityTemplate fromString(String string) {
    var pattern = Pattern.compile("(\\[(.+)])\\s+(.+)");
    var matcher = pattern.matcher(string);
    if (matcher.find()) {
      var tagsStringConverter = new TagsStringConverter();
      return new ActivityTemplate(
          matcher.group(3).strip(), tagsStringConverter.fromString(matcher.group(2)));
    } else {
      return new ActivityTemplate(string.strip(), List.of());
    }
  }
}
