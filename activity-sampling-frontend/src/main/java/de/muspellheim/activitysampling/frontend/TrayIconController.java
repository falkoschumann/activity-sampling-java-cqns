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

class TrayIconController {
  @Getter @Setter private Consumer<Activity> onLogActivitySelected;

  private TrayIcon trayIcon;

  TrayIconController() {
    if (!SystemTray.isSupported()) {
      System.out.println("System tray is not supported on this platform");
      return;
    }

    var url = getClass().getResource("tray-icon.png");
    var image = Toolkit.getDefaultToolkit().getImage(url);
    trayIcon = new TrayIcon(image);
  }

  void setRecentActivities(List<Activity> recentActivities) {
    if (!SystemTray.isSupported()) {
      return;
    }

    EventQueue.invokeLater(
        () -> {
          var menu = new PopupMenu();
          var stringConverter = new ActivityStringConverter();
          recentActivities.forEach(
              it -> {
                MenuItem item = new MenuItem(stringConverter.toString(it));
                item.addActionListener(e -> onLogActivitySelected.accept(it));
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
          var trayIcons = List.of(tray.getTrayIcons());
          if (!trayIcons.contains(trayIcon)) {
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
