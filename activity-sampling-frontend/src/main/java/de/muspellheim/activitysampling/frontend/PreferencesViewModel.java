/*
 * Activity Sampling - Frontend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.frontend;

import de.muspellheim.activitysampling.contract.MessageHandling;
import de.muspellheim.activitysampling.contract.messages.commands.ChangeActivityLogFileCommand;
import de.muspellheim.activitysampling.contract.messages.commands.ChangePeriodDurationCommand;
import de.muspellheim.activitysampling.contract.messages.queries.PreferencesQuery;
import java.nio.file.Path;
import java.time.Duration;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class PreferencesViewModel {
  private final ObjectProperty<Duration> periodDuration =
      new SimpleObjectProperty<>() {
        @Override
        protected void invalidated() {
          var command = new ChangePeriodDurationCommand(getValue());
          messageHandling.handle(command);
        }
      };
  private final ObjectProperty<Path> activityLogFile = new SimpleObjectProperty<>();

  private final MessageHandling messageHandling;

  public PreferencesViewModel(MessageHandling messageHandling) {
    this.messageHandling = messageHandling;
  }

  public ObjectProperty<Duration> periodDuration() {
    return periodDuration;
  }

  public ObjectProperty<Path> activityLogFile() {
    return activityLogFile;
  }

  public void loadPreferences() {
    var result = messageHandling.handle(new PreferencesQuery());
    periodDuration.setValue(result.getPeriodDuration());
    activityLogFile.setValue(result.getActivityLogFile());
  }

  public void changeActivityLogFile(Path file) {
    var command = new ChangeActivityLogFileCommand(file);
    messageHandling.handle(command);
    activityLogFile.set(file);
  }
}
