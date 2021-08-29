/*
 * Activity Sampling - Frontend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.frontend;

import java.lang.reflect.Method;

class ComponentReference<T> {
  private final Class<?> clazz;
  private final String name;
  private Method accessor;
  private boolean reflected = false;

  ComponentReference(Class<?> clazz, String name) {
    this.clazz = clazz;
    this.name = name;
  }

  @SuppressWarnings("unchecked")
  T get(Object bean) {
    reflect();
    try {
      return (T) accessor.invoke(bean);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private void reflect() {
    if (!reflected) {
      reflected = true;
      try {
        accessor = clazz.getMethod(name);
      } catch (Exception e) {
        System.err.println("Failed to introspect component " + name);
      }
    }
  }
}
