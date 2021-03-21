/*
 * Activity Sampling - Frontend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.frontend;

import java.util.ResourceBundle;
import javafx.fxml.FXMLLoader;

class ViewControllerFactory {
  private final FXMLLoader loader;

  ViewControllerFactory(Class<?> controllerType) {
    var viewFile = getViewFilename(controllerType);
    var location = controllerType.getResource(viewFile);
    try {
      var resources = ResourceBundle.getBundle("ActivitySampling");
      loader = new FXMLLoader(location, resources);
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
