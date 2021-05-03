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
import javafx.beans.InvalidationListener;

class AppTrayIcon {
  private final ActivitySamplingViewModel viewModel = new ActivitySamplingViewModel();

  private TrayIcon trayIcon;

  AppTrayIcon() {
    if (!SystemTray.isSupported()) {
      System.out.println("System tray is not supported on this platform");
      return;
    }

    var url = getClass().getResource("app.png");
    var image = Toolkit.getDefaultToolkit().getImage(url);
    trayIcon = new TrayIcon(image);

    viewModel
        .formDisabledProperty()
        .addListener(((observable, oldValue, newValue) -> showOrHide(newValue)));
    viewModel
        .getRecentActivities()
        .addListener((InvalidationListener) observable -> updateMenuItems());
  }

  private void updateMenuItems() {
    EventQueue.invokeLater(
        () -> {
          var menu = new PopupMenu();
          viewModel
              .getRecentActivities()
              .forEach(
                  it -> {
                    MenuItem item = new MenuItem(it);
                    item.addActionListener(e -> viewModel.logActivity(it));
                    menu.add(item);
                  });
          trayIcon.setPopupMenu(menu);
        });
  }

  private void showOrHide(Boolean newValue) {
    EventQueue.invokeLater(
        () -> {
          var tray = SystemTray.getSystemTray();
          if (newValue) {
            tray.remove(trayIcon);
          } else {
            if (!List.of(tray.getTrayIcons()).contains(trayIcon)) {
              try {
                tray.add(trayIcon);
              } catch (AWTException e) {
                System.err.println("Can not add icon to system tray: " + e.toString());
              }
            }

            trayIcon.displayMessage("What are you working on?", null, MessageType.NONE);
          }
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
