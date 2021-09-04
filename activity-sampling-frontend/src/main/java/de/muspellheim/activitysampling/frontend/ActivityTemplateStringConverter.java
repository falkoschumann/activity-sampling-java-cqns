/*
 * Activity Sampling - Frontend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.frontend;

import de.muspellheim.activitysampling.contract.data.ActivityTemplate;
import javafx.util.StringConverter;

public class ActivityTemplateStringConverter extends StringConverter<ActivityTemplate> {

  @Override
  public String toString(ActivityTemplate object) {
    return object.project()
        + " ("
        + object.client()
        + ") "
        + object.task()
        + (object.notes() != null ? " - " + object.notes() : "");
  }

  @Override
  public ActivityTemplate fromString(String string) {
    throw new UnsupportedOperationException();
  }
}
