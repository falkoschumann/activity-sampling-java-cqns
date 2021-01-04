/*
 * Activity Sampling - Frontend
 * Copyright (c) 2020 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.frontend;

import de.muspellheim.activitysampling.contract.messages.commands.LogActivityCommand;
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
  @Getter @Setter private LogActivityCommand lastCommand;
  @Getter @Setter private Consumer<LogActivityCommand> onLogActivityCommand; // TODO Mit Todo ersetzen

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

  void show() {
    if (!SystemTray.isSupported()) {
      return;
    }

    var tray = SystemTray.getSystemTray();
    if (lastCommand != null) {
      PopupMenu menu = createPopupMenu();
      trayIcon.setPopupMenu(menu);
    }
    if (!List.of(tray.getTrayIcons()).contains(trayIcon)) {
      try {
        tray.add(trayIcon);
      } catch (AWTException e) {
        System.err.println(e.toString());
      }
    }

    trayIcon.displayMessage("What are you working on?", null, MessageType.NONE);
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

  private PopupMenu createPopupMenu() {
    String label = lastCommand.getActivity();
    if (lastCommand.getTags() != null) {
      label = "[" + lastCommand.getTags() + "] " + label;
    }
    MenuItem item = new MenuItem(label);
    item.addActionListener(
        it -> {
          if (onLogActivityCommand == null) {
            return;
          }

          onLogActivityCommand.accept(lastCommand);
        });

    var menu = new PopupMenu();
    menu.add(item);
    return menu;
  }
}
