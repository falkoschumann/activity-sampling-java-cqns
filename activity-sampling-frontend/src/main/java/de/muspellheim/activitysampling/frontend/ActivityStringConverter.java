/*
 * Activity Sampling - Frontend
 * Copyright (c) 2020 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.frontend;

import de.muspellheim.activitysampling.contract.data.Activity;
import javafx.util.StringConverter;

class ActivityStringConverter extends StringConverter<Activity> {
  @Override
  public String toString(Activity object) {
    String string = object.getActivity();
    if (object.getTags() != null) {
      string = "[" + String.join(", ", object.getTags()) + "] " + string;
    }
    return string;
  }

  @Override
  public Activity fromString(String string) {
    throw new UnsupportedOperationException("Not implemented yet");
  }
}
