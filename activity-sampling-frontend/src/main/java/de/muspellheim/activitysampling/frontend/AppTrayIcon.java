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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import lombok.Getter;
import lombok.Setter;

class AppTrayIcon {
  @Getter @Setter private Consumer<Activity> onActivitySelected;

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

  void display(List<Activity> activities) {
    // TODO Extract LastActivitiesQuery
    var lastActivities = new ArrayList<Activity>();
    activities.forEach(
        it -> {
          var exists =
              lastActivities.stream()
                  .filter(
                      i ->
                          Objects.equals(it.getActivity(), i.getActivity())
                              && Objects.equals(it.getTags(), i.getTags()))
                  .findFirst();
          if (exists.isPresent()) {
            return;
          }

          lastActivities.add(it);

          if (lastActivities.size() == 11) {
            lastActivities.remove(0);
          }
        });
    Collections.reverse(lastActivities);
    PopupMenu menu = createPopupMenu(lastActivities);
    trayIcon.setPopupMenu(menu);
  }

  private PopupMenu createPopupMenu(List<Activity> activities) {
    var menu = new PopupMenu();
    var stringConverter = new ActivityStringConverter();
    activities.forEach(
        it -> {
          MenuItem item = new MenuItem(stringConverter.toString(it));
          item.addActionListener(e -> handleActivitySelected(it));
          menu.add(item);
        });
    return menu;
  }

  private void handleActivitySelected(Activity it) {
    if (onActivitySelected == null) {
      return;
    }

    onActivitySelected.accept(it);
  }
}
