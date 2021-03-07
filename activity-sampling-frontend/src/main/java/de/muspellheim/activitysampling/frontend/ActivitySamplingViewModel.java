/*
 * Activity Sampling - Frontend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.frontend;

import de.muspellheim.activitysampling.contract.MessageHandling;
import de.muspellheim.activitysampling.contract.messages.commands.ChangeActivityLogFileCommand;
import de.muspellheim.activitysampling.contract.messages.commands.ChangePeriodDurationCommand;
import de.muspellheim.activitysampling.contract.messages.queries.PreferencesQuery;
import java.nio.file.Paths;
import java.time.Duration;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ActivitySamplingViewModel {
  private final MessageHandling messageHandling;
  private final ObjectProperty<Duration> periodDuration =
      new SimpleObjectProperty<>() {
        @Override
        protected void invalidated() {
          var command = new ChangePeriodDurationCommand(getValue());
          messageHandling.handle(command);
        }
      };
  private final StringProperty activityLogFile =
      new SimpleStringProperty() {
        @Override
        protected void invalidated() {
          var command = new ChangeActivityLogFileCommand(Paths.get(getValue()));
          messageHandling.handle(command);
        }
      };

  public ActivitySamplingViewModel(MessageHandling messageHandling) {
    this.messageHandling = messageHandling;
  }

  public ObjectProperty<Duration> periodDuration() {
    return periodDuration;
  }

  public StringProperty activityLogFile() {
    return activityLogFile;
  }

  public void loadPreferences() {
    var result = messageHandling.handle(new PreferencesQuery());
    periodDuration.setValue(result.getPeriodDuration());
    activityLogFile.setValue(result.getActivityLogFile().toString());
  }
}
