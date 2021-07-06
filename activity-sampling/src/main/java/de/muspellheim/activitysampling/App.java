/*
 * Activity Sampling - Application
 * Copyright (c) 2020 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling;

import de.muspellheim.activitysampling.backend.EventStore;
import de.muspellheim.activitysampling.backend.PreferencesStore;
import de.muspellheim.activitysampling.backend.adapters.CsvEventStore;
import de.muspellheim.activitysampling.backend.adapters.MemoryEventStore;
import de.muspellheim.activitysampling.backend.adapters.MemoryPreferencesStore;
import de.muspellheim.activitysampling.backend.adapters.PreferencesPreferencesStore;
import de.muspellheim.activitysampling.backend.messagehandlers.ActivityLogQueryHandler;
import de.muspellheim.activitysampling.backend.messagehandlers.ChangeActivityLogFileCommandHandler;
import de.muspellheim.activitysampling.backend.messagehandlers.ChangePeriodDurationCommandHandler;
import de.muspellheim.activitysampling.backend.messagehandlers.LogActivityCommandHandler;
import de.muspellheim.activitysampling.backend.messagehandlers.PreferencesQueryHandler;
import de.muspellheim.activitysampling.backend.messagehandlers.WorkingHoursByActivityQueryHandler;
import de.muspellheim.activitysampling.backend.messagehandlers.WorkingHoursByNumberQueryHandler;
import de.muspellheim.activitysampling.backend.messagehandlers.WorkingHoursThisWeekQueryHandler;
import de.muspellheim.activitysampling.backend.messagehandlers.WorkingHoursTodayQueryHandler;
import de.muspellheim.activitysampling.contract.messages.queries.ActivityLogQuery;
import de.muspellheim.activitysampling.contract.messages.queries.PreferencesQuery;
import de.muspellheim.activitysampling.frontend.ActivitySamplingController;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {
  private EventStore eventStore;
  private PreferencesStore preferencesStore;

  public static void main(String[] args) {
    Application.launch(args);
  }

  @Override
  public void init() {
    if (getParameters().getUnnamed().contains("--demo")) {
      System.out.println("Run in demo mode...");
      System.setProperty("demoMode", "true");
      eventStore = new MemoryEventStore();
      eventStore.addRecordedObserver(it -> System.out.println("Logged event: " + it));
      preferencesStore = new MemoryPreferencesStore();
    } else {
      preferencesStore = new PreferencesPreferencesStore();
      var activityLogFile = preferencesStore.loadActivityLogFile();
      System.out.println("Save activity log in: " + activityLogFile.toAbsolutePath());
      eventStore = new CsvEventStore(activityLogFile.toString());
    }

    if (getParameters().getNamed().containsKey("activityLogFile")) {
      var activityLogFile = getParameters().getNamed().get("activityLogFile");
      preferencesStore = new PreferencesStoreWrapper(preferencesStore, activityLogFile);
      eventStore.setUri(activityLogFile);
    }
  }

  @Override
  public void start(Stage primaryStage) {
    var logActivityCommandHandler = new LogActivityCommandHandler(eventStore);
    var changePeriodDurationCommandHandler =
        new ChangePeriodDurationCommandHandler(preferencesStore);
    var changeActivityLogFileCommandHandler =
        new ChangeActivityLogFileCommandHandler(preferencesStore, eventStore);
    var activityLogQueryHandler = new ActivityLogQueryHandler(eventStore);
    var preferencesQueryHandler = new PreferencesQueryHandler(preferencesStore);
    var workingHoursTodayQueryHandler = new WorkingHoursTodayQueryHandler(eventStore);
    var workingHoursThisWeekQueryHandler = new WorkingHoursThisWeekQueryHandler(eventStore);
    var workingHoursByActivityQueryHandler = new WorkingHoursByActivityQueryHandler(eventStore);
    var workingHoursByNumberQueryHandler = new WorkingHoursByNumberQueryHandler(eventStore);
    var frontend = ActivitySamplingController.create(primaryStage);

    frontend.setOnLogActivityCommand(
        command -> {
          logActivityCommandHandler.handle(command);
          var result = activityLogQueryHandler.handle(new ActivityLogQuery());
          frontend.display(result);
        });
    frontend.setOnChangePeriodDurationCommand(
        command -> {
          changePeriodDurationCommandHandler.handle(command);
          var result = preferencesQueryHandler.handle(new PreferencesQuery());
          frontend.display(result);
        });
    frontend.setOnChangeActivityLogFileCommand(
        command -> {
          changeActivityLogFileCommandHandler.handle(command);
          var preferencesQueryResult = preferencesQueryHandler.handle(new PreferencesQuery());
          frontend.display(preferencesQueryResult);
          var activityLogQueryResult = activityLogQueryHandler.handle(new ActivityLogQuery());
          frontend.display(activityLogQueryResult);
        });
    frontend.setOnPreferencesQuery(
        query -> {
          var result = preferencesQueryHandler.handle(new PreferencesQuery());
          frontend.display(result);
        });
    frontend.setOnActivityLogQuery(
        query -> {
          var result = activityLogQueryHandler.handle(query);
          frontend.display(result);
        });
    frontend.setOnWorkingHoursTodayQuery(
        query -> {
          var result = workingHoursTodayQueryHandler.handle(query);
          frontend.display(result);
        });
    frontend.setOnWorkingHoursThisWeekQuery(
        query -> {
          var result = workingHoursThisWeekQueryHandler.handle(query);
          frontend.display(result);
        });
    frontend.setOnWorkingHoursByActivityQuery(
        query -> {
          var result = workingHoursByActivityQueryHandler.handle(query);
          frontend.display(result);
        });
    frontend.setOnWorkingHoursByNumberQuery(
        query -> {
          var result = workingHoursByNumberQueryHandler.handle(query);
          frontend.display(result);
        });

    frontend.run();
  }

  private static class PreferencesStoreWrapper implements PreferencesStore {
    private final PreferencesStore preferencesStore;
    private Path activityLogFile;

    public PreferencesStoreWrapper(PreferencesStore preferencesStore, String activityLogFile) {
      this.preferencesStore = preferencesStore;
      this.activityLogFile = Paths.get(activityLogFile);
    }

    @Override
    public Duration loadPeriodDuration() {
      return preferencesStore.loadPeriodDuration();
    }

    @Override
    public void savePeriodDuration(Duration periodDuration) {
      preferencesStore.savePeriodDuration(periodDuration);
    }

    @Override
    public Path loadActivityLogFile() {
      return activityLogFile;
    }

    @Override
    public void saveActivityLogFile(Path activityLogFile) {
      this.activityLogFile = activityLogFile;
    }
  }
}
