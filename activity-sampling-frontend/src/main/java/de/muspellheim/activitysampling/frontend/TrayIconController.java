/*
 * Activity Sampling - Frontend
 * Copyright (c) 2020 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.frontend;

import de.muspellheim.activitysampling.contract.data.ActivityTemplate;
import java.awt.AWTException;
import java.awt.EventQueue;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import lombok.Getter;
import lombok.Setter;

class TrayIconController {
  @Getter @Setter private Consumer<ActivityTemplate> onActivitySelected;

  private final ResourceBundle resources = ResourceBundle.getBundle("ActivitySampling");

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

  void setRecent(List<ActivityTemplate> value) {
    if (!SystemTray.isSupported()) {
      return;
    }

    EventQueue.invokeLater(
        () -> {
          var menu = new PopupMenu();
          var stringConverter = new ActivityTemplateStringConverter();
          value.forEach(
              it -> {
                MenuItem menuItem = new MenuItem(stringConverter.toString(it));
                menuItem.addActionListener(e -> onActivitySelected.accept(it));
                menu.add(menuItem);
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
          var missingIconInTray = !List.of(tray.getTrayIcons()).contains(trayIcon);
          if (missingIconInTray) {
            try {
              tray.add(trayIcon);
            } catch (AWTException e) {
              System.err.println("Can not add icon to system tray: " + e);
            }
          }

          trayIcon.displayMessage(resources.getString("trayIcon.message"), null, MessageType.NONE);
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
