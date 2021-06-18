/*
 * Activity Sampling - Frontend
 * Copyright (c) 2020 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.frontend;

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

class TrayIconViewController {
  @Getter @Setter private Consumer<String> onActivitySelected;

  private TrayIcon trayIcon;

  TrayIconViewController() {
    if (!SystemTray.isSupported()) {
      System.out.println("System tray is not supported on this platform");
      return;
    }

    var url = getClass().getResource("tray-icon.png");
    var image = Toolkit.getDefaultToolkit().getImage(url);
    trayIcon = new TrayIcon(image);
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

  void display(List<String> recentActivities) {
    EventQueue.invokeLater(
        () -> {
          var menu = new PopupMenu();
          recentActivities.forEach(
              it -> {
                MenuItem item = new MenuItem(it);
                item.addActionListener(e -> onActivitySelected.accept(it));
                menu.add(item);
              });
          trayIcon.setPopupMenu(menu);
        });
  }
}
