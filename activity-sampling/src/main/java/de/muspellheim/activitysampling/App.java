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
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import javafx.application.Application;
import javafx.application.Platform;
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

    var activityLogQueryProcessor =
        queryProcessor(activityLogQueryHandler::handle, frontend::display);
    frontend.setOnLogActivityCommand(
        commandProcessor(
            logActivityCommandHandler::handle,
            () -> activityLogQueryProcessor.accept(new ActivityLogQuery()),
            frontend::display));
    var preferencesQueryProcessor =
        queryProcessor(preferencesQueryHandler::handle, frontend::display);
    frontend.setOnChangePeriodDurationCommand(
        commandProcessor(
            changePeriodDurationCommandHandler::handle,
            () -> preferencesQueryProcessor.accept(new PreferencesQuery()),
            frontend::display));
    frontend.setOnChangeActivityLogFileCommand(
        commandProcessor(
            changeActivityLogFileCommandHandler::handle,
            () -> {
              preferencesQueryProcessor.accept(new PreferencesQuery());
              // FIXME Folgendes hat mal das Activity-Log neu geladen, aber jetzt nicht  mehr, seit
              //  Replay nur noch im Konstruktor verwendet wird
              activityLogQueryProcessor.accept(new ActivityLogQuery());
            },
            frontend::display));
    frontend.setOnPreferencesQuery(preferencesQueryProcessor);
    frontend.setOnActivityLogQuery(activityLogQueryProcessor);
    frontend.setOnWorkingHoursTodayQuery(
        queryProcessor(workingHoursTodayQueryHandler::handle, frontend::display));
    frontend.setOnWorkingHoursThisWeekQuery(
        queryProcessor(workingHoursThisWeekQueryHandler::handle, frontend::display));
    frontend.setOnWorkingHoursByActivityQuery(
        queryProcessor(workingHoursByActivityQueryHandler::handle, frontend::display));
    frontend.setOnWorkingHoursByNumberQuery(
        queryProcessor(workingHoursByNumberQueryHandler::handle, frontend::display));

    frontend.run();
  }

  private static <C, S> Consumer<C> commandProcessor(
      Function<C, S> commandHandler, Runnable onSuccess, Consumer<Failure> onFailure) {
    return command ->
        CompletableFuture.supplyAsync(() -> commandHandler.apply(command))
            .thenAcceptAsync(
                status -> {
                  if (status instanceof Failure failure) {
                    onFailure.accept(failure);
                  } else {
                    onSuccess.run();
                  }
                },
                Platform::runLater);
  }

  private static void noOperation() {}

  private static <Q, R> Consumer<Q> queryProcessor(
      Function<Q, R> queryHandler, Consumer<R> projector) {
    return query ->
        CompletableFuture.supplyAsync(() -> queryHandler.apply(query))
            .thenAcceptAsync(projector, Platform::runLater);
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
