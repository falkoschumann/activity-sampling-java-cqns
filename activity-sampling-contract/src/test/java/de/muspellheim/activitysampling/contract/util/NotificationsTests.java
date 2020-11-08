/*
 * Activity Sampling - Contract
 * Copyright (c) 2020 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.contract.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class NotificationsTests {
  private List<String> stringEvents;
  private List<Integer> integerEvents;
  private List<Number> numberEvents;
  private List<Number> doubleEvents;

  @BeforeEach
  void setUp() {
    stringEvents = new ArrayList<>();
    integerEvents = new ArrayList<>();
    numberEvents = new ArrayList<>();
    doubleEvents = new ArrayList<>();
  }

  private void consumeString(String event) {
    stringEvents.add(event);
  }

  private void consumeInt(int event) {
    integerEvents.add(event);
  }

  private void consumeNumber(Number event) {
    numberEvents.add(event);
  }

  private void consumeDouble(Number event) {
    doubleEvents.add(event);
  }

  @Test
  void subscribe_EventTypeNull() {
    assertThrows(
        NullPointerException.class,
        () -> {
          Notifications notfications = Notifications.getDefault();
          notfications.subscribe(null, this::consumeString);
        });
  }

  @Test
  void subscribe_SubscriberNull() {
    assertThrows(
        NullPointerException.class,
        () -> {
          Notifications notfications = Notifications.getDefault();
          notfications.subscribe(String.class, null);
        });
  }

  @Test
  void unsubscribe_EventTypeNull() {
    assertThrows(
        NullPointerException.class,
        () -> {
          Notifications notfications = Notifications.getDefault();
          notfications.unsubscribe(null, this::consumeString);
        });
  }

  @Test
  void unsubscribe_SubscriberNull() {
    assertThrows(
        NullPointerException.class,
        () -> {
          Notifications notfications = Notifications.getDefault();
          notfications.unsubscribe(null);
        });
  }

  @Test()
  void publish_EventNull() {
    assertThrows(
        NullPointerException.class,
        () -> {
          Notifications notifications = Notifications.getDefault();

          notifications.publish(null);
        });
  }

  @Test
  void publish() {
    Notifications notfications = Notifications.getDefault();
    notfications.subscribe(String.class, this::consumeString);
    notfications.subscribe(Integer.class, this::consumeInt);

    notfications.publish("Foo");
    notfications.publish(42);
    notfications.publish("Bar");

    assertEquals(List.of("Foo", "Bar"), stringEvents, "string events");
    assertEquals(List.of(42), integerEvents, "integer events");
  }

  @Test
  void unsubscribe() {
    Notifications notfications = Notifications.getDefault();
    Consumer<String> subscriber = this::consumeString;
    notfications.subscribe(String.class, subscriber);

    notfications.publish("Foo");
    notfications.unsubscribe(subscriber);
    notfications.publish("Bar");

    assertEquals(List.of("Foo"), stringEvents);
  }

  @Test
  void eventTypeHierarchy() {
    Notifications notfications = Notifications.getDefault();
    Consumer<Integer> intSubscriber = this::consumeInt;
    notfications.subscribe(Integer.class, intSubscriber);
    Consumer<Number> numberSubscriber = this::consumeNumber;
    notfications.subscribe(Number.class, numberSubscriber);
    Consumer<Number> doubleSubscriber = this::consumeDouble;
    notfications.subscribe(Double.class, doubleSubscriber);

    notfications.publish(0.815);
    notfications.publish(42);
    notfications.unsubscribe(Number.class, numberSubscriber);
    notfications.unsubscribe(Number.class, doubleSubscriber);
    notfications.publish(2.718);
    notfications.unsubscribe(Integer.class, intSubscriber);
    notfications.publish(7);

    assertEquals(List.of(42), integerEvents, "integer events");
    assertEquals(List.of(0.815, 42), numberEvents, "number events");
    assertEquals(List.of(0.815), doubleEvents, "double events");
  }
}
