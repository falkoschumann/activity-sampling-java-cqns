/*
 * Activity Sampling - Frontend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.frontend;

import java.net.URL;
import javafx.fxml.FXMLLoader;

class ViewControllerFactory {
  private final FXMLLoader loader;

  ViewControllerFactory(Class<?> controllerType) {
    var filename = getViewFilename(controllerType);
    URL location = controllerType.getResource(filename);
    try {
      loader = new FXMLLoader(location);
      loader.load();
    } catch (Exception e) {
      throw new IllegalArgumentException("Can not load view from " + location, e);
    }
  }

  <T> T getView() {
    return loader.getRoot();
  }

  <T> T getController() {
    return loader.getController();
  }

  private static String getViewFilename(Class<?> controllerType) {
    String classname = controllerType.getName();
    if (classname.endsWith("Controller")) {
      classname = classname.substring(0, classname.length() - "Controller".length());
    }
    return "/" + classname.replace(".", "/") + ".fxml";
  }
}
