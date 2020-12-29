/*
 * Activity Sampling - Frontend
 * Copyright (c) 2020 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.frontend;

import de.muspellheim.activitysampling.contract.messages.notifications.PeriodEndedNotification;
import java.awt.AWTException;
import java.awt.EventQueue;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;

public class AppTrayIcon {
  private SystemTray tray;
  private TrayIcon trayIcon;

  public AppTrayIcon() {
    if (!SystemTray.isSupported()) {
      System.out.println("System tray is not supported on this platform");
      return;
    }

    tray = SystemTray.getSystemTray();
    var url = getClass().getResource("app.png");
    var image = Toolkit.getDefaultToolkit().getImage(url);
    trayIcon = new TrayIcon(image);
    trayIcon.addActionListener(e -> System.out.println("Action: " + e));
    try {
      tray.add(trayIcon);
    } catch (AWTException e) {
      throw new IllegalStateException(e);
    }
  }

  public void display(PeriodEndedNotification notification) {
    if (trayIcon == null) {
      return;
    }

    trayIcon.displayMessage("What are you working on?", null, MessageType.NONE);
  }

  public void dispose() {
    if (trayIcon == null) {
      return;
    }

    EventQueue.invokeLater(() -> tray.remove(trayIcon));
  }
}
