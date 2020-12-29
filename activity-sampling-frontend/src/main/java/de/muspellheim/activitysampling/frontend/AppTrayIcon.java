/*
 * Activity Sampling - Frontend
 * Copyright (c) 2020 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.frontend;

import de.muspellheim.activitysampling.contract.messages.commands.LogActivityCommand;
import de.muspellheim.activitysampling.contract.messages.notifications.PeriodEndedNotification;
import java.awt.AWTException;
import java.awt.EventQueue;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.util.function.Consumer;
import lombok.Getter;
import lombok.Setter;

public class AppTrayIcon {
  @Getter @Setter LogActivityCommand lastActivity;
  @Getter @Setter private Consumer<LogActivityCommand> onLogActivityCommand;

  private SystemTray tray;
  private TrayIcon trayIcon;

  public AppTrayIcon() {
    if (!SystemTray.isSupported()) {
      System.out.println("System tray is not supported on this platform");
      return;
    }

    tray = SystemTray.getSystemTray();
  }

  public void display(PeriodEndedNotification notification) {
    if (tray == null) {
      return;
    }

    if (trayIcon == null) {
      var url = getClass().getResource("app.png");
      var image = Toolkit.getDefaultToolkit().getImage(url);
      trayIcon = new TrayIcon(image);
      if (lastActivity != null) {
        String s = lastActivity.getActivity();
        if (lastActivity.getTags() != null) {
          s = "[" + lastActivity.getTags() + "] " + s;
        }
        MenuItem menuItem = new MenuItem(s);
        menuItem.addActionListener(
            it -> {
              if (onLogActivityCommand == null) {
                return;
              }

              onLogActivityCommand.accept(lastActivity);
            });

        var menu = new PopupMenu();
        menu.add(menuItem);
        trayIcon.setPopupMenu(menu);
      }

      try {
        tray.add(trayIcon);
      } catch (AWTException e) {
        System.err.println(e.toString());
      }
    }

    trayIcon.displayMessage("What are you working on?", null, MessageType.NONE);
  }

  public void dispose() {
    if (tray == null) {
      return;
    }

    EventQueue.invokeLater(
        () -> {
          tray.remove(trayIcon);
          trayIcon = null;
        });
  }
}
