/*
 * Activity Sampling - Frontend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.frontend;

import de.muspellheim.activitysampling.contract.MessageHandling;
import java.net.URL;
import java.util.Properties;

public class ViewModelFactory {
  private static MessageHandling messageHandling;
  private static URL iconUrl;
  private static Properties appProperties;

  private static ActivitySamplingViewModel activitySamplingViewModel;
  private static InfoViewModel infoViewModel;

  public static void initMessageHandling(MessageHandling messageHandling) {
    ViewModelFactory.messageHandling = messageHandling;
  }

  public static void initIconUrl(URL iconUrl) {
    ViewModelFactory.iconUrl = iconUrl;
  }

  public static void initAppProperties(Properties appProperties) {
    ViewModelFactory.appProperties = appProperties;
  }

  public static ActivitySamplingViewModel getActivitySamplingViewModel() {
    if (activitySamplingViewModel == null) {
      activitySamplingViewModel = new ActivitySamplingViewModel(messageHandling);
    }
    return activitySamplingViewModel;
  }

  public static InfoViewModel getInfoViewModel() {
    if (infoViewModel == null) {
      infoViewModel = new InfoViewModel(iconUrl, appProperties);
    }
    return infoViewModel;
  }
}
