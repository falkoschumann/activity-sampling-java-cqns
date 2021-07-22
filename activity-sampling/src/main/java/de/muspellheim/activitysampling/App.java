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
import de.muspellheim.activitysampling.backend.adapters.PrefsPreferencesStore;
import de.muspellheim.activitysampling.backend.messagehandlers.ActivityLogQueryHandler;
import de.muspellheim.activitysampling.backend.messagehandlers.ChangeActivityLogFileCommandHandler;
import de.muspellheim.activitysampling.backend.messagehandlers.ChangePeriodDurationCommandHandler;
import de.muspellheim.activitysampling.backend.messagehandlers.LogActivityCommandHandler;
import de.muspellheim.activitysampling.backend.messagehandlers.PreferencesQueryHandler;
import de.muspellheim.activitysampling.backend.messagehandlers.WorkingHoursByActivityQueryHandler;
import de.muspellheim.activitysampling.backend.messagehandlers.WorkingHoursByNumberQueryHandler;
import de.muspellheim.activitysampling.backend.messagehandlers.WorkingHoursThisWeekQueryHandler;
import de.muspellheim.activitysampling.backend.messagehandlers.WorkingHoursTodayQueryHandler;
import de.muspellheim.activitysampling.contract.messages.commands.Failure;
import de.muspellheim.activitysampling.contract.messages.queries.ActivityLogQuery;
import de.muspellheim.activitysampling.contract.messages.queries.PreferencesQuery;
import de.muspellheim.activitysampling.frontend.MainWindowController;
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
      // TODO Add examples ergänzen
      eventStore = new MemoryEventStore();
      eventStore.addRecordedObserver(it -> System.out.println("Logged event: " + it));
      preferencesStore = new MemoryPreferencesStore();
    } else {
      preferencesStore = new PrefsPreferencesStore();
      var activityLogFile = preferencesStore.loadActivityLogFile();
      System.out.println("Save activity log in: " + activityLogFile);
      eventStore = new CsvEventStore(activityLogFile);
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
    var frontend = MainWindowController.create(primaryStage);

    // TODO Use CompletableFuture.supplyAsync(request).thenAcceptAsync(answer, Platform::runLater)
    frontend.setOnLogActivityCommand(
        command -> {
          var status = logActivityCommandHandler.handle(command);
          if (status instanceof Failure failure) {
            frontend.display(failure);
          }
          var result = activityLogQueryHandler.handle(new ActivityLogQuery());
          frontend.display(result);
        });
    frontend.setOnChangePeriodDurationCommand(
        command -> {
          var status = changePeriodDurationCommandHandler.handle(command);
          if (status instanceof Failure failure) {
            frontend.display(failure);
          }
          var result = preferencesQueryHandler.handle(new PreferencesQuery());
          frontend.display(result);
        });
    frontend.setOnChangeActivityLogFileCommand(
        command -> {
          var status = changeActivityLogFileCommandHandler.handle(command);
          if (status instanceof Failure failure) {
            frontend.display(failure);
          }
          var preferencesQueryResult = preferencesQueryHandler.handle(new PreferencesQuery());
          frontend.display(preferencesQueryResult);
          // FIXME Folgendes hat mal das Activity-Log neu geladen, aber jetzt nicht mehr, seit
          //  Replay nur noch im Konstruktor verwendet wird
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
    // TODO Entferne Store Wrapper und erstelle Notification, für den Fall, dass File nicht
    //  schreibbar oder lesbar
    private final PreferencesStore preferencesStore;
    private String activityLogFile;

    public PreferencesStoreWrapper(PreferencesStore preferencesStore, String activityLogFile) {
      this.preferencesStore = preferencesStore;
      this.activityLogFile = activityLogFile;
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
    public String loadActivityLogFile() {
      return activityLogFile;
    }

    @Override
    public void saveActivityLogFile(String activityLogFile) {
      this.activityLogFile = activityLogFile;
    }
  }
}
