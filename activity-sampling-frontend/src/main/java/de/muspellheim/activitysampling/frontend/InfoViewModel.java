/*
 * Activity Sampling - Frontend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.frontend;

import java.net.URL;
import java.util.Properties;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class InfoViewModel {
  private final StringProperty iconUrl = new SimpleStringProperty("");
  private final StringProperty title = new SimpleStringProperty("");
  private final StringProperty version = new SimpleStringProperty("");
  private final StringProperty copyright = new SimpleStringProperty("");

  public InfoViewModel(URL iconUrl, Properties appProperties) {
    this.iconUrl.set(iconUrl.toString());
    title.set(appProperties.getProperty("title"));
    version.set(appProperties.getProperty("version"));
    copyright.set(appProperties.getProperty("copyright"));
  }

  public StringProperty iconUrlProperty() {
    return iconUrl;
  }

  public StringProperty titleProperty() {
    return title;
  }

  public StringProperty versionProperty() {
    return version;
  }

  public StringProperty copyrightProperty() {
    return copyright;
  }
}
