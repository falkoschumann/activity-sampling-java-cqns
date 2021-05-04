/*
 * Activity Sampling - Frontend
 * Copyright (c) 2020 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.frontend;

import de.muspellheim.activitysampling.contract.data.Activity;
import java.awt.AWTException;
import java.awt.EventQueue;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.util.List;
import java.util.function.Consumer;
import lombok.Getter;
import lombok.Setter;

class AppTrayIcon {
  @Getter @Setter private Consumer<Activity> onLogActivity;

  private TrayIcon trayIcon;

  AppTrayIcon() {
    if (!SystemTray.isSupported()) {
      System.out.println("System tray is not supported on this platform");
      return;
    }

    var url = getClass().getResource("app.png");
    var image = Toolkit.getDefaultToolkit().getImage(url);
    trayIcon = new TrayIcon(image);
  }

  public void setRecentActivities(List<Activity> recentActivities) {
    if (!SystemTray.isSupported()) {
      return;
    }

    EventQueue.invokeLater(
        () -> {
          var stringConverter = new ActivityStringConverter();
          var menu = new PopupMenu();
          recentActivities.forEach(
              it -> {
                MenuItem item = new MenuItem(stringConverter.toString(it));
                item.addActionListener(e -> onLogActivity.accept(it));
                menu.add(item);
              });
          trayIcon.setPopupMenu(menu);
        });
  }

  void show() {
    if (!SystemTray.isSupported()) {
      return;
    }

    EventQueue.invokeLater(
        () -> {
          var tray = SystemTray.getSystemTray();
          if (!List.of(tray.getTrayIcons()).contains(trayIcon)) {
            try {
              tray.add(trayIcon);
            } catch (AWTException e) {
              System.err.println("Can not add icon to system tray: " + e);
            }
          }

          trayIcon.displayMessage("What are you working on?", null, MessageType.NONE);
        });
  }

  void hide() {
    if (!SystemTray.isSupported()) {
      return;
    }

    EventQueue.invokeLater(
        () -> {
          var tray = SystemTray.getSystemTray();
          tray.remove(trayIcon);
        });
  }
}
