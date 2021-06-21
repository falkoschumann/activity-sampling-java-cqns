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

class TrayIconController {
  private static final boolean DISABLED = true;
  @Getter @Setter private Consumer<String> onActivitySelected;

  private TrayIcon trayIcon;

  TrayIconController() {
    if (DISABLED || !SystemTray.isSupported()) {
      System.out.println("System tray is not supported on this platform");
      return;
    }

    var url = getClass().getResource("tray-icon.png");
    var image = Toolkit.getDefaultToolkit().getImage(url);
    trayIcon = new TrayIcon(image);
  }

  void setRecent(List<String> value) {
    if (DISABLED) {
      System.out.println("TrayIconViewController::setRecent()");
      return;
    }
    if (!SystemTray.isSupported()) {
      return;
    }

    EventQueue.invokeLater(
        () -> {
          var menu = new PopupMenu();
          value.forEach(
              it -> {
                MenuItem menuItem = new MenuItem(it);
                menuItem.addActionListener(e -> onActivitySelected.accept(it));
                menu.add(menuItem);
              });
          trayIcon.setPopupMenu(menu);
        });
  }

  void show() {
    if (DISABLED) {
      System.out.println("TrayIconViewController::show()");
      return;
    }
    if (!SystemTray.isSupported()) {
      return;
    }

    EventQueue.invokeLater(
        () -> {
          var tray = SystemTray.getSystemTray();
          var missingIconInTray = !List.of(tray.getTrayIcons()).contains(trayIcon);
          if (missingIconInTray) {
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
    if (DISABLED) {
      System.out.println("TrayIconViewController::hide()");
      return;
    }
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