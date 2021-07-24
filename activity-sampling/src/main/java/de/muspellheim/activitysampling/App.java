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
import de.muspellheim.activitysampling.backend.messagehandlers.ChangePreferencesCommandHandler;
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
      System.setProperty("demoMode", "true");
      preferencesStore = new MemoryPreferencesStore().addExamples();
      eventStore = new MemoryEventStore().addExamples();
    } else {
      preferencesStore = new PrefsPreferencesStore();
      eventStore = new CsvEventStore();
    }
  }

  @Override
  public void start(Stage primaryStage) {
    var logActivityCommandHandler = new LogActivityCommandHandler(eventStore);
    var changePreferencesCommandHandler = new ChangePreferencesCommandHandler(preferencesStore);
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
    frontend.setOnChangePreferencesCommand(
        commandProcessor(
            changePreferencesCommandHandler::handle,
            () -> preferencesQueryProcessor.accept(new PreferencesQuery()),
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
}
