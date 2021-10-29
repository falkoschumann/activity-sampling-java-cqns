/*
 * Activity Sampling - Application
 * Copyright (c) 2020 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling;

import de.muspellheim.activitysampling.backend.EventStore;
import de.muspellheim.activitysampling.backend.PreferencesRepository;
import de.muspellheim.activitysampling.backend.adapters.CsvEventStore;
import de.muspellheim.activitysampling.backend.adapters.MemoryEventStore;
import de.muspellheim.activitysampling.backend.adapters.MemoryPreferencesRepository;
import de.muspellheim.activitysampling.backend.adapters.PrefsPreferencesRepository;
import de.muspellheim.activitysampling.backend.adapters.SystemClock;
import de.muspellheim.activitysampling.frontend.MainWindowController;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

public class App extends Application {
  private PreferencesRepository preferencesRepository;
  private EventStore eventStore;

  public static void main(String[] args) {
    Application.launch(args);
  }

  @Override
  public void init() {
    if (getParameters().getUnnamed().contains("--demo")) {
      System.setProperty("activitysampling.demo", "true");
      eventStore = new MemoryEventStore().addExamples();
      preferencesRepository = new MemoryPreferencesRepository().addExamples();
    } else {
      eventStore = new CsvEventStore();
      preferencesRepository = new PrefsPreferencesRepository();
    }
  }

  @Override
  public void start(Stage primaryStage) {
    var systemClock = new SystemClock();
    var requestHandler = new RequestHandler(eventStore, preferencesRepository);
    var frontend = MainWindowController.create(primaryStage);

    frontend.setOnChangeMainWindowBoundsCommand(handle(requestHandler::handle, frontend::display));
    frontend.setOnChangePreferencesCommand(
        handle(requestHandler::handle, frontend::display, frontend::display));
    frontend.setOnLogActivityCommand(
        handle(requestHandler::handle, frontend::display, frontend::display));
    frontend.setOnActivityLogQuery(
        handle(requestHandler::handle, frontend::display, frontend::display));
    frontend.setOnMainWindowBoundsQuery(
        handle(requestHandler::handle, frontend::display, frontend::display));
    frontend.setOnPreferencesQuery(
        handle(requestHandler::handle, frontend::display, frontend::display));
    frontend.setOnTimeReportQuery(
        handle(requestHandler::handle, frontend::display, frontend::display));
    systemClock.setOnClockTickedNotification(handle(requestHandler::handle, frontend::display));
    requestHandler.setOnPeriodProgressedNotification(
        n -> Platform.runLater(() -> frontend.display(n)));
    requestHandler.setOnPeriodEndedNotification(n -> Platform.runLater(() -> frontend.display(n)));

    frontend.run();
    systemClock.run();
  }

  private static <I> Consumer<I> handle(Consumer<I> handler, Consumer<Throwable> onError) {
    return request ->
        CompletableFuture.runAsync(() -> handler.accept(request))
            .exceptionallyAsync(
                exception -> {
                  onError.accept(exception);
                  return null;
                },
                Platform::runLater);
  }

  private static <I, O> Consumer<I> handle(
      Function<I, O> handler, Consumer<O> onSuccess, Consumer<Throwable> onError) {
    return request ->
        CompletableFuture.supplyAsync(() -> handler.apply(request))
            .whenCompleteAsync(
                (response, exception) -> {
                  if (response != null) {
                    onSuccess.accept(response);
                  } else if (exception != null) {
                    onError.accept(exception);
                  }
                },
                Platform::runLater);
  }
}
